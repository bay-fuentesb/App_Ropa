package cl.duoc.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.ui.components.PrendaImagen
import cl.duoc.myapplication.viewmodel.RopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPrendas(
    ropaViewModel: RopaViewModel = viewModel(),
    navController: androidx.navigation.NavController
) {
    val prendas = ropaViewModel.prendas
    val isLoading = ropaViewModel.isLoading
    val errorMessage = ropaViewModel.errorMessage

    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para mostrar errores
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            ropaViewModel.limpiarError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // USAMOS CenterAlignedTopAppBar PARA CENTRADO AUTOMÁTICO Y ELEGANTE
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mi Armario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("inicio") }) {
                        Icon(Icons.Filled.Home, contentDescription = "Inicio")
                    }
                },
                actions = {
                    IconButton(onClick = { ropaViewModel.cargarPrendasDesdeApi() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                    }
                    IconButton(onClick = { navController.navigate("outfits") }) {
                        Icon(Icons.Filled.Create, contentDescription = "Outfits")
                    }
                    IconButton(onClick = { navController.navigate("agregar") }) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- LISTA UNIFICADA (Para Scroll Fluido) ---
            if (prendas.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tu armario está vacío",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate("agregar") }) {
                            Text("Agregar primera prenda")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp), // Margen general
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre tarjetas
                ) {
                    // Si tienes filtros, ponlos aquí como un item {}
                    // item { FiltrosSection(...) }

                    items(prendas) { prenda ->
                        PrendaCard(
                            prenda = prenda,
                            onDeleteClick = { ropaViewModel.eliminarPrenda(prenda) }
                        )
                    }
                }
            }

            // --- OVERLAY DE CARGA ---
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PrendaCard(
    prenda: Prenda,
    onDeleteClick: () -> Unit
) {
    val colorCompose = try {
        Color(android.graphics.Color.parseColor(prenda.color))
    } catch (_: Exception) {
        Color.LightGray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp), // Bordes más redondeados
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // IMAGEN CON BORDES REDONDEADOS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Un poco más alto para lucir la prenda
                    .clip(RoundedCornerShape(12.dp)) // Recorta la imagen
                    .background(Color.White) // Fondo neutro para la imagen
            ) {
                PrendaImagen(
                    imagenPath = prenda.imagenUri,
                    modifier = Modifier.fillMaxSize()
                )

                // Etiqueta flotante de categoría (Opcional, estilo visual)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = prenda.categoria,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // INFO
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prenda.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    // Si quieres mostrar la marca u otro detalle
                    // Text(text = "Marca", style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Círculo de color con borde para contraste
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(colorCompose, CircleShape)
                            .padding(2.dp) // Simula un borde interno si el color es igual al fondo
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}