package cl.duoc.myapplication.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.viewmodel.RopaViewModel

@Composable
fun TestApiScreen(
    ropaViewModel: RopaViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val prendas = ropaViewModel.prendas
    val isLoading = ropaViewModel.isLoading
    val errorMessage = ropaViewModel.errorMessage

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // 1. HEADER Y CONTROLES
        Text("🛠️ ZONA DE PRUEBAS API", style = MaterialTheme.typography.titleLarge)
        Text("Conectado a: AWS EC2 (MySQL)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { ropaViewModel.cargarPrendasDesdeApi() }) {
                Text("GET (Cargar)")
            }

            Button(
                onClick = {
                    // Creamos una prenda dummy con hora actual para que sea única
                    val dummy = Prenda(
                        titulo = "Test API ${System.currentTimeMillis() % 1000}",
                        categoria = "Prueba",
                        color = "#FF0000",
                        imagenUri = "" // Sin imagen por ahora para probar solo datos
                    )
                    ropaViewModel.agregarPrenda(dummy)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("POST (Crear)")
            }
        }

        Button(onClick = onVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
            Text("Volver a la App Normal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. ESTADO DE CARGA Y ERRORES
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Cargando datos de la nube...", color = Color.Blue)
        }

        if (errorMessage != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    text = "ERROR: $errorMessage",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Button(onClick = { ropaViewModel.limpiarError() }) { Text("Limpiar Error") }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // 3. LISTA DE RESULTADOS (Raw Data)
        Text("Resultados (${prendas.size}):", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(prendas) { prenda ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Mostramos el ID para verificar que MySQL lo generó (no debe ser null)
                            Text("ID: ${prenda.id}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Titulo: ${prenda.titulo}")
                            Text("Cat: ${prenda.categoria}")
                        }

                        IconButton(onClick = { ropaViewModel.eliminarPrenda(prenda) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}