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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.ui.components.EmptyStateCard
import cl.duoc.myapplication.ui.components.PrendaImagen // <-- Importamos el componente inteligente
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. HEADER
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

            // 2. ESTADÍSTICAS (Usando componente compartido)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Prendas",
                        count = ropaViewModel.prendas.size.toString(),
                        icon = R.drawable.balenciaga,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Outfits",
                        count = ropaViewModel.outfits.size.toString(),
                        icon = R.drawable.yeezy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. OUTFITS DESTACADOS
            item {
                SectionTitle(title = "Tus Outfits Destacados")
                val userOutfits = ropaViewModel.outfits

                if (userOutfits.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
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
                        buttonText = "Crear Outfit",
                        onClick = { navController?.navigate("outfits") }
                    )
                }
            }

            // 4. MI ARMARIO
            item {
                SectionTitle(title = "Gestión de Armario")
                CardArmario(
                    onAgregar = { navController?.navigate("agregar") },
                    onVerTodo = { navController?.navigate("miRopa") }
                )
            }

            //Solo para pruebas de API Nube
            /*item{
                Button(
                    onClick = { navController?.navigate("testApi")},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                ){
                    Text("Test API Nube")
                }
            }*/


        }
    }
}

// --- COMPONENTES PRIVADOS (Solo usados en Inicio) ---

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
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Face, "Avatar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Hola, de nuevo! 👋", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(nombreUsuario ?: "Invitado", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        IconButton(
            onClick = onLogout,
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, "Salir")
        }
    }
}

@Composable
private fun OutfitCard(outfit: OutfitSugerido) {
    Card(
        modifier = Modifier.width(160.dp).height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)) {
                val prendasMuestra = outfit.combinacion.take(2)
                if (prendasMuestra.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        prendasMuestra.forEach { prenda ->
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(1.dp)) {
                                // AQUÍ USAMOS EL COMPONENTE QUE ARREGLA LA ROTACIÓN 👇
                                PrendaImagen(imagenPath = prenda.imagenUri, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(outfit.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${outfit.combinacion.size} prendas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CardArmario(onAgregar: () -> Unit, onVerTodo: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("¿Nueva compra?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Agrega tus prendas nuevas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onAgregar, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                    Text("Agregar Prenda")
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(painter = painterResource(id = R.drawable.balenciaga), contentDescription = null, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        TextButton(onClick = onVerTodo, modifier = Modifier.fillMaxWidth().padding(8.dp)) { Text("Ver todo mi armario") }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
}

@Composable
private fun VerTodosCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(100.dp).height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Star, contentDescription = null)
            Text("Ver todos", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}