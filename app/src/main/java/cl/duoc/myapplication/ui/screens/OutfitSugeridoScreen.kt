package cl.duoc.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.viewmodel.RopaViewModel
import cl.duoc.myapplication.repository.OutfitRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitSugeridoScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel = viewModel()
) {
    val prendas = ropaViewModel.prendas
    val outfitRepository = remember { OutfitRepository() }

    var outfit by remember { mutableStateOf(ropaViewModel.outfits.firstOrNull()) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outfit Sugerido") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
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

            // Mostrar mensaje de error si existe
            mensajeError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (outfit == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Aún no has generado un outfit",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )

                    Text(
                        text = "Prendas disponibles: ${prendas.size}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    if (prendas.isNotEmpty()) {
                        Text(
                            text = "Categorías: ${prendas.map { it.categoria }.distinct().joinToString()}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = outfit!!.nombre,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(outfit!!.combinacion) { prenda ->
                        PrendaRow(prenda = prenda)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    mensajeError = null
                    if (prendas.isEmpty()) {
                        mensajeError = "No hay prendas disponibles. Agrega algunas prendas primero."
                        return@Button
                    }

                    // Intentar generar outfit aleatorio
                    val nuevoOutfit = outfitRepository.generarOutfitAleatorio(prendas)

                    if (nuevoOutfit != null) {
                        ropaViewModel.agregarOutfit(nuevoOutfit)
                        outfit = nuevoOutfit
                    } else {
                        // Fallback: intentar con sugerencias
                        val sugerencias = outfitRepository.generarSugerenciasOutfits(prendas)
                        if (sugerencias.isNotEmpty()) {
                            val fallbackOutfit = sugerencias.first()
                            ropaViewModel.agregarOutfit(fallbackOutfit)
                            outfit = fallbackOutfit
                        } else {
                            mensajeError = "No se pudo generar un outfit. Asegúrate de tener prendas de diferentes categorías."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = prendas.isNotEmpty()
            ) {
                Text("Generar Outfit Aleatorio")
            }

            // Botón para cargar datos de ejemplo
            if (prendas.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        ropaViewModel.cargarPrendasDeEjemplo()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text("Cargar Prendas de Ejemplo")
                }
            }

            // Botón adicional para limpiar
            if (outfit != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        outfit = null
                        mensajeError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Limpiar Outfit")
                }
            }
        }
    }
}

@Composable
fun PrendaRow(prenda: Prenda) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Placeholder para imagen - usando un Box con color basado en el color de la prenda
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = when (prenda.color.lowercase()) {
                            "rojo" -> Color.Red
                            "azul" -> Color.Blue
                            "verde" -> Color.Green
                            "negro" -> Color.Black
                            "blanco" -> Color.White
                            "amarillo" -> Color.Yellow
                            "gris" -> Color.Gray
                            "rosa" -> Color.Magenta
                            "morado" -> Color(0xFF6A0DAD)
                            "naranja" -> Color(0xFFFFA500)
                            else -> Color.Gray
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = prenda.categoria.take(3).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = prenda.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Categoría: ${prenda.categoria}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Color: ${prenda.color}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}