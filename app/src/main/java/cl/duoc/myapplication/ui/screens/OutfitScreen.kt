package cl.duoc.myapplication.ui.screens

import android.os.Build
import android.graphics.ImageDecoder
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.R
import cl.duoc.myapplication.model.OutfitSugerido
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
            CenterAlignedTopAppBar(
                title = { Text("Outfits") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("crearOutfit") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Crear outfit")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (outfitsUsuario.isEmpty()) {
                EmptyOutfitsView(navController)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(outfitsUsuario) { outfit ->
                        OutfitSuggestedCard(outfit)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyOutfitsView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aún no hay outfits — crea uno o agrega prendas",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("agregar") }) {
            Text("Agregar Prenda")
        }
    }
}

@Composable
fun OutfitSuggestedCard(outfit: OutfitSugerido) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Título del outfit
            Text(
                text = outfit.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Miniaturas de las prendas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                outfit.combinacion.forEach { prenda ->
                    val uri = try { prenda.imagenUri.toUri() } catch (_: Exception) { null }

                    val bitmap = remember(uri) {
                        try {
                            if (uri != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                                    ImageDecoder.decodeBitmap(source)
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
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.balenciaga),
                            contentDescription = prenda.titulo,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de prendas con color real
            Text(
                text = "Prendas incluidas:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            outfit.combinacion.forEach { prenda ->

                val colorCircle = try {
                    Color(android.graphics.Color.parseColor(prenda.color))
                } catch (_: Exception) {
                    Color.Transparent
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colorCircle, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = prenda.titulo,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Categoría: ${prenda.categoria}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Acción futura */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar este outfit")
            }
        }
    }
}
