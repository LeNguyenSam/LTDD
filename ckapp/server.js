// server.js
const express = require('express');
const mysql = require('mysql2/promise');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

const SECRET_KEY = 'SamLe';

// Kết nối MySQL
const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'iot_sensor_db',
    waitForConnections: true,
    connectionLimit: 10,
});

// ==================== AUTH ====================

// 1. API ĐĂNG KÝ
app.post('/api/register', async (req, res) => {
    const { username, email, password } = req.body;

    if (!username || !email || !password) {
        return res.status(400).json({ success: false, error: 'Vui lòng điền đầy đủ thông tin' });
    }

    try {
        const lowerEmail = email.trim().toLowerCase();

        const [existingUser] = await pool.query(
            'SELECT id FROM users WHERE gmail = ? OR username = ?',
            [lowerEmail, username]
        );

        if (existingUser.length > 0) {
            return res.status(400).json({ success: false, error: 'Email hoặc Tên tài khoản đã tồn tại' });
        }

        const hash = await bcrypt.hash(password, 10);

        await pool.query(
            'INSERT INTO users (username, gmail, password_hash, is_active) VALUES (?, ?, ?, 1)',
            [username, lowerEmail, hash]
        );

        res.status(201).json({ success: true, message: 'Đăng ký thành công!' });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: 'Lỗi máy chủ khi đăng ký' });
    }
});

// 2. API ĐĂNG NHẬP
app.post('/api/login', async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ success: false, error: 'Vui lòng điền đầy đủ thông tin' });
    }

    try {
        const lowerEmail = email.trim().toLowerCase();

        const [rows] = await pool.query(
            'SELECT * FROM users WHERE gmail = ? AND is_active = 1',
            [lowerEmail]
        );

        if (rows.length === 0) {
            return res.status(401).json({ success: false, error: 'Email hoặc mật khẩu không chính xác' });
        }

        const user = rows[0];

        const match = await bcrypt.compare(password, user.password_hash);
        if (!match) {
            return res.status(401).json({ success: false, error: 'Email hoặc mật khẩu không chính xác' });
        }

        const token = jwt.sign(
            { id: user.id, email: user.gmail },
            SECRET_KEY,
            { expiresIn: '24h' }
        );

        pool.query('UPDATE users SET last_login_at = NOW() WHERE id = ?', [user.id])
            .catch(e => console.error('Lỗi cập nhật last_login_at:', e));

        res.json({
            success: true,
            token: token,
            user: {
                username: user.username,
                email: user.gmail,
                displayName: user.username
            }
        });

    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: 'Lỗi máy chủ khi đăng nhập' });
    }
});

// Middleware kiểm tra JWT
const authMiddleware = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) return res.status(401).json({ success: false, error: 'Không có token xác thực' });

    try {
        req.user = jwt.verify(token, SECRET_KEY);
        next();
    } catch (err) {
        return res.status(401).json({ success: false, error: 'Token không hợp lệ hoặc đã hết hạn' });
    }
};

// ==================== SENSOR DATA (TẤT CẢ) ====================

// Nhận dữ liệu từ thiết bị (ESP32...)
app.post('/api/sensors', async (req, res) => {
    const {
        gas, flame, temperature, humidity,
        pressure, light, uvIndex,
        device_code = 'NODE-001'
    } = req.body;

    try {
        const [dev] = await pool.query(
            'SELECT id FROM devices WHERE device_code = ?', [device_code]
        );
        if (!dev.length) {
            return res.status(400).json({ success: false, error: 'Device không tồn tại' });
        }

        const device_id = dev[0].id;

        // Tính alert_level cho gas (theo ngưỡng cố định mặc định)
        const gasAlertLevel = gas >= 600 ? 'danger' : gas >= 300 ? 'warning' : 'normal';

        await pool.query(
            'INSERT INTO sensor_gas (device_id, gas_ppm, alert_level) VALUES (?, ?, ?)',
            [device_id, gas, gasAlertLevel]
        );
        await pool.query(
            'INSERT INTO sensor_fire (device_id, fire_detected) VALUES (?, ?)',
            [device_id, flame ? 1 : 0]
        );
        await pool.query(
            'INSERT INTO sensor_dht (device_id, temperature_c, humidity_pct) VALUES (?, ?, ?)',
            [device_id, temperature, humidity]
        );
        await pool.query(
            'INSERT INTO sensor_pressure (device_id, pressure_hpa) VALUES (?, ?)',
            [device_id, pressure]
        );
        await pool.query(
            'INSERT INTO sensor_light (device_id, lux) VALUES (?, ?)',
            [device_id, light]
        );
        await pool.query(
            'INSERT INTO sensor_uv (device_id, uv_index) VALUES (?, ?)',
            [device_id, uvIndex]
        );

        res.json({ success: true, message: 'Dữ liệu cảm biến đã được lưu' });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: 'Lỗi khi lưu dữ liệu' });
    }
});

