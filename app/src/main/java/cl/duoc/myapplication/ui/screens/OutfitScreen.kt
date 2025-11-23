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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.ui.components.PrendaImagen // Asegúrate de importar tu componente
import cl.duoc.myapplication.viewmodel.RopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel = viewModel()
) {
    val outfitsUsuario = ropaViewModel.outfits

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Outfits", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        // Botón Flotante para crear nuevo outfit
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("crearOutfit") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, "Crear") },
                text = { Text("Nuevo Outfit") }
            )
        }
    ) { innerPadding ->

        if (outfitsUsuario.isEmpty()) {
            EmptyOutfitsView(
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp), // Espacio para el FAB
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título o espacio inicial
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(outfitsUsuario) { outfit ->
                    OutfitSuggestedCard(
                        outfit = outfit,
                        onDeleteClick = {
                            ropaViewModel.eliminarOutfit(outfit)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyOutfitsView(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.yeezy), // O un icono de guardarropa
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu colección está vacía",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Crea combinaciones únicas con tu ropa.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("crearOutfit") }) {
            Text("Crear mi primer Outfit")
        }
        TextButton(onClick = { navController.navigate("outfitSugerido") }) {
            Text("Generar uno aleatorio")
        }
    }
}

@Composable
fun OutfitSuggestedCard(
    outfit: OutfitSugerido,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // HEADER DE LA TARJETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = outfit.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${outfit.combinacion.size} prendas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar outfit",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CARRUSEL DE IMÁGENES (LazyRow para scroll horizontal)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(outfit.combinacion) { prenda ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    ) {
                        // AQUI USAMOS TU COMPONENTE MEJORADO
                        PrendaImagen(
                            imagenPath = prenda.imagenUri,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // LISTA DETALLADA PEQUEÑA
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                outfit.combinacion.take(3).forEach { prenda ->
                    OutfitPrendaItem(prenda)
                }
                if (outfit.combinacion.size > 3) {
                    Text(
                        text = "+ ${outfit.combinacion.size - 3} prendas más...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 28.dp) // Alinear con el texto del item
                    )
                }
            }
        }
    }
}

@Composable
fun OutfitPrendaItem(prenda: Prenda) {
    val colorCircle = try {
        Color(android.graphics.Color.parseColor(prenda.color))
    } catch (_: Exception) {
        Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Bolita de color
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(colorCircle, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = prenda.titulo,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = prenda.categoria,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}