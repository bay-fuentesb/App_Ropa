package cl.duoc.myapplication.ui.theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.R
import cl.duoc.myapplication.viewmodel.UsuarioFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioFormScreen(navController: NavController, viewModel: UsuarioFormViewModel = viewModel()) {
    val formState = viewModel.form.collectAsState(initial = viewModel.form.value)
    val form = formState.value
    val errorsState = viewModel.errors.collectAsState(initial = viewModel.errors.value)
    val errors = errorsState.value

    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Gradiente moderno para fondo
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF667eea), // Azul moderno
            Color(0xFF764ba2)  // Púrpura elegante
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Reducido el padding para más espacio
                    .align(Alignment.TopCenter) // Cambiado a TopCenter
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        clip = true
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState) // Scroll añadido aquí
                        .padding(24.dp), // Padding reducido
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Logo/Header mejorado - más compacto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        // Icono de moda más pequeño
                        Box(
                            modifier = Modifier
                                .size(60.dp) // Reducido el tamaño
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(
                                    2.dp,
                                    color = Color.White,
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.dresscodeicon),
                                contentDescription = "Logo DressCode",
                                modifier = Modifier.size(32.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "DressCode",
                            fontSize = 20.sp, // Reducido el tamaño
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Crea tu cuenta y ordena tu closet virtual",
                            fontSize = 12.sp, // Reducido el tamaño
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Campos del formulario con menos espacio entre ellos
                    FashionTextField(
                        value = form.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = "Nombre completo",
                        placeholder = "Ingresa tu nombre",
                        leadingIcon = Icons.Default.Person,
                        isError = errors.nombre != null,
                        errorText = errors.nombre,
                        modifier = Modifier.fillMaxWidth()
                    )

                    FashionTextField(
                        value = form.correo,
                        onValueChange = { viewModel.onCorreoChange(it) },
                        label = "Correo electrónico",
                        placeholder = "tu@email.com",
                        leadingIcon = Icons.Default.Email,
                        isError = errors.correo != null,
                        errorText = errors.correo,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    FashionTextField(
                        value = form.edad?.toString() ?: "",
                        onValueChange = { viewModel.onEdadChange(it.toIntOrNull()) },
                        label = "Edad",
                        placeholder = "18",
                        leadingIcon = Icons.Default.Info,
                        isError = errors.edad != null,
                        errorText = errors.edad,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Checkboxes más compactos
                    FashionCheckbox(
                        checked = form.aceptaTerminos,
                        onCheckedChange = { viewModel.onAceptaTerminosChange(it) },
                        label = "Acepto los términos y condiciones",
                        description = "He leído y acepto la política de privacidad",
                        isError = errors.terminos != null,
                        errorText = errors.terminos,
                        modifier = Modifier.fillMaxWidth()
                    )

                    FashionCheckbox(
                        checked = form.quiereNotificaciones,
                        onCheckedChange = { viewModel.onQuiereNotificacionesChange(it) },
                        label = "Recibir notificaciones",
                        description = "Mantente al día con las últimas tendencias y ofertas",
                        isError = false,
                        errorText = null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón mejorado
                    Button(
                        onClick = {
                            if (viewModel.isFormValid(errors)) {
                                isLoading = true
                                // Simular proceso de registro
                                scope.launch {
                                    // Aquí iría tu lógica de registro real
                                    kotlinx.coroutines.delay(1500) // Simular carga
                                    isLoading = false
                                    navController.navigate("inicio")
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Por favor, corrige los errores antes de continuar",
                                        withDismissAction = true
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp), // Altura reducida
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Crear Cuenta",
                                fontSize = 15.sp, // Tamaño reducido
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Enlace para iniciar sesión
                    Text(
                        text = "¿Ya tienes una cuenta? Inicia sesión",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp, // Tamaño reducido
                        modifier = Modifier
                            .clickable {
                                // navController.navigate("login")
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FashionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp // Texto más pequeño
                )
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp) // Icono más pequeño
                    )
                }
            },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp), // Altura fija para consistencia
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp)
        )

        errorText?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp, // Texto de error más pequeño
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun FashionCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    description: String? = null,
    isError: Boolean = false,
    errorText: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(10.dp) // Padding reducido
    ) {
        Row(
            verticalAlignment = Alignment.Top, // Cambiado a Top para mejor alineación
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 12.dp, top = 4.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp // Texto más pequeño
                )

                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp, // Texto más pequeño
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        errorText?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp, // Texto de error más pequeño
                modifier = Modifier.padding(start = 52.dp, top = 4.dp)
            )
        }
    }
}