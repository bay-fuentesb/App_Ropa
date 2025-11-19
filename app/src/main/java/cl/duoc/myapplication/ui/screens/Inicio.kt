package cl.duoc.myapplication.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.viewmodel.RopaViewModel
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.graphics.ImageDecoder
import android.graphics.BitmapFactory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.ui.components.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

@Composable
fun Inicio(navController: NavController? = null,
           ropaViewModel: RopaViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val usuarioLogueado = remember {mutableStateOf<String?>(null)}

    //cargar usuario al iniciar
    LaunchedEffect(Unit){
        usuarioLogueado.value = sessionManager.getCurrentUser()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // Header con información de usuario y botón de cerrar sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Información del usuario logueado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person), // Necesitarás agregar este icono
                        contentDescription = "Usuario",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Bienvenido",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = usuarioLogueado.value?: "Usuario",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón de cerrar sesión
                IconButton(
                    onClick = {
                        scope.launch{
                            sessionManager.logout()
                            navController?.navigate("login"){
                                popUpTo("inicio"){ inclusive = true }
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout), // Necesitarás agregar este icono
                        contentDescription = "Cerrar sesión",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Outfits con descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 Tus Outfits",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Revisa y gestiona tus outfits guardados. Crea nuevas combinaciones y organiza tu estilo.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val userOutfits = ropaViewModel.outfits
                    val context = LocalContext.current

                    if (userOutfits.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            userOutfits.take(3).forEach { outfit ->
                                OutfitCard(outfit = outfit, context = context)
                            }
                        }
                    } else {
                        // Outfit aleatorio de ejemplo
                        RandomOutfitCard()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController?.navigate("outfits") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Ver todos mis outfits", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección de Mi Ropa con descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👕 Mi Armario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Administra tu colección de ropa. Agrega nuevas prendas, organiza por categorías y mantén tu armario actualizado.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProductSmallCard(imageRes = R.drawable.balenciaga, modifier = Modifier.weight(1f))
                        ProductSmallCard(imageRes = R.drawable.yeezy, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController?.navigate("miRopa") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = "Ver todo mi armario", color = MaterialTheme.colorScheme.onPrimary)
                        }

                        Button(
                            onClick = { navController?.navigate("agregar") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(text = "➕ Agregar nueva prenda", color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutfitCard(outfit: OutfitSugerido, context: Context) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = outfit.nombre,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                outfit.combinacion.take(3).forEach { prenda ->
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

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = prenda.titulo,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.balenciaga),
                            contentDescription = prenda.titulo,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RandomOutfitCard() {
    val poleras = listOf(R.drawable.balenciaga, R.drawable.yeezy)
    val pantalones = listOf(R.drawable.balenciaga, R.drawable.yeezy)
    val zapatos = listOf(R.drawable.balenciaga, R.drawable.yeezy)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crea tu primer outfit!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutfitItem(imageRes = poleras.random(), label = "Polera", modifier = Modifier.weight(1f))
                OutfitItem(imageRes = pantalones.random(), label = "Pantalón", modifier = Modifier.weight(1f))
                OutfitItem(imageRes = zapatos.random(), label = "Zapato", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProductSmallCard(imageRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun OutfitItem(imageRes: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Card(
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(2.dp)
            ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                modifier = Modifier.fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer (modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}