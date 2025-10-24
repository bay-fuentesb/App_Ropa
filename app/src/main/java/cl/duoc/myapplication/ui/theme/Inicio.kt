package cl.duoc.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import kotlin.random.Random
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.viewmodel.RopaViewModel
import cl.duoc.myapplication.repository.OutfitRepository
import cl.duoc.myapplication.model.OutfitSugerido
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.graphics.ImageDecoder
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri

@Composable
fun Inicio(navController: NavController? = null, ropaViewModel: RopaViewModel = viewModel()) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Outfits",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mostrar outfits del usuario si existen, sino mostrar placeholders
            val userOutfits = ropaViewModel.outfits
            val context = LocalContext.current

            if (userOutfits.isNotEmpty()) {
                // Mostrar hasta 3 outfits en fila
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    userOutfits.take(3).forEach { outfit ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = outfit.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
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
                                            Image(bitmap = bitmap.asImageBitmap(), contentDescription = prenda.titulo, modifier = Modifier.size(44.dp), contentScale = ContentScale.Crop)
                                        } else {
                                            Image(painter = painterResource(id = R.drawable.balenciaga), contentDescription = prenda.titulo, modifier = Modifier.size(44.dp), contentScale = ContentScale.Crop)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Placeholder con drawables de ejemplo
                val poleras = listOf(R.drawable.balenciaga, R.drawable.yeezy)
                val pantalones = listOf(R.drawable.balenciaga, R.drawable.yeezy)
                val zapatos = listOf(R.drawable.balenciaga, R.drawable.yeezy)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutfitItem(imageRes = poleras.random(), label = "Polera", modifier = Modifier.weight(1f))
                        OutfitItem(imageRes = pantalones.random(), label = "Pantalón", modifier = Modifier.weight(1f))
                        OutfitItem(imageRes = zapatos.random(), label = "Zapato", modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón IR A OUTFITS justo debajo de la sección de outfits y arriba de Mi Ropa
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(onClick = { navController?.navigate("outfits") }, modifier = Modifier.fillMaxWidth(0.9f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text(text = "Ir a Outfits", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Mi Ropa",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProductSmallCard(imageRes = R.drawable.balenciaga, modifier = Modifier.weight(1f))
                ProductSmallCard(imageRes = R.drawable.yeezy, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { navController?.navigate("miRopa") }, modifier = Modifier.fillMaxWidth(0.9f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text(text = "Ver mas", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController?.navigate("agregar") }, modifier = Modifier.fillMaxWidth(0.9f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text(text = "Agregar ropa", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun OutfitItem(imageRes: Int, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Card(
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ProductSmallCard(imageRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