// Lấy dữ liệu mới nhất toàn bộ sensor
app.get('/api/sensors/latest', async (req, res) => {
    try {
        const [gas]   = await pool.query(`SELECT gas_ppm as gas FROM sensor_gas ORDER BY recorded_at DESC LIMIT 1`);
        const [fire]  = await pool.query(`SELECT fire_detected as flame FROM sensor_fire ORDER BY recorded_at DESC LIMIT 1`);
        const [dht]   = await pool.query(`SELECT temperature_c as temperature, humidity_pct as humidity FROM sensor_dht ORDER BY recorded_at DESC LIMIT 1`);
        const [press] = await pool.query(`SELECT pressure_hpa as pressure FROM sensor_pressure ORDER BY recorded_at DESC LIMIT 1`);
        const [lt]    = await pool.query(`SELECT lux as light FROM sensor_light ORDER BY recorded_at DESC LIMIT 1`);
        const [uv]    = await pool.query(`SELECT uv_index FROM sensor_uv ORDER BY recorded_at DESC LIMIT 1`);

        res.json({
            gas:         gas[0]?.gas ?? 0,
            flame:       fire[0]?.flame === 1,
            temperature: dht[0]?.temperature ?? 0,
            humidity:    dht[0]?.humidity ?? 0,
            pressure:    press[0]?.pressure ?? 0,
            light:       lt[0]?.light ?? 0,
            uvIndex:     uv[0]?.uv_index ?? 0,
            time: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: 'Lỗi lấy dữ liệu mới nhất' });
    }
});

// ==================== GAS SENSOR RIÊNG ====================

/**
 * POST /api/sensors/gas
 * Body: { gas_ppm, device_code?, threshold? }
 * - Lưu giá trị gas mới vào database
 * - alert_level được tính dựa trên threshold gửi từ Android
 *   (nếu không gửi threshold thì dùng mặc định 300)
 */
app.post('/api/sensors/gas', async (req, res) => {
    const {
        gas_ppm,
        device_code = 'NODE-001',
        threshold = 300   // ngưỡng do Android gửi lên (ppm)
    } = req.body;

    if (gas_ppm === undefined || gas_ppm === null) {
        return res.status(400).json({ success: false, error: 'Thiếu giá trị gas_ppm' });
    }

    const gasPpm = Number(gas_ppm);
    if (isNaN(gasPpm)) {
        return res.status(400).json({ success: false, error: 'gas_ppm phải là số' });
    }

    try {
        // Lấy device_id
        const [dev] = await pool.query(
            'SELECT id FROM devices WHERE device_code = ?', [device_code]
        );
        if (!dev.length) {
            return res.status(400).json({ success: false, error: `Device '${device_code}' không tồn tại` });
        }
        const device_id = dev[0].id;

        // Tính alert_level dựa trên threshold động từ Android
        let alert_level;
        if (gasPpm >= 600) {
            alert_level = 'danger';      // luôn nguy hiểm khi ≥ 600
        } else if (gasPpm >= threshold) {
            alert_level = 'warning';     // vượt ngưỡng người dùng đặt
        } else {
            alert_level = 'normal';
        }

        // Lưu vào DB
        await pool.query(
            'INSERT INTO sensor_gas (device_id, gas_ppm, alert_level) VALUES (?, ?, ?)',
            [device_id, gasPpm, alert_level]
        );

        res.json({
            success: true,
            message: 'Đã lưu dữ liệu khí gas',
            data: {
                gas_ppm: gasPpm,
                alert_level,
                threshold,
                recorded_at: new Date().toISOString()
            }
        });
    } catch (err) {
        console.error('Lỗi lưu gas:', err);
        res.status(500).json({ success: false, error: 'Lỗi máy chủ khi lưu gas' });
    }
});

/**
 * GET /api/sensors/gas/latest
 * Lấy bản ghi khí gas mới nhất từ database
 * Query param: ?threshold=300  (để server tính lại alert_level theo ngưỡng Android đặt)
 */
app.get('/api/sensors/gas/latest', async (req, res) => {
    const threshold = parseInt(req.query.threshold) || 300;

    try {
        const [rows] = await pool.query(`
            SELECT
                gas_ppm,
                alert_level,
                recorded_at
            FROM sensor_gas
            ORDER BY recorded_at DESC
            LIMIT 1
        `);

        if (!rows.length) {
            return res.json({
                gas_ppm: 0,
                alert_level: 'normal',
                threshold,
                is_exceeding_threshold: false,
                recorded_at: null
            });
        }

        const gasPpm = rows[0].gas_ppm;

        // Tính lại alert dựa trên threshold của Android
        let dynamicAlertLevel;
        if (gasPpm >= 600) {
            dynamicAlertLevel = 'danger';
        } else if (gasPpm >= threshold) {
            dynamicAlertLevel = 'warning';
        } else {
            dynamicAlertLevel = 'normal';
        }

        res.json({
            gas_ppm:              gasPpm,
            alert_level:          dynamicAlertLevel,   // tính theo threshold động
            alert_level_db:       rows[0].alert_level, // lưu trong DB (theo threshold lúc ghi)
            threshold,
            is_exceeding_threshold: gasPpm >= threshold,
            exceed_by:            Math.max(0, gasPpm - threshold),
            recorded_at:          rows[0].recorded_at,
            time: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
        });
    } catch (err) {
        console.error('Lỗi lấy gas:', err);
        res.status(500).json({ success: false, error: 'Lỗi máy chủ khi lấy dữ liệu gas' });
    }
});

