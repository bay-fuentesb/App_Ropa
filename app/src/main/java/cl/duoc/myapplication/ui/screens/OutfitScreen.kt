package cl.duoc.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.repository.OutfitRepository
import cl.duoc.myapplication.viewmodel.RopaViewModel
import android.graphics.ImageDecoder
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import cl.duoc.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel = viewModel()
) {
    val prendas = ropaViewModel.prendas
    val outfitRepository = OutfitRepository()
    val scope = rememberCoroutineScope()

    val outfitsSugeridosState = remember { mutableStateOf<List<OutfitSugerido>>(emptyList()) }



    val userOutfits = ropaViewModel.outfits

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
            // Botones: crear outfit y mostrar sugerencias a demanda
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate("crearOutfit") }, modifier = Modifier.weight(1f)) {
                    Text("Crear outfit")
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            val displayList: List<OutfitSugerido> = (userOutfits + outfitsSugeridosState.value)

            if (displayList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aún no hay outfits — crea uno o agrega prendas",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("agregar") }) {
                        Text("Agregar Prenda")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayList) { outfit ->
                        OutfitSugeridoCompletoCard(outfit = outfit)
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitSugeridoCompletoCard(outfit: OutfitSugerido) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = outfit.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                outfit.combinacion.forEach { prenda ->
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
                                .size(64.dp)
                                .weight(1f)
                        )
                    } else {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.balenciaga),
                            contentDescription = prenda.titulo,
                            modifier = Modifier
                                .size(64.dp)
                                .weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Prendas incluidas:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            outfit.combinacion.forEach { prenda ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "• ${prenda.titulo}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "  Categoría: ${prenda.categoria} | Color: ${prenda.color}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Acción para guardar o usar el outfit */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Usar este Outfit")
            }
        }
    }
}
