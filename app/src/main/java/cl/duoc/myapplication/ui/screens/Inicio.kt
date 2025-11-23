package cl.duoc.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.ui.components.EmptyStateCard
import cl.duoc.myapplication.ui.components.PrendaImagen
import cl.duoc.myapplication.ui.components.SessionManager
import cl.duoc.myapplication.ui.components.StatCard
import cl.duoc.myapplication.viewmodel.RopaViewModel
import kotlinx.coroutines.launch

@Composable
fun Inicio(
    navController: NavController? = null,
    ropaViewModel: RopaViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val usuarioLogueado = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        usuarioLogueado.value = sessionManager.getCurrentUser()
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Padding general lateral
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp) // Más espacio entre secciones
        ) {
            // 1. HEADER (Saludo)
            item {
                HeaderSaludo(
                    nombreUsuario = usuarioLogueado.value,
                    onLogout = {
                        scope.launch {
                            sessionManager.logout()
                            navController?.navigate("login") { popUpTo("inicio") { inclusive = true } }
                        }
                    }
                )
            }

            // 2. ESTADÍSTICAS
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 🔥 Nuevo Título y Leyenda
                    SectionHeader(
                        title = "Resumen de Estilo",
                        description = "Un vistazo rápido a la cantidad de prendas y combinaciones que has creado."
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Prendas",
                            count = ropaViewModel.prendas.size.toString(),
                            icon = R.drawable.balenciaga, // Asegúrate que este recurso exista
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Outfits",
                            count = ropaViewModel.outfits.size.toString(),
                            icon = R.drawable.yeezy, // Asegúrate que este recurso exista
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. OUTFITS DESTACADOS
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 🔥 Nuevo Título y Leyenda
                    SectionHeader(
                        title = "Tus Outfits Destacados",
                        description = "Revisa las combinaciones favoritas que has guardado para inspirarte hoy."
                    )

                    val userOutfits = ropaViewModel.outfits

                    if (userOutfits.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp) // Pequeño padding interno
                        ) {
                            items(userOutfits) { outfit ->
                                OutfitCard(outfit = outfit)
                            }
                            item {
                                VerTodosCard { navController?.navigate("outfits") }
                            }
                        }
                    } else {
                        EmptyStateCard(
                            text = "Aún no tienes outfits creados",
                            buttonText = "Crear mi primer Outfit",
                            onClick = { navController?.navigate("outfits") }
                        )
                    }
                }
            }

            // 4. MI ARMARIO
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 🔥 Nuevo Título y Leyenda
                    SectionHeader(
                        title = "Gestión de Armario",
                        description = "Sube tus nuevas compras o revisa todo tu inventario de ropa disponible."
                    )

                    CardArmario(
                        onAgregar = { navController?.navigate("agregar") },
                        onVerTodo = { navController?.navigate("miRopa") }
                    )
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

// 🔥 COMPONENTE NUEVO: Título con descripción
@Composable
private fun SectionHeader(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Color gris suave
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )
    }
}

@Composable
private fun HeaderSaludo(nombreUsuario: String?, onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "¡Hola de nuevo!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = nombreUsuario ?: "Usuario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        IconButton(
            onClick = onLogout,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.ExitToApp, "Cerrar sesión")
        }
    }
}

@Composable
private fun OutfitCard(outfit: OutfitSugerido) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp), // Un poco más alto para mejor proporción
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val prendasMuestra = outfit.combinacion.take(4) // Intenta mostrar hasta 4
                if (prendasMuestra.isNotEmpty()) {
                    // Grid simple de 2x2 o fila dependiendo de la cantidad
                    Row(modifier = Modifier.fillMaxSize()) {
                        prendasMuestra.take(2).forEach { prenda ->
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(1.dp)) {
                                PrendaImagen(
                                    imagenPath = prenda.imagenUri,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.balenciaga), null, tint = Color.Gray)
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = outfit.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${outfit.combinacion.size} prendas",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CardArmario(onAgregar: () -> Unit, onVerTodo: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "¿Nueva compra?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Agrega esa prenda a tu colección.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón pequeño para agregar rápido
                    Button(
                        onClick = onAgregar,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar")
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.balenciaga),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))


            OutlinedButton(
                onClick = onVerTodo,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver todo mi armario")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun VerTodosCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(220.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Ver todos",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}