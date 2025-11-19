package cl.duoc.myapplication.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import cl.duoc.myapplication.R
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.viewmodel.RopaViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AgregarRopa(navController: androidx.navigation.NavController, ropaViewModel: RopaViewModel) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("#FFFFFF") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Accesorios", "Calcetines", "Chaqueta", "Jockey","Parka","Pantalones", "Polera", "Poleron", "Zapatilla")

    // --- helpers para cargar bitmap desde Uri ---
    fun loadBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val src = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            null
        }
    }

    // --- cámara / galería ---
    var cameraTempFile by remember { mutableStateOf<File?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraTempFile != null) {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", cameraTempFile!!)
            imageUri = uri
            bitmap = loadBitmap(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val file = File.createTempFile("agregar_prenda", ".jpg", context.cacheDir)
            file.deleteOnExit()
            cameraTempFile = file
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            takePictureLauncher.launch(uri)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) bitmap = loadBitmap(uri)
    }

    // ----------------------------
    // SELECTOR HSV (Hue slider + SV square)
    // ----------------------------
    // estado HSV
    var hue by remember { mutableStateOf(0f) }            // 0..360
    var sat by remember { mutableStateOf(1f) }            // 0..1
    var value by remember { mutableStateOf(1f) }          // 0..1

    // actualizar colorHex desde HSV
    fun hsvToColorInt(h: Float, s: Float, v: Float): Int {
        val hsv = floatArrayOf(h, s, v)
        return android.graphics.Color.HSVToColor(hsv)
    }
    fun intToHexString(colorInt: Int): String {
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }
    LaunchedEffect(hue, sat, value) {
        val cInt = hsvToColorInt(hue, sat, value)
        colorHex = intToHexString(cInt)
    }

    // Para scroll principal
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)            // <-- arregla el scroll
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("inicio") }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }

            Text(text = "Agregar prenda", fontSize = 20.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(),
                modifier = Modifier
                    .size(200.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (bitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.balenciaga),
                            contentDescription = "placeholder",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f)
                ) { Text("Cámara") }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) { Text("Galería") }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Categoria (dropdown)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Abrir categorías")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = {
                            category = cat
                            expanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Rueda/selector HSV UI ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Selecciona color", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Vista previa y hex
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(colorHex)))
                            .shadow(2.dp, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = colorHex, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SV box (saturation/value)
                val svBoxSize = 240.dp
                val svModifier = Modifier
                    .size(svBoxSize)
                    .clip(RoundedCornerShape(8.dp))

                Box(
                    modifier = svModifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.White, Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))))
                            )
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val px = change.position.x
                                val py = change.position.y
                                val w = size.width.toFloat()
                                val h = size.height.toFloat()
                                val s = (px / w).coerceIn(0f, 1f)
                                val v = (1f - (py / h)).coerceIn(0f, 1f)
                                sat = s
                                value = v
                            }
                        }
                ) {
                    // Black vertical overlay to control value
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black)
                                )
                            )
                    )

                    // Indicator circle
                    val indicatorX = (sat.coerceIn(0f,1f))
                    val indicatorY = (1f - value.coerceIn(0f,1f))
                    // position indicator using offset in pixels via Layout is more involved,
                    // we'll simply show a small legend below instead (keeps implementation robust).
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hue slider with rainbow background
                val rainbowColors = listOf(
                    Color(0xFFFF0000), Color(0xFFFF7F00), Color(0xFFFFFF00),
                    Color(0xFF00FF00), Color(0xFF00FFFF), Color(0xFF0000FF),
                    Color(0xFF8B00FF), Color(0xFFFF0000)
                )

                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush = Brush.horizontalGradient(rainbowColors))
                    )
                    Slider(
                        value = hue,
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, value))),
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo color como texto oculto (pero guardamos el hex)
            OutlinedTextField(
                value = colorHex,
                onValueChange = { hex ->
                    // permitir edición manual si quiere el usuario
                    colorHex = hex
                    try {
                        // actualizar hsv aproximado si el usuario escribe hex
                        val cInt = android.graphics.Color.parseColor(hex)
                        val hsv = FloatArray(3)
                        android.graphics.Color.colorToHSV(cInt, hsv)
                        hue = hsv[0]; sat = hsv[1]; value = hsv[2]
                    } catch (_: Exception) {}
                },
                label = { Text("Color (hex)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (imageUri != null && title.isNotBlank() && category.isNotBlank()) {
                        ropaViewModel.agregarPrenda(
                            Prenda(
                                titulo = title,
                                categoria = category,
                                color = colorHex,
                                imagenUri = imageUri.toString()
                            )
                        )
                        navController.navigate("miRopa")
                    } else {
                        val mensaje = when {
                            imageUri == null -> "Agrega una imagen"
                            title.isBlank() -> "Agrega un título"
                            category.isBlank() -> "Selecciona una categoría"
                            else -> "Completa todos los campos"
                        }
                        scope.launch { snackbarHostState.showSnackbar(mensaje) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Agregar", color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.height(40.dp)) // espacio final para scroll cómodo
        } // Column
    } // Surface
}
