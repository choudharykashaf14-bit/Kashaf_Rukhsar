/**
 * Android Jetpack Compose Authentication Architecture Bundle
 * Package: com.example.composeauth
 * Target: Android 15 (API 35) / Jetpack Compose BOM 2024.10.01
 */


================================================================================
// FILE: app/src/main/java/com/example/composeauth/ui/screens/LoginScreen.kt
// PURPOSE: Jetpack Compose Login screen with real-time validation and secure password visibility toggle.
================================================================================

package com.example.composeauth.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeauth.ui.components.PasswordTextField
import com.example.composeauth.viewmodel.AuthViewModel

/**
 * Android Jetpack Compose Login Screen
 * Features:
 * - Material Design 3 OutlinedTextField with floating label & leading icon
 * - Secure Password Visibility Toggle (VisualTransformation.None vs PasswordVisualTransformation)
 * - Real-time input validation & error state feedback
 * - "Remember Me" Checkbox & Biometric Prompt trigger
 * - Smooth animated state transitions
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onBiometricClick: () -> Unit = {}
) {
    val uiState by viewModel.loginUiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Icon & Title
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "App Security Key",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Sign in to access your secure account",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Email or Username Field with Live Validation
            OutlinedTextField(
                value = uiState.emailOrUsername,
                onValueChange = { viewModel.onLoginEmailChange(it) },
                label = { Text("Email address or username") },
                placeholder = { Text("alex@example.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email icon"
                    )
                },
                isError = uiState.emailError != null,
                supportingText = {
                    uiState.emailError?.let { errorText ->
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Secure Password Input with Visibility Toggle Composable
            PasswordTextField(
                value = uiState.password,
                onValueChange = { viewModel.onLoginPasswordChange(it) },
                label = "Password",
                isPasswordVisible = uiState.isPasswordVisible,
                onToggleVisibility = { viewModel.toggleLoginPasswordVisibility() },
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    viewModel.login(onSuccess = onLoginSuccess)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Remember Me & Forgot Password Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = { viewModel.onRememberMeChange(it) }
                    )
                    Text(
                        text = "Remember me",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = { /* Handle Forgot Password Sheet */ }
                ) {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action: Sign In Button with Ripple & Progress Indicator
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login(onSuccess = onLoginSuccess)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Biometric Authentication Shortcut
            OutlinedButton(
                onClick = onBiometricClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Quick Sign in with Biometrics")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Navigation Footer
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


================================================================================
// FILE: app/src/main/java/com/example/composeauth/ui/screens/RegisterScreen.kt
// PURPOSE: Jetpack Compose Registration screen with multi-field data validation, live password strength meter, and dual visibility toggles.
================================================================================

package com.example.composeauth.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeauth.ui.components.PasswordStrengthIndicator
import com.example.composeauth.ui.components.PasswordTextField
import com.example.composeauth.viewmodel.AuthViewModel

/**
 * Android Jetpack Compose Registration Screen
 * Features:
 * - Full Name, RFC-5322 Email, Phone Number, Password, and Confirm Password fields
 * - Secure Password Visibility Toggles on both sensitive fields
 * - Interactive Real-time Password Strength Meter (Weak / Fair / Good / Strong)
 * - Confirm password matching validator
 * - Terms of Service agreement checkbox
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.registerUiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Login"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join with us",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Please enter valid credentials to set up your account",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp, bottom = 20.dp)
            )

            // 1. Full Name Field
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onRegisterNameChange(it) },
                label = { Text("Full Legal Name") },
                placeholder = { Text("Sarah Connor") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Name")
                },
                isError = uiState.fullNameError != null,
                supportingText = {
                    uiState.fullNameError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Email Address Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onRegisterEmailChange(it) },
                label = { Text("Email Address") },
                placeholder = { Text("sarah@example.com") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                },
                isError = uiState.emailError != null,
                supportingText = {
                    uiState.emailError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Phone Number Field (Optional/Formatted)
            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = { viewModel.onRegisterPhoneChange(it) },
                label = { Text("Phone Number") },
                placeholder = { Text("+1 (555) 000-1234") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone")
                },
                isError = uiState.phoneError != null,
                supportingText = {
                    uiState.phoneError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Password Field with Visibility Toggle
            PasswordTextField(
                value = uiState.password,
                onValueChange = { viewModel.onRegisterPasswordChange(it) },
                label = "Create Password",
                isPasswordVisible = uiState.isPasswordVisible,
                onToggleVisibility = { viewModel.toggleRegisterPasswordVisibility() },
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.fillMaxWidth()
            )

            // Password Strength Indicator Component
            if (uiState.password.isNotEmpty()) {
                PasswordStrengthIndicator(
                    password = uiState.password,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Confirm Password Field with Visibility Toggle
            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onRegisterConfirmPasswordChange(it) },
                label = "Confirm Password",
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onToggleVisibility = { viewModel.toggleRegisterConfirmPasswordVisibility() },
                isError = uiState.confirmPasswordError != null,
                errorMessage = uiState.confirmPasswordError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    viewModel.register(onSuccess = onRegisterSuccess)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 6. Terms & Conditions Agreement Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.agreeToTerms,
                    onCheckedChange = { viewModel.onAgreeToTermsChange(it) }
                )
                Text(
                    text = "I agree to the Terms of Service & Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.termsError != null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }

            // Primary Register Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.register(onSuccess = onRegisterSuccess)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Switch to Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


================================================================================
// FILE: app/src/main/java/com/example/composeauth/ui/components/PasswordTextField.kt
// PURPOSE: Reusable Jetpack Compose Password OutlinedTextField with secure visibility toggle and error indicators.
================================================================================

package com.example.composeauth.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Material 3 OutlinedTextField with Secure Password Visibility Toggle
 * 
 * Demonstrates idiomatic Jetpack Compose state hoisting:
 * - VisualTransformation.None when isPasswordVisible == true
 * - PasswordVisualTransformation() when isPasswordVisible == false
 * - Semantic accessibility descriptions for screen readers (TalkBack)
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("••••••••") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            val description = if (isPasswordVisible) "Hide password" else "Show password"
            val icon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
            
            IconButton(
                onClick = onToggleVisibility,
                modifier = Modifier.semantics {
                    contentDescription = description
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        // Core Jetpack Compose Visual Transformation for secure password handling
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() }
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        modifier = modifier
    )
}


================================================================================
// FILE: app/src/main/java/com/example/composeauth/ui/components/PasswordStrengthIndicator.kt
// PURPOSE: Jetpack Compose real-time password strength meter with animated LinearProgressIndicator and criteria checklist.
================================================================================

package com.example.composeauth.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class StrengthEvaluation(
    val score: Float, // 0f to 1f
    val label: String,
    val color: Color,
    val hasMinLength: Boolean,
    val hasUpper: Boolean,
    val hasLower: Boolean,
    val hasNumber: Boolean,
    val hasSpecial: Boolean
)

fun calculateStrength(password: String): StrengthEvaluation {
    val hasMinLength = password.length >= 8
    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    var passed = 0
    if (hasMinLength) passed++
    if (hasUpper) passed++
    if (hasLower) passed++
    if (hasNumber) passed++
    if (hasSpecial) passed++

    val (label, color, fraction) = when {
        password.length < 6 || passed <= 1 -> Triple("Weak", Color(0xFFEF4444), 0.25f)
        passed in 2..3 -> Triple("Fair", Color(0xFFF59E0B), 0.50f)
        passed == 4 -> Triple("Good", Color(0xFF3B82F6), 0.75f)
        else -> Triple("Strong", Color(0xFF10B981), 1.0f)
    }

    return StrengthEvaluation(
        score = fraction,
        label = label,
        color = color,
        hasMinLength = hasMinLength,
        hasUpper = hasUpper,
        hasLower = hasLower,
        hasNumber = hasNumber,
        hasSpecial = hasSpecial
    )
}

@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = calculateStrength(password)
    val animatedProgress by animateFloatAsState(targetValue = strength.score, label = "strength_progress")
    val animatedColor by animateColorAsState(targetValue = strength.color, label = "strength_color")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = strength.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = animatedColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = animatedColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Requirement checklist items
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            RequirementRow(satisfied = strength.hasMinLength, text = "At least 8 characters")
            RequirementRow(satisfied = strength.hasUpper && strength.hasLower, text = "Uppercase & lowercase letters")
            RequirementRow(satisfied = strength.hasNumber, text = "At least one number (0-9)")
            RequirementRow(satisfied = strength.hasSpecial, text = "At least one special character (!@#$)")
        }
    }
}

@Composable
private fun RequirementRow(satisfied: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (satisfied) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (satisfied) Color(0xFF10B981) else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (satisfied) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


================================================================================
// FILE: app/src/main/java/com/example/composeauth/viewmodel/AuthViewModel.kt
// PURPOSE: Android ViewModel managing StateFlow, field validation rules, password toggle states, and async sign-in execution.
================================================================================

package com.example.composeauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class LoginUiState(
    val emailOrUsername: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val agreeToTerms: Boolean = false,
    val isLoading: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null
)

class AuthViewModel : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    private val emailPattern = Pattern.compile(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$"
    )

    // --- Login Handlers ---
    fun onLoginEmailChange(input: String) {
        _loginUiState.update { 
            it.copy(emailOrUsername = input, emailError = null) 
        }
    }

    fun onLoginPasswordChange(input: String) {
        _loginUiState.update { 
            it.copy(password = input, passwordError = null) 
        }
    }

    fun toggleLoginPasswordVisibility() {
        _loginUiState.update { 
            it.copy(isPasswordVisible = !it.isPasswordVisible) 
        }
    }

    fun onRememberMeChange(checked: Boolean) {
        _loginUiState.update { it.copy(rememberMe = checked) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _loginUiState.value
        var hasError = false
        var emailErr: String? = null
        var passErr: String? = null

        val emailTrimmed = state.emailOrUsername.trim()
        if (emailTrimmed.isBlank()) {
            emailErr = "Email or username cannot be empty"
            hasError = true
        } else if (emailTrimmed.contains("@") && !emailPattern.matcher(emailTrimmed).matches()) {
            emailErr = "Enter a valid email address"
            hasError = true
        }

        if (state.password.isBlank()) {
            passErr = "Password cannot be empty"
            hasError = true
        } else if (state.password.length < 6) {
            passErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) {
            _loginUiState.update { it.copy(emailError = emailErr, passwordError = passErr) }
            return
        }

        // Execute Simulated Authentication
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true) }
            delay(1200) // Simulating network handshake
            _loginUiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }

    // --- Registration Handlers ---
    fun onRegisterNameChange(name: String) {
        _registerUiState.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onRegisterEmailChange(email: String) {
        _registerUiState.update { it.copy(email = email, emailError = null) }
    }

    fun onRegisterPhoneChange(phone: String) {
        _registerUiState.update { it.copy(phoneNumber = phone, phoneError = null) }
    }

    fun onRegisterPasswordChange(password: String) {
        _registerUiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onRegisterConfirmPasswordChange(confirm: String) {
        _registerUiState.update { it.copy(confirmPassword = confirm, confirmPasswordError = null) }
    }

    fun toggleRegisterPasswordVisibility() {
        _registerUiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        _registerUiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onAgreeToTermsChange(agreed: Boolean) {
        _registerUiState.update { it.copy(agreeToTerms = agreed, termsError = null) }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _registerUiState.value
        var hasError = false
        var nameErr: String? = null
        var emailErr: String? = null
        var passErr: String? = null
        var confirmErr: String? = null
        var termsErr: String? = null

        if (state.fullName.trim().length < 2) {
            nameErr = "Full name must be at least 2 characters"
            hasError = true
        }

        if (!emailPattern.matcher(state.email.trim()).matches()) {
            emailErr = "Valid email is required"
            hasError = true
        }

        if (state.password.length < 8) {
            passErr = "Password must be at least 8 characters"
            hasError = true
        }

        if (state.password != state.confirmPassword) {
            confirmErr = "Passwords do not match"
            hasError = true
        }

        if (!state.agreeToTerms) {
            termsErr = "You must accept the terms"
            hasError = true
        }

        if (hasError) {
            _registerUiState.update {
                it.copy(
                    fullNameError = nameErr,
                    emailError = emailErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr,
                    termsError = termsErr
                )
            }
            return
        }

        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true) }
            delay(1400)
            _registerUiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}


================================================================================
// FILE: app/src/main/java/com/example/composeauth/ui/theme/Theme.kt
// PURPOSE: Material Design 3 Dynamic Color Scheme support for Android 12+ and fallback dark/light tokens.
================================================================================

package com.example.composeauth.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA8C7FA),
    onPrimary = Color(0xFF062E6F),
    primaryContainer = Color(0xFF23448C),
    onPrimaryContainer = Color(0xFFD3E3FD),
    secondary = Color(0xFFBDC7DC),
    onSecondary = Color(0xFF283141),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0B57D0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD3E3FD),
    onPrimaryContainer = Color(0xFF041E49),
    secondary = Color(0xFF565F71),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFDFBFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFBFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFF74777F),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun ComposeAuthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}


================================================================================
// FILE: app/src/main/java/com/example/composeauth/MainActivity.kt
// PURPOSE: ComponentActivity entry point integrating Jetpack Compose Navigation between Login and Register screens.
================================================================================

package com.example.composeauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeauth.ui.screens.LoginScreen
import com.example.composeauth.ui.screens.RegisterScreen
import com.example.composeauth.ui.theme.ComposeAuthTheme
import com.example.composeauth.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAuthTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(350)
                            ) + fadeIn(animationSpec = tween(350))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(350)
                            ) + fadeOut(animationSpec = tween(350))
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(350)
                            ) + fadeIn(animationSpec = tween(350))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(350)
                            ) + fadeOut(animationSpec = tween(350))
                        }
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                },
                                onLoginSuccess = {
                                    // Navigate to home or dashboard
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


================================================================================
// FILE: app/build.gradle.kts
// PURPOSE: Gradle configuration with Jetpack Compose BOM, Material 3, Navigation Compose, and Lifecycle.
================================================================================

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.composeauth"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.composeauth"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Jetpack Compose Bill of Materials (BOM)
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // AndroidX Core & Lifecycle
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Jetpack Compose UI & Material 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")
    
    // Biometrics API
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Tooling & Debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

