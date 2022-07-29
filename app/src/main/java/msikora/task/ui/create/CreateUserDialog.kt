package msikora.task.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import msikora.task.domain.Gender
import msikora.task.ui.Colors

@Composable
fun CreateUserDialog(
    navController: NavController,
    viewModel: CreateUserViewModel = hiltViewModel(),
) {

    val onDismissRequest: () -> Unit = { navController.popBackStack() }
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        LaunchedEffect(viewModel.finishSignal) {
            viewModel.finishSignal.collect {
                onDismissRequest()
            }
        }
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                TextFormField(
                    value = viewModel.name.collectAsState().value,
                    onValueChange = { viewModel.updateName(it) },
                    errorValue = viewModel.nameError.collectAsState("").value,
                    placeholderValue = "Name"
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextFormField(
                    value = viewModel.email.collectAsState().value,
                    onValueChange = { viewModel.updateEmail(it) },
                    errorValue = viewModel.emailError.collectAsState("").value,
                    placeholderValue = "Email"
                )
                Spacer(modifier = Modifier.height(16.dp))
                val selectedGender by viewModel.gender.collectAsState(CreateUserViewModel.DEFAULT_GENDER)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectableGroup()
                ) {
                    GenderRatioButton(Gender.Female, selectedGender) { viewModel.updateGender(it) }
                    GenderRatioButton(Gender.Male, selectedGender) { viewModel.updateGender(it) }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = viewModel.genderError.collectAsState("").value,
                    color = Colors.red
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = viewModel.generalError.collectAsState("").value,
                    color = Colors.red
                )

                val isInProgress by viewModel.callInProgress.collectAsState(false)
                if (isInProgress) {
                    CircularProgressIndicator()
                } else {
                    Button(onClick = viewModel::submit) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}

@Composable
fun GenderRatioButton(gender: Gender, selectedGender: Gender, onUpdateGender: (Gender) -> Unit) {
    Text(
        text = when (gender) {
            Gender.Male -> "Male"
            Gender.Female -> "Female"
        }
    )
    RadioButton(
        selected = gender == selectedGender,
        onClick = { onUpdateGender(gender) }
    )
}

@Composable
fun ColumnScope.TextFormField(
    value: String,
    onValueChange: (String) -> Unit,
    errorValue: String,
    placeholderValue: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderValue) },
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = errorValue, color = Colors.red)
}