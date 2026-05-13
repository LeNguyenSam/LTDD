package com.example.ckapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── User & Auth State ───────────────────────────────────────────────────────
data class UserAccount(
    val username: String,
    val email: String,
    val displayName: String
)

object AuthState {
    var currentUser: UserAccount? by mutableStateOf(null)
    val isLoggedIn get() = currentUser != null

    private val store = mutableMapOf<String, Pair<String, UserAccount>>()

    fun login(email: String, password: String): Boolean {
        val entry = store[email.trim().lowercase()] ?: return false
        if (entry.first != password) return false
        currentUser = entry.second
        return true
    }

    fun register(username: String, email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        if (store.containsKey(key)) return false
        store[key] = Pair(password, UserAccount(username, email, username))
        return true
    }

    fun logout() {
        currentUser = null
    }
}

@Composable
fun AccountScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        AnimatedContent(
            targetState = AuthState.isLoggedIn,
            label = "Auth Transition"
        ) { loggedIn ->
            if (loggedIn) {
                ProfileContent(AuthState.currentUser!!)
            } else {
                AuthContent()
            }
        }
    }
}

@Composable
private fun AuthContent() {
    var isLogin by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(TealDim),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                null,
                tint = Teal,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = if (isLogin) "Đăng nhập" else "Đăng ký",
            color = TextPri,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(32.dp))

        if (isLogin) LoginForm() else RegisterForm { isLogin = true }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                text = if (isLogin) "Chưa có tài khoản? Đăng ký"
                else "Đã có tài khoản? Đăng nhập",
                color = Teal,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoginForm() {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        IotTextField(email, { email = it }, "Email", Icons.Default.Email)
        IotTextField(pass, { pass = it }, "Mật khẩu", Icons.Default.Lock, isPassword = true)

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { AuthState.login(email, pass) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Đăng nhập", color = BgDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RegisterForm(onSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        IotTextField(username, { username = it }, "Tên hiển thị", Icons.Default.Person)
        IotTextField(email, { email = it }, "Email", Icons.Default.Email)
        IotTextField(pass, { pass = it }, "Mật khẩu", Icons.Default.Lock, isPassword = true)

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (AuthState.register(username, email, pass)) onSuccess()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Tạo tài khoản", color = BgDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileContent(user: UserAccount) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(TealDim),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                null,
                tint = Teal,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = user.displayName,
            color = TextPri,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user.email,
            color = TextSec,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(60.dp))

        Button(
            onClick = { AuthState.logout() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Đăng xuất", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IotTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = Teal) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = BorderColor,
            focusedTextColor = TextPri,
            unfocusedTextColor = TextPri,
            focusedContainerColor = BgCard,
            unfocusedContainerColor = BgCard
        )
    )
}