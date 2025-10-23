package cl.duoc.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.viewmodel.RopaViewModel
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import android.graphics.BitmapFactory
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.repository.OutfitRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun MisPrendas(
    ropaViewModel: RopaViewModel = viewModel(),
    navController: NavController
) {
    val prendas = ropaViewModel.prendas
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados para el filtro
    var mostrarFiltro by remember { mutableStateOf(false) }
    var categoriaFiltro by remember { mutableStateOf("Todas") }
    val categorias = listOf("Todas", "Polera", "Poleron", "Zapatilla", "Calcetines", "Accesorios", "Jockeys", "Chaqueta", "Parka")

    // Estados para outfits
    var outfitSugerido by remember { mutableStateOf<OutfitSugerido?>(null) }
    var mostrarOutfit by remember { mutableStateOf(false) }
    val outfitRepository = OutfitRepository()

    // Filtrar prendas según la categoría seleccionada
    val prendasFiltradas = if (categoriaFiltro == "Todas") {
        prendas
    } else {
        prendas.filter { it.categoria == categoriaFiltro }
    }

    if (prendas.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No tienes prendas en tu inventario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate("agregar") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Agregar ropa", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate("inicio") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Volver al Inicio", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header con botones de navegación y filtro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Inicio
                IconButton(
                    onClick = { navController.navigate("inicio") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Volver al inicio"
                    )
                }

                // Título centrado
                Text(
                    text = "Mi Ropa",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Botón de sugerencia de outfit
                IconButton(
                    onClick = {
                        scope.launch {
                            val outfit = outfitRepository.generarOutfitAleatorio(prendas)
                            outfitSugerido = outfit
                            mostrarOutfit = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "Sugerir outfit"
                    )
                }

                // Botón agregar más prendas
                IconButton(
                    onClick = { navController.navigate("agregar") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar ropa"
                    )
                }

                // Botón de filtro
                IconButton(
                    onClick = { mostrarFiltro = !mostrarFiltro }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Filtrar por categoría"
                    )
                }
            }

            // Mostrar outfit sugerido si existe
            if (mostrarOutfit && outfitSugerido != null) {
                OutfitSugeridoCard(
                    outfit = outfitSugerido!!,
                    onDismiss = { mostrarOutfit = false }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Mostrar filtro actual si no es "Todas"
            if (categoriaFiltro != "Todas") {
                Text(
                    text = "Filtro: $categoriaFiltro",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Menú desplegable de filtros
            if (mostrarFiltro) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = categoriaFiltro,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por categoría") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.List,
                                    contentDescription = "Abrir categorías"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaFiltro = categoria
                                    expanded = false
                                    mostrarFiltro = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botón para ver más outfits sugeridos
            if (prendas.size >= 2) {
                Button(
                    onClick = {
                        navController.navigate("outfits")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Ver Todos los Outfits Sugeridos")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Lista de prendas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(prendasFiltradas) { prenda ->
                    PrendaCard(prenda = prenda, context = context)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun PrendaCard(prenda: cl.duoc.myapplication.model.Prenda, context: android.content.Context) {
    val uri = try { prenda.imagenUri.toUri() } catch (_: Exception) { null }
    val bitmap = remember(uri) {
        try {
            if (uri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val src = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(src)
                } else {
                    val stream = context.contentResolver.openInputStream(uri)
                    stream.use { BitmapFactory.decodeStream(it) }
                }
            } else null
        } catch (_: Exception) { null }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = prenda.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.balenciaga),
                    contentDescription = prenda.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Título: ${prenda.titulo}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Categoría: ${prenda.categoria}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Color: ${prenda.color}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun OutfitSugeridoCard(outfit: OutfitSugerido, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ Outfit Sugerido",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(id = R.drawable.balenciaga), // Puedes cambiar por un icono de cerrar
                        contentDescription = "Cerrar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = outfit.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Prendas incluidas:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )

            outfit.combinacion.forEach { prenda ->
                Text(
                    text = "• ${prenda.titulo} (${prenda.categoria})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = outfit.motivo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = when (outfit.puntuacion) {
                    in 8..10 -> MaterialTheme.colorScheme.primary
                    in 5..7 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.tertiary
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Puntuación: ${outfit.puntuacion}/10",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}