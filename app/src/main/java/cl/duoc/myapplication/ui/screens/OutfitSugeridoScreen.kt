package cl.duoc.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.ui.components.PrendaImagen
import cl.duoc.myapplication.viewmodel.RopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitSugeridoScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel = viewModel()
) {
    val prendas = ropaViewModel.prendas

    // Observamos el estado del ViewModel
    val outfit = ropaViewModel.outfitSugerido
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outfit Sugerido") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. MENSAJE DE ERROR (Si existe)
            if (mensajeError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = mensajeError!!, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            // 2. CONTENIDO PRINCIPAL (Con Weight para el Scroll)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (outfit == null) {
                    // VISTA SIN OUTFIT
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Presiona el botón para generar un look",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prendas disponibles: ${prendas.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    // VISTA CON OUTFIT (LAZY COLUMN SCROLLABLE)
                    Column {
                        Text(
                            text = outfit!!.nombre,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(outfit!!.combinacion) { prenda ->
                                PrendaRow(prenda = prenda)
                            }
                        }
                    }
                }
            }

            // 3. BOTONES (Footer Fijo)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Button(
                    onClick = {
                        mensajeError = null
                        if (prendas.isEmpty()) {
                            mensajeError = "No hay prendas disponibles. Agrega algunas prendas primero."
                            return@Button
                        }

                        val error = ropaViewModel.generarOutfitSugerido()
                        if (error != null) {
                            mensajeError = error
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = prendas.isNotEmpty()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar Aleatorio")
                }

                if (prendas.isEmpty()) {
                    OutlinedButton(
                        onClick = { ropaViewModel.cargarPrendasDeEjemplo() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cargar Prendas de Ejemplo")
                    }
                }

                if (outfit != null) {
                    TextButton(
                        onClick = {
                            ropaViewModel.limpiarOutfitSugerido()
                            mensajeError = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Limpiar pantalla", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun PrendaRow(prenda: Prenda) {
    val colorParse = try { Color(android.graphics.Color.parseColor(prenda.color)) } catch (_: Exception) { Color.Transparent }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                PrendaImagen(imagenPath = prenda.imagenUri, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = prenda.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = prenda.categoria, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(24.dp).background(colorParse, CircleShape).border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape))
            }
        }
    }
}