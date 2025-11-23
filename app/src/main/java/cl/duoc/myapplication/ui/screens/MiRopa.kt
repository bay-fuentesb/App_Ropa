package cl.duoc.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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

    // Filtros
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var colorSeleccionado by remember { mutableStateOf<String?>(null) }

    // 🔥 ESTADO PARA CONTROLAR EL DIÁLOGO DE BORRADO
    var prendaAEliminar by remember { mutableStateOf<Prenda?>(null) }

    val categoriasDisponibles = listOf(
        "Accesorios", "Calcetines", "Chaqueta", "Jockey",
        "Parka", "Pantalones", "Polera", "Poleron", "Zapatilla"
    )
    val coloresDisponibles = remember(prendas) { prendas.map { it.color }.distinct() }

    val prendasFiltradas = remember(prendas, categoriaSeleccionada, colorSeleccionado) {
        prendas.filter { prenda ->
            (categoriaSeleccionada == null || prenda.categoria == categoriaSeleccionada) &&
                    (colorSeleccionado == null || prenda.color == colorSeleccionado)
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            ropaViewModel.limpiarError()
        }
    }

    // 🔥 ALERTA DE CONFIRMACIÓN
    if (prendaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { prendaAEliminar = null },
            title = { Text("Eliminar Prenda") },
            text = { Text("¿Estás seguro de que quieres eliminar '${prendaAEliminar?.titulo}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Aquí ocurre el borrado real
                        ropaViewModel.eliminarPrenda(prendaAEliminar!!)
                        prendaAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { prendaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Armario", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("inicio") }) {
                        Icon(Icons.Filled.Home, contentDescription = "Inicio")
                    }
                },
                actions = {
                    IconButton(onClick = { ropaViewModel.cargarPrendasDesdeApi() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("outfits") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                text = { Text("Mis Outfits") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (prendas.isNotEmpty()) {
                FiltrosSection(
                    categorias = categoriasDisponibles,
                    colores = coloresDisponibles,
                    catSeleccionada = categoriaSeleccionada,
                    colSeleccionado = colorSeleccionado,
                    onCatSelected = { nuevaCat ->
                        categoriaSeleccionada = if (categoriaSeleccionada == nuevaCat) null else nuevaCat
                    },
                    onColorSelected = { nuevoColor ->
                        colorSeleccionado = if (colorSeleccionado == nuevoColor) null else nuevoColor
                    },
                    onLimpiarFiltros = {
                        categoriaSeleccionada = null
                        colorSeleccionado = null
                    }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    prendas.isEmpty() -> {
                        EmptyStateMessage(
                            mensaje = "Tu armario está vacío",
                            botonTexto = "Agregar primera prenda",
                            onClick = { navController.navigate("agregar") }
                        )
                    }
                    prendasFiltradas.isEmpty() -> {
                        EmptyStateMessage(
                            mensaje = "No se encontraron prendas con esos filtros",
                            botonTexto = "Limpiar filtros",
                            onClick = {
                                categoriaSeleccionada = null
                                colorSeleccionado = null
                            }
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(prendasFiltradas) { prenda ->
                                PrendaCard(
                                    prenda = prenda,
                                    // 🔥 CAMBIO CLAVE: En vez de borrar directo, guardamos la prenda en la variable para activar la alerta
                                    onDeleteClick = { prendaAEliminar = prenda }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... El resto de los componentes (FiltrosSection, EmptyStateMessage, PrendaCard) se mantienen igual ...
// Asegúrate de copiar y pegar las funciones auxiliares que ya tenías abajo.

// --- COMPONENTES AUXILIARES ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosSection(
    categorias: List<String>,
    colores: List<String>,
    catSeleccionada: String?,
    colSeleccionado: String?,
    onCatSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
    ) {
        // Fila 1: Categorías
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón limpiar filtros (solo si hay alguno activo)
            if (catSeleccionada != null || colSeleccionado != null) {
                item {
                    FilterChip(
                        selected = true,
                        onClick = onLimpiarFiltros,
                        label = { Icon(Icons.Default.Close, contentDescription = "Borrar", modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                }
            }

            items(categorias) { cat ->
                FilterChip(
                    selected = cat == catSeleccionada,
                    onClick = { onCatSelected(cat) },
                    label = { Text(cat) },
                    leadingIcon = if (cat == catSeleccionada) {
                        { Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila 2: Colores (Solo si hay colores disponibles)
        if (colores.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "Colores:",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(end = 8.dp)
                    )
                }
                items(colores) { colorHex ->
                    val colorInt = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Gray }

                    FilterChip(
                        selected = colorHex == colSeleccionado,
                        onClick = { onColorSelected(colorHex) },
                        label = {
                            // Mostramos el círculo de color dentro del chip
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(colorInt)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = (colorHex == colSeleccionado),
                            borderColor = if(colorHex == colSeleccionado) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(mensaje: String, botonTexto: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClick) {
                Text(botonTexto)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                PrendaImagen(
                    imagenPath = prenda.imagenUri,
                    modifier = Modifier.fillMaxSize()
                )

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
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(colorCompose, CircleShape)
                            .padding(2.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape) // Añadí borde para visibilidad
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