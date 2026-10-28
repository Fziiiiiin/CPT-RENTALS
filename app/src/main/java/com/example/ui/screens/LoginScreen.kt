package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MidnightIndigo
import com.example.ui.theme.MidnightIndigoLight
import com.example.ui.theme.PalePurpleBg
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CptViewModel

@Composable
fun LoginScreen(
    viewModel: CptViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isForgotView by remember { mutableStateOf(false) }
    var forgotUsername by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background blur element
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(RoyalPurple.copy(alpha = 0.08f))
                .blur(50.dp)
        )

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Brand Header
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MidnightIndigo, MidnightIndigoLight)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "CPT Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Staff Portal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "EVENT RENTALS CRM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalPurple,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isForgotView) {
                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        placeholder = { Text("admin or warehouse") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "User", tint = TextSecondary)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_username_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password", tint = TextSecondary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.login(username, password) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Demo Credentials Helper & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PalePurpleBg)
                                    .clickable {
                                        username = "admin"
                                        password = "admin123"
                                        viewModel.quickLogin(UserRole.ADMIN)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("demo_admin_button")
                            ) {
                                Text(
                                    text = "Admin",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEDE9FE))
                                    .clickable {
                                        username = "warehouse"
                                        password = "ware123"
                                        viewModel.quickLogin(UserRole.WAREHOUSE)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("demo_warehouse_button")
                            ) {
                                Text(
                                    text = "Warehouse",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MidnightIndigoLight
                                )
                            }
                        }

                        Text(
                            text = "Forgot Password?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple,
                            modifier = Modifier
                                .clickable { isForgotView = true }
                                .padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign In Button
                    Button(
                        onClick = { viewModel.login(username, password) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_submit_button")
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    // Forgot Password View
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Reset Password",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Enter your username to receive reset instructions.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = forgotUsername,
                            onValueChange = { forgotUsername = it },
                            label = { Text("Username") },
                            placeholder = { Text("e.g. admin") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalPurple,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_username_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { isForgotView = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back to Login", color = TextSecondary)
                            }
                            Button(
                                onClick = {
                                    if (forgotUsername.isNotBlank()) {
                                        viewModel.showNotification("Password Reset Sent", "Password reset instructions sent to $forgotUsername.")
                                        isForgotView = false
                                        forgotUsername = ""
                                    } else {
                                        viewModel.showNotification("Error", "Please enter a valid username.", isError = true)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reset")
                            }
                        }
                    }
                }
            }
        }
    }
}
