package com.laru.auth.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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


@Composable
fun PasswordRecoveryEmailScreen(
    navigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    BackHandler(onBack = navigateBack)

    val uiState by viewModel.passwordRecoveryUiState.collectAsState()

    PasswordRecoveryScreenContent(
        emailTextFieldValue = uiState.email,
        screenState = uiState.screenState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun PasswordRecoveryScreenContent(
    emailTextFieldValue: String,
    screenState: ScreenState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
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

        Spacer(Modifier.height(Spacers.default))
        FilledTonalButton(
            onClick = { onAction(AuthAction.OnSendInstructionsClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.buttonDefault),
//            enabled =  // TODO manage email check
        ) {
            Text(stringResource(R.string.send_instructions))
        }

        Spacer(Modifier.height(Spacers.large))
        when (screenState) {
            ScreenState.Idle -> {}
            ScreenState.Loading -> {
                CircularProgressIndicator()
            }
            is ScreenState.Error -> {
                Text("Sending instructions failed", color = Color.Red.copy(alpha = 0.5f))
            }
            is ScreenState.Success -> {
                Text("Instructions were successfully sent", color = Color.Green.copy(alpha = 0.5f))
            }
        }

    }
}


@Composable
@Preview(showBackground = true)
private fun PasswordRecoveryEmailScreenPreview() {
    PasswordRecoveryScreenContent(
        emailTextFieldValue = "",
        screenState = ScreenState.Error(),
        onAction = {}
    )
}
