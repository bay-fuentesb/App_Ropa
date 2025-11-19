package cl.duoc.myapplication.ui.screens

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.net.toUri
import cl.duoc.myapplication.viewmodel.RopaViewModel
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOutfitScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel
) {
    val prendas = ropaViewModel.prendas
    val context = LocalContext.current

    val prendasSeleccionadas = remember { mutableStateListOf<Prenda>() }
    var nombreOutfit by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Outfit") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = nombreOutfit,
                onValueChange = { nombreOutfit = it },
                label = { Text("Nombre del outfit") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Selecciona prendas:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(prendas) { prenda ->

                    val isSelected = prendasSeleccionadas.contains(prenda)

                    PrendaSeleccionableCard(
                        prenda = prenda,
                        context = context,
                        seleccionado = isSelected,
                        onClick = {
                            if (isSelected) prendasSeleccionadas.remove(prenda)
                            else prendasSeleccionadas.add(prenda)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (nombreOutfit.isNotBlank() && prendasSeleccionadas.isNotEmpty()) {
                        ropaViewModel.agregarOutfit(
                            OutfitSugerido(
                                nombre = nombreOutfit,
                                combinacion = prendasSeleccionadas.toList()
                            )
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar outfit")
            }
        }
    }
}

@Composable
fun PrendaSeleccionableCard(
    prenda: Prenda,
    context: android.content.Context,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val uri = try { prenda.imagenUri.toUri() } catch (_: Exception) { null }

    val bitmap = remember(uri) {
        try {
            if (uri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val src = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(src)
                } else {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            } else null
        } catch (_: Exception) { null }
    }

    val colorParse = try {
        Color(android.graphics.Color.parseColor(prenda.color))
    } catch (_: Exception) {
        Color.DarkGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (bitmap != null)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )
            else
                Image(
                    painter = painterResource(R.drawable.balenciaga),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(prenda.titulo, style = MaterialTheme.typography.titleMedium)
                Text(prenda.categoria)
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(colorParse, CircleShape)
            )
        }
    }
}
