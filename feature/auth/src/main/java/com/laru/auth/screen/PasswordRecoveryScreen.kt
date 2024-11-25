package com.laru.auth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.laru.auth.R
import com.laru.auth.presentation.AuthAction
import com.laru.auth.presentation.AuthViewModel
import com.laru.ui.model.ScreenState
import com.laru.ui.model.Paddings
import com.laru.ui.model.Sizes
import com.laru.ui.model.Spacers
import kotlinx.coroutines.delay


@Composable
fun PasswordRecoveryScreen(
    navigateOnSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.passwordRecoveryUiState.collectAsState()

    LaunchedEffect(uiState.screenState) {
        if (uiState.screenState == ScreenState.Success()) {
            delay(1500)
            navigateOnSuccess()
        }
    }

    PasswordRecoveryScreenContent(
        screenState = uiState.screenState,
        passwordTextFieldValue = uiState.newPassword,
        confirmPasswordTextFieldValue = uiState.confirmNewPassword,
        onAction = viewModel::onAction
    )
}

@Composable
private fun PasswordRecoveryScreenContent(
    screenState: ScreenState,
    passwordTextFieldValue: String,
    confirmPasswordTextFieldValue: String,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Paddings.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO border color depending on the password complexity
        OutlinedTextField(
            value = passwordTextFieldValue,
            onValueChange = { onAction(AuthAction.OnRecoverPasswordChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password_field_label)) },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = if (passwordTextFieldValue.isEmpty()) null else {
                {
                    IconButton({ passwordVisible = !passwordVisible }) {
                        if (passwordVisible)
                            Icon(Icons.Filled.Visibility, contentDescription = null)
                        else
                            Icon(Icons.Filled.VisibilityOff, contentDescription = null)
                    }
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = confirmPasswordTextFieldValue,
            onValueChange = { onAction(AuthAction.OnRecoverConfirmPasswordChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.confirm_password_field_label)) },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = if (passwordTextFieldValue.isEmpty()) null else {
                {
                    IconButton({ passwordVisible = !passwordVisible }) {
                        if (passwordVisible)
                            Icon(Icons.Filled.Visibility, contentDescription = null)
                        else
                            Icon(Icons.Filled.VisibilityOff, contentDescription = null)
                    }
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordTextFieldValue != confirmPasswordTextFieldValue,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            )
        )

        Spacer(Modifier.height(Spacers.default))
        FilledTonalButton(
            onClick = { onAction(AuthAction.OnRecoverPasswordClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.buttonDefault),
            enabled = passwordTextFieldValue.isNotEmpty() &&
                    passwordTextFieldValue == confirmPasswordTextFieldValue // TODO manage
        ) {
            Text(stringResource(R.string.change_password))
        }

        Spacer(Modifier.height(Spacers.large)) // TODO refactor
        when (screenState) {
            ScreenState.Idle -> {}
            ScreenState.Loading -> {
                CircularProgressIndicator()
            }
            is ScreenState.Error -> {
                Text("Changing password failed", color = Color.Red.copy(alpha = 0.5f))
            }
            is ScreenState.Success -> {
                Text("Password was successfully changed", color = Color.Green.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PasswordRecoveryScreenPreview() {
    PasswordRecoveryScreenContent(
        screenState = ScreenState.Success(),
        passwordTextFieldValue = "123",
        confirmPasswordTextFieldValue = "123",
        onAction = {}
    )
}
