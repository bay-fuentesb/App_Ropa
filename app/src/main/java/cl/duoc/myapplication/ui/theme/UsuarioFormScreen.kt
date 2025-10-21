package cl.duoc.myapplication.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.myapplication.viewmodel.UsuarioFormViewModel

@Composable
fun UsuarioFormScreen(navController: NavController, viewModel: UsuarioFormViewModel = viewModel()) {
    // Leer StateFlows del ViewModel
    val formState = viewModel.form.collectAsState(initial = viewModel.form.value)
    val form = formState.value
    val errorsState = viewModel.errors.collectAsState(initial = viewModel.errors.value)
    val errors = errorsState.value

    var showDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "The Clothing APP 👓",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = form.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                isError = errors.nombre != null,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { errors.nombre?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = form.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo") },
                isError = errors.correo != null,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { errors.correo?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = form.edad?.toString() ?: "",
                onValueChange = { viewModel.onEdadChange(it.toIntOrNull()) },
                label = { Text("Edad") },
                isError = errors.edad != null,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { errors.edad?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            RowWithCheckbox(
                checked = form.aceptaTerminos,
                onCheckedChange = { viewModel.onAceptaTerminosChange(it) },
                label = "Acepta los términos",
                errorText = errors.terminos
            )

            Spacer(modifier = Modifier.height(8.dp))

            RowWithCheckbox(
                checked = form.quiereNotificaciones,
                onCheckedChange = { viewModel.onQuiereNotificacionesChange(it) },
                label = "¿Quiere recibir notificaciones?",
                errorText = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (viewModel.isFormValid(errors)) {
                        // avanzar al inicio
                        navController.navigate("inicio")
                    } else {
                        showSnackbar = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Enviar", color = MaterialTheme.colorScheme.onPrimary)
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmación") },
                    text = { Text("Formulario enviado correctamente") },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) { Text("OK") }
                    }
                )
            }

            if (showSnackbar) {
                Snackbar(action = {}) { Text("Corrige los errores antes de enviar") }
            }
        }
    }
}

@Composable
private fun RowWithCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    errorText: String?
) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material3.Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label)
        errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
