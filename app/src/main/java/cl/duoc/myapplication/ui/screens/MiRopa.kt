package cl.duoc.myapplication.ui.screens

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.net.toUri
import cl.duoc.myapplication.R
import cl.duoc.myapplication.viewmodel.RopaViewModel

@Composable
fun MisPrendas(
    ropaViewModel: RopaViewModel = viewModel(),
    navController: androidx.navigation.NavController
) {
    val prendas = ropaViewModel.prendas
    val context = LocalContext.current

    var mostrarFiltro by remember { mutableStateOf(false) }
    var categoriaFiltro by remember { mutableStateOf("Todas") }

    val categorias = listOf(
        "Todas", "Polera", "Poleron", "Zapatilla", "Calcetines",
        "Accesorios", "Jockeys", "Chaqueta", "Parka", "Pantalones"
    )

    val prendasFiltradas = if (categoriaFiltro == "Todas") prendas
    else prendas.filter { it.categoria == categoriaFiltro }

    Column(modifier = Modifier.fillMaxSize()) {

        // --------------------- TOP BAR ------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("inicio") }) {
                Icon(Icons.Filled.Home, contentDescription = "Inicio")
            }

            Text(
                text = "Mi Ropa",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            IconButton(onClick = { navController.navigate("outfits") }) {
                Icon(Icons.Filled.Create, contentDescription = "Outfits")
            }

            IconButton(onClick = { navController.navigate("agregar") }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }

            IconButton(onClick = { mostrarFiltro = !mostrarFiltro }) {
                Icon(Icons.Filled.List, contentDescription = "Filtrar")
            }
        }

        // --------------------- FILTRO ------------------------
        if (mostrarFiltro) {
            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = categoriaFiltro,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por categoría") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.List, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                categoriaFiltro = it
                                expanded = false
                                mostrarFiltro = false
                            }
                        )
                    }
                }
            }
        }

        // --------------------- LISTA DE PRENDAS ------------------------
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(prendasFiltradas) { prenda ->
                PrendaCard(prenda = prenda, context = context)
            }

            item {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("outfitSugerido") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Generar Outfit Aleatorio")
                    }

                    // Espacio entre botones más pequeño
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.navigate("crearOutfit") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Crear Outfit Personalizado")
                    }
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
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            } else null
        } catch (_: Exception) { null }
    }

    val colorCompose = try {
        Color(android.graphics.Color.parseColor(prenda.color))
    } catch (_: Exception) {
        Color.DarkGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Imagen
            if (bitmap != null)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            else
                Image(
                    painter = painterResource(R.drawable.balenciaga),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text("Título: ${prenda.titulo}", fontWeight = FontWeight.Bold)
                    Text("Categoría: ${prenda.categoria}")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(colorCompose, CircleShape)
                    )
                    Text(
                        prenda.color,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
