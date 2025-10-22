package cl.duoc.myapplication.ui.theme
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.viewmodel.RopaViewModel
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import android.graphics.BitmapFactory
import androidx.compose.ui.Alignment

@Composable
fun MisPrendas(
    ropaViewModel: RopaViewModel = viewModel(), navController: NavController) {
    val prendas = ropaViewModel.prendas
    val context = LocalContext.current

    if (prendas.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No tienes prendas en tu inventario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("agregar") }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text(text = "Agregar ropa", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate("inicio") }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text(text = "Volver al Inicio", color = MaterialTheme.colorScheme.onSecondary)
            }
        }

    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text(
                    text = "Tu Ropa ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(prendas) { prenda ->
                val uri = try { prenda.imagenUri.toUri() } catch (_: Exception) { null }
                val bitmap = try {
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

                Spacer(modifier = Modifier.height(4.dp))

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
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.balenciaga),
                                contentDescription = prenda.titulo,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text("Titulo: ${prenda.titulo}", style = MaterialTheme.typography.titleMedium)
                        Text("Categoría: ${prenda.categoria}")
                        Text("Color: ${prenda.color}")

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = { navController.navigate("inicio") },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text(
                                text = "Volver al Inicio",
                                color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}