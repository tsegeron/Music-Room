package com.laru.auth.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.laru.auth.R
import com.laru.auth.presentation.AuthAction
import com.laru.auth.presentation.AuthViewModel
import com.laru.ui.model.Paddings
import com.laru.ui.model.Sizes
import com.laru.ui.model.Spacers
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    navigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    BackHandler(onBack = navigateBack)

    val uiState by viewModel.signUpUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    SignUpScreenContent(
        usernameTextFieldValue = uiState.username,
        emailTextFieldValue = uiState.email,
        passwordTextFieldValue = uiState.password,
        confirmPasswordTextFieldValue = uiState.confirmPassword,
        isTermsAcceptedValue = uiState.isTermsAccepted,
        onAction = viewModel::onAction,
        signUpWithGoogle = {
            coroutineScope.launch {
                val credentialManager = CredentialManager.create(context)
                try {
                    val result = credentialManager.getCredential(context, viewModel.credentialRequest)
                    viewModel.handleGoogleAuth(result)
                } catch (e: GetCredentialException) {
//                    handleFailure()
                    Log.e("MainActivity", "GetCredentialException", e)
                }
            }
        },
        signUpWithFacebook = {},
    )
}

@Composable
private fun SignUpScreenContent(
    usernameTextFieldValue: String,
    emailTextFieldValue: String,
    passwordTextFieldValue: String,
    confirmPasswordTextFieldValue: String,
    isTermsAcceptedValue: Boolean,
    onAction: (AuthAction) -> Unit,
    signUpWithGoogle: () -> Unit,
    signUpWithFacebook: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Paddings.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = usernameTextFieldValue,
            onValueChange = { onAction(AuthAction.OnSignUpUsernameChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(R.string.username_field_label))
            },
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
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
            value = emailTextFieldValue,
            onValueChange = { onAction(AuthAction.OnEmailChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(R.string.email_field_label))
            },
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            )
        )

        // TODO border color depending on the password complexity
        OutlinedTextField(
            value = passwordTextFieldValue,
            onValueChange = { onAction(AuthAction.OnSignUpPasswordChange(it)) },
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
            onValueChange = { onAction(AuthAction.OnSignUpConfirmPasswordChange(it)) },
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
        Row {
            Checkbox(
                checked = isTermsAcceptedValue,
                onCheckedChange = { onAction(AuthAction.OnAgreeWithTermsClick) }
            )
            TextButton(
                onClick = {} // TODO open modalSheet of Terms and Conditions
            ) { // add annotatedString with hyperlinks??
                Text(stringResource(R.string.agree_with_terms_and_conditions))
            }
        }

        Spacer(Modifier.height(Spacers.default))
        FilledTonalButton(
            onClick = { onAction(AuthAction.OnSignUpClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.buttonDefault),
            enabled = isTermsAcceptedValue && passwordTextFieldValue.isNotEmpty() &&
                    passwordTextFieldValue == confirmPasswordTextFieldValue // TODO manage
        ) {
            Text(stringResource(R.string.sign_up))
        }


        Spacer(Modifier.height(Spacers.extraLarge))
        OutlinedButton(
            onClick = signUpWithGoogle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.continue_with_google))
            Spacer(Modifier.width(Spacers.medium))
            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(Sizes.iconLarge)
            )
        }

        Spacer(Modifier.height(Spacers.default))
        OutlinedButton(
            onClick = signUpWithFacebook,
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        ) {
            Text(stringResource(R.string.continue_with_facebook))
            Spacer(Modifier.width(Spacers.medium))
            Icon(
                painter = painterResource(R.drawable.ic_facebook),
                contentDescription = null,
                modifier = Modifier.size(Sizes.iconLarge)
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun SignUpScreenPreview() {
    SignUpScreenContent(
        usernameTextFieldValue = "",
        emailTextFieldValue = "",
        passwordTextFieldValue = "",
        confirmPasswordTextFieldValue = "",
        isTermsAcceptedValue = true,
        onAction = {},
        signUpWithGoogle = {},
        signUpWithFacebook = {},
    )
}
