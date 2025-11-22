package cl.duoc.myapplication.ui.screens

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.net.toUri
import cl.duoc.myapplication.viewmodel.RopaViewModel
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.R
import cl.duoc.myapplication.ui.components.PrendaImagen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOutfitScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel
) {
    val prendas = ropaViewModel.prendas
    val context = LocalContext.current

    val prendasSeleccionadas = remember { mutableStateListOf<Prenda>() }
    var nombreOutfit by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }
    var mostrarDialogoGuardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Outfit Personalizado") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Sección de información y formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Crear Nuevo Outfit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nombreOutfit,
                        onValueChange = {
                            nombreOutfit = it
                            mostrarError = false
                        },
                        label = { Text("Nombre del outfit") },
                        placeholder = { Text("Ej: Outfit Casual, Look de Oficina...") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = mostrarError && nombreOutfit.isBlank(),
                        trailingIcon = {
                            if (mostrarError && nombreOutfit.isBlank()) {
                                Icon(Icons.Filled.Warning, "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    if (mostrarError && nombreOutfit.isBlank()) {
                        Text(
                            text = "El nombre es obligatorio",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contador de prendas seleccionadas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prendas seleccionadas: ${prendasSeleccionadas.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (prendasSeleccionadas.isNotEmpty()) {
                            TextButton(onClick = { prendasSeleccionadas.clear() }) {
                                Text("Limpiar selección")
                            }
                        }
                    }
                }
            }

            // Sección de selección de prendas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selecciona prendas:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${prendas.size} disponibles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (prendas.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.balenciaga),
                                contentDescription = "Sin prendas",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay prendas disponibles",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Agrega algunas prendas primero",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(prendas) { prenda ->
                                PrendaSeleccionableCard(
                                    prenda = prenda,
                                    context = context,
                                    seleccionado = prendasSeleccionadas.contains(prenda),
                                    onClick = {
                                        if (prendasSeleccionadas.contains(prenda)) {
                                            prendasSeleccionadas.remove(prenda)
                                        } else {
                                            prendasSeleccionadas.add(prenda)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            Button(
                onClick = {
                    if (nombreOutfit.isBlank() || prendasSeleccionadas.isEmpty()) {
                        mostrarError = true
                    } else {
                        val nuevoOutfit = OutfitSugerido(
                            nombre = nombreOutfit,
                            combinacion = prendasSeleccionadas.toList()
                        )
                        ropaViewModel.agregarOutfit(nuevoOutfit)
                        mostrarDialogoGuardado = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = prendasSeleccionadas.isNotEmpty() && nombreOutfit.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Guardar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar Outfit",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Mensaje de validación
            if (mostrarError && prendasSeleccionadas.isEmpty()) {
                Text(
                    text = "Selecciona al menos una prenda",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    // Diálogo de confirmación
    if (mostrarDialogoGuardado) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoGuardado = false },
            title = { Text("Outfit Guardado") },
            text = {
                Text("Tu outfit \"$nombreOutfit\" ha sido creado exitosamente con ${prendasSeleccionadas.size} prendas.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoGuardado = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun PrendaSeleccionableCard(
    prenda: Prenda,
    context: android.content.Context,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val uri = try { prenda.imagenUri.toUri() } catch (_: Exception) { null }

    val bitmap = remember(uri) {
        try {
            if (uri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val src = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(src)
                } else {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            } else null
        } catch (_: Exception) { null }
    }

    val colorParse = try {
        Color(android.graphics.Color.parseColor(prenda.color))
    } catch (_: Exception) {
        Color.DarkGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(if (seleccionado) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (seleccionado) {
            CardDefaults.outlinedCardBorder()
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de selección
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (seleccionado) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            )
            {
                if (seleccionado) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Seleccionado",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Llamada funcion corregido Prenda
            PrendaImagen(
                imagenPath = prenda.imagenUri,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

            Spacer(modifier = Modifier.width(16.dp))

            // Información de la prenda
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prenda.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = prenda.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Indicador de color
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(colorParse, CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
            )
        }
    }