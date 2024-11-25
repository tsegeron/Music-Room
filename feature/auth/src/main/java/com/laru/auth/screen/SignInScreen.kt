package com.laru.auth.screen

import android.util.Log
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
fun SignInScreen(
    navigateToSignUp: () -> Unit,
    navigateToPasswordRestore: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.signInUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    SignInScreenContent(
        emailTextFieldValue = uiState.email,
        passwordTextFieldValue = uiState.password,
        onAction = viewModel::onAction,
        navigateToSignUp = navigateToSignUp,
        navigateToPasswordRestore = navigateToPasswordRestore,
        signInWithGoogle = {
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
        signInWithFacebook = {},
        modifier = Modifier
    )
}

@Composable
private fun SignInScreenContent(
    emailTextFieldValue: String,
    passwordTextFieldValue: String,
    onAction: (AuthAction) -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToPasswordRestore: () -> Unit,
    signInWithGoogle: () -> Unit,
    signInWithFacebook: () -> Unit,
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
        OutlinedTextField(
            value = emailTextFieldValue,
            onValueChange = { onAction(AuthAction.OnEmailChange(it)) }, // assigning directly causes app crash
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

        OutlinedTextField(
            value = passwordTextFieldValue,
            onValueChange = { onAction(AuthAction.OnSignInPasswordChange(it)) },
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
                } },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            onClick = { onAction(AuthAction.OnSignInClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.buttonDefault),
            enabled = emailTextFieldValue.isNotEmpty() && passwordTextFieldValue.isNotEmpty() // TODO manage checking
        ) {
            Text(stringResource(R.string.log_in))
        }

        Spacer(Modifier.height(Spacers.large))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.no_account)) // TODO change prompt
            TextButton(navigateToSignUp) {
                Text(stringResource(R.string.sign_up))
            }
        }
        Spacer(Modifier.height(Spacers.small))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.forgot_password)) // TODO change prompt
            TextButton(navigateToPasswordRestore) {
                Text(stringResource(R.string.recover_password))
            }
        }

        Spacer(Modifier.height(Spacers.extraLarge))
        OutlinedButton(
            onClick = signInWithGoogle,
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
            onClick = signInWithFacebook,
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
private fun AuthScreenPreview() {
    SignInScreenContent(
        emailTextFieldValue = "",
        passwordTextFieldValue = "",
        onAction = {},
        navigateToSignUp = {},
        navigateToPasswordRestore = {},
        signInWithGoogle = {},
        signInWithFacebook = {}
    )
}