/**
 * GET /api/sensors/gas/history
 * Lấy lịch sử khí gas (30 bản ghi gần nhất)
 * Query param: ?threshold=300
 */
app.get('/api/sensors/gas/history', async (req, res) => {
    const threshold = parseInt(req.query.threshold) || 300;
    const limit     = parseInt(req.query.limit) || 30;

    try {
        const [rows] = await pool.query(`
            SELECT
                gas_ppm,
                alert_level,
                recorded_at,
                DATE_FORMAT(recorded_at, '%H:%i') as time
            FROM sensor_gas
            ORDER BY recorded_at DESC
            LIMIT ?
        `, [limit]);

        // Tính lại alert_level theo threshold động
        const result = rows.map(row => ({
            gas_ppm:    row.gas_ppm,
            time:       row.time,
            recorded_at: row.recorded_at,
            alert_level: row.gas_ppm >= 600 ? 'danger'
                        : row.gas_ppm >= threshold ? 'warning'
                        : 'normal'
        }));

        res.json(result);
    } catch (err) {
        console.error('Lỗi lấy lịch sử gas:', err);
        res.status(500).json({ success: false, error: 'Lỗi máy chủ khi lấy lịch sử gas' });
    }
});

/**
 * GET /api/sensors/gas/stats
 * Thống kê khí gas: min, max, avg trong 1 giờ / 24 giờ
 */
app.get('/api/sensors/gas/stats', async (req, res) => {
    try {
        const [hour] = await pool.query(`
            SELECT
                MIN(gas_ppm) as min_ppm,
                MAX(gas_ppm) as max_ppm,
                ROUND(AVG(gas_ppm), 1) as avg_ppm,
                COUNT(*) as total_readings
            FROM sensor_gas
            WHERE recorded_at >= NOW() - INTERVAL 1 HOUR
        `);

        const [day] = await pool.query(`
            SELECT
                MIN(gas_ppm) as min_ppm,
                MAX(gas_ppm) as max_ppm,
                ROUND(AVG(gas_ppm), 1) as avg_ppm,
                COUNT(*) as total_readings
            FROM sensor_gas
            WHERE recorded_at >= NOW() - INTERVAL 24 HOUR
        `);

        const [alerts] = await pool.query(`
            SELECT COUNT(*) as alert_count
            FROM sensor_gas
            WHERE alert_level IN ('warning', 'danger')
              AND recorded_at >= NOW() - INTERVAL 24 HOUR
        `);

        res.json({
            last_hour: hour[0],
            last_24h:  day[0],
            alerts_24h: alerts[0].alert_count
        });
    } catch (err) {
        console.error('Lỗi lấy stats gas:', err);
        res.status(500).json({ success: false, error: 'Lỗi máy chủ khi lấy thống kê gas' });
    }
});

// ==================== LỊCH SỬ ĐẦY ĐỦ ====================

app.get('/api/sensors/history', async (req, res) => {
    try {
        const [rows] = await pool.query(`
            SELECT
                g.gas_ppm as gas,
                f.fire_detected as flame,
                d.temperature_c as temperature,
                d.humidity_pct as humidity,
                p.pressure_hpa as pressure,
                l.lux as light,
                u.uv_index,
                DATE_FORMAT(d.recorded_at, '%H:%i') as time
            FROM sensor_dht d
            LEFT JOIN sensor_gas g ON g.device_id = d.device_id
                AND g.recorded_at BETWEEN d.recorded_at AND d.recorded_at + INTERVAL 30 SECOND
            LEFT JOIN sensor_fire f ON f.device_id = d.device_id
                AND f.recorded_at BETWEEN d.recorded_at AND d.recorded_at + INTERVAL 30 SECOND
            LEFT JOIN sensor_pressure p ON p.device_id = d.device_id
                AND p.recorded_at BETWEEN d.recorded_at AND d.recorded_at + INTERVAL 30 SECOND
            LEFT JOIN sensor_light l ON l.device_id = d.device_id
                AND l.recorded_at BETWEEN d.recorded_at AND d.recorded_at + INTERVAL 30 SECOND
            LEFT JOIN sensor_uv u ON u.device_id = d.device_id
                AND u.recorded_at BETWEEN d.recorded_at AND d.recorded_at + INTERVAL 30 SECOND
            ORDER BY d.recorded_at DESC
            LIMIT 30
        `);

        res.json(rows);
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, error: 'Lỗi lấy lịch sử' });
    }
});

// ==================== START SERVER ====================

app.listen(3000, () => {
    console.log('🚀 Server IoT đang chạy tại http://localhost:3000');
    console.log('📡 Gas API endpoints:');
    console.log('   POST /api/sensors/gas          — Gửi dữ liệu gas (có threshold)');
    console.log('   GET  /api/sensors/gas/latest   — Gas mới nhất từ DB');
    console.log('   GET  /api/sensors/gas/history  — Lịch sử gas');
    console.log('   GET  /api/sensors/gas/stats    — Thống kê gas');
});