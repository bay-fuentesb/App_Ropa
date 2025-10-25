package cl.duoc.myapplication.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.repository.OutfitRepository
import cl.duoc.myapplication.viewmodel.RopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOutfitScreen(
    navController: NavController,
    ropaViewModel: RopaViewModel = viewModel()
) {
    val prendas = ropaViewModel.prendas
    val outfitRepository = OutfitRepository()
    val context = LocalContext.current

    val superiores = prendas.filter { it.categoria == "Polera" || it.categoria == "Poleron" || it.categoria == "Chaqueta" || it.categoria == "Parka" }
    val inferiores = prendas.filter { it.categoria == "Pantalones" }
    val calzados = prendas.filter { it.categoria == "Zapatilla" }
    val accesorios = prendas.filter { it.categoria == "Accesorios" }
    val jockeys = prendas.filter { it.categoria == "Jockey" || it.categoria == "Jockeys" }

    var outfitName by remember { mutableStateOf("") }

    var expandedSup by remember { mutableStateOf(false) }
    var expandedInf by remember { mutableStateOf(false) }
    var expandedCal by remember { mutableStateOf(false) }
    var expandedAcc by remember { mutableStateOf(false) }
    var expandedJock by remember { mutableStateOf(false) }

    var selectedSup by remember { mutableStateOf<cl.duoc.myapplication.model.Prenda?>(null) }
    var selectedInf by remember { mutableStateOf<cl.duoc.myapplication.model.Prenda?>(null) }
    var selectedCal by remember { mutableStateOf<cl.duoc.myapplication.model.Prenda?>(null) }
    var selectedAcc by remember { mutableStateOf<cl.duoc.myapplication.model.Prenda?>(null) }
    var selectedJock by remember { mutableStateOf<cl.duoc.myapplication.model.Prenda?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear Outfit") },
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
            Text(text = "Selecciona prendas para tu outfit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = outfitName,
                onValueChange = { outfitName = it },
                label = { Text("Nombre del outfit (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = selectedSup?.titulo ?: "Seleccionar superior",
                onValueChange = {},
                readOnly = true,
                label = { Text("Superior") },
                trailingIcon = { IconButton(onClick = { expandedSup = !expandedSup }) { Icon(Icons.Filled.Create, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedSup, onDismissRequest = { expandedSup = false }) {
                DropdownMenuItem(text = { Text("Ninguno") }, onClick = { selectedSup = null; expandedSup = false })
                superiores.forEach { pr -> DropdownMenuItem(text = { Text(pr.titulo) }, onClick = { selectedSup = pr; expandedSup = false }) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = selectedInf?.titulo ?: "Seleccionar inferior",
                onValueChange = {},
                readOnly = true,
                label = { Text("Inferior") },
                trailingIcon = { IconButton(onClick = { expandedInf = !expandedInf }) { Icon(Icons.Filled.Create, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedInf, onDismissRequest = { expandedInf = false }) {
                DropdownMenuItem(text = { Text("Ninguno") }, onClick = { selectedInf = null; expandedInf = false })
                inferiores.forEach { pr -> DropdownMenuItem(text = { Text(pr.titulo) }, onClick = { selectedInf = pr; expandedInf = false }) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = selectedCal?.titulo ?: "Seleccionar calzado",
                onValueChange = {},
                readOnly = true,
                label = { Text("Calzado") },
                trailingIcon = { IconButton(onClick = { expandedCal = !expandedCal }) { Icon(Icons.Filled.Create, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedCal, onDismissRequest = { expandedCal = false }) {
                DropdownMenuItem(text = { Text("Ninguno") }, onClick = { selectedCal = null; expandedCal = false })
                calzados.forEach { pr -> DropdownMenuItem(text = { Text(pr.titulo) }, onClick = { selectedCal = pr; expandedCal = false }) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = selectedJock?.titulo ?: "Seleccionar jockey (opcional)",
                onValueChange = {},
                readOnly = true,
                label = { Text("Jockey") },
                trailingIcon = { IconButton(onClick = { expandedJock = !expandedJock }) { Icon(Icons.Filled.Create, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedJock, onDismissRequest = { expandedJock = false }) {
                DropdownMenuItem(text = { Text("Ninguno") }, onClick = { selectedJock = null; expandedJock = false })
                jockeys.forEach { pr -> DropdownMenuItem(text = { Text(pr.titulo) }, onClick = { selectedJock = pr; expandedJock = false }) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = selectedAcc?.titulo ?: "Seleccionar accesorio (opcional)",
                onValueChange = {},
                readOnly = true,
                label = { Text("Accesorio") },
                trailingIcon = { IconButton(onClick = { expandedAcc = !expandedAcc }) { Icon(Icons.Filled.Create, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expandedAcc, onDismissRequest = { expandedAcc = false }) {
                DropdownMenuItem(text = { Text("Ninguno") }, onClick = { selectedAcc = null; expandedAcc = false })
                accesorios.forEach { pr -> DropdownMenuItem(text = { Text(pr.titulo) }, onClick = { selectedAcc = pr; expandedAcc = false }) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val combinacion = listOfNotNull(selectedSup, selectedInf, selectedCal, selectedJock, selectedAcc)
                    if (combinacion.size >= 2) {
                        val name = if (outfitName.isNotBlank()) outfitName else "Outfit Manual"
                        val outfit = OutfitSugerido(
                            nombre = name,
                            combinacion = combinacion,

                        )
                        ropaViewModel.agregarOutfit(outfit)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Selecciona al menos 2 prendas", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Crear")
                }

                Button(onClick = {
                    outfitRepository.generarOutfitAleatorio(prendas)?.let { outfit ->
                        val name = if (outfitName.isNotBlank()) outfitName else "Outfit Aleatorio"
                        val outfitNamed = outfit.copy(nombre = name)
                        ropaViewModel.agregarOutfit(outfitNamed)
                        navController.popBackStack()
                    }
                }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                    Text("Aleatorio")
                }
            }
        }
    }
}
