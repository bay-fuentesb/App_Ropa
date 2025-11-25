package cl.duoc.myapplication.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import cl.duoc.myapplication.R
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.ui.utils.ColorUtils
import cl.duoc.myapplication.ui.utils.ImageUtils
import cl.duoc.myapplication.viewmodel.RopaViewModel
import cl.duoc.myapplication.ui.utils.Constants // ✅ Importante
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
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

    // ✅ USAMOS LA LISTA CENTRALIZADA
    val categories = Constants.CATEGORIAS

    // --- cámara / galería ---
    var cameraTempFile by remember { mutableStateOf<File?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraTempFile != null) {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", cameraTempFile!!)
            imageUri = uri
            bitmap = ImageUtils.loadBitmap(context, uri)
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
        if (uri != null) {
            bitmap = ImageUtils.loadBitmap(context, uri)
        }
    }

    // --- HSV Lógica ---
    var hue by remember { mutableStateOf(0f) }
    var sat by remember { mutableStateOf(1f) }
    var value by remember { mutableStateOf(1f) }

    LaunchedEffect(hue, sat, value) {
        val cInt = ColorUtils.hsvToColorInt(hue, sat, value)
        colorHex = ColorUtils.intToHexString(cInt)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agregar prenda", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("inicio") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta de Imagen
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(),
                modifier = Modifier.size(170.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Botones Cámara/Galería
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Cámara") }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Galería") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de Texto
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown Categoría
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Abrir")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true }
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

            Spacer(modifier = Modifier.height(16.dp))

            // SECCIÓN COLOR
            Text(
                text = "Selecciona color",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(colorHex))).border(1.dp, Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = colorHex,
                    onValueChange = { hex ->
                        colorHex = hex
                        try {
                            val cInt = android.graphics.Color.parseColor(hex)
                            val hsv = FloatArray(3)
                            android.graphics.Color.colorToHSV(cInt, hsv)
                            hue = hsv[0]; sat = hsv[1]; value = hsv[2]
                        } catch (_: Exception) {}
                    },
                    label = { Text("Código Hex") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector Visual de Color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush = Brush.horizontalGradient(colors = listOf(Color.White, Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))))))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val px = change.position.x
                            val py = change.position.y
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            sat = (px / w).coerceIn(0f, 1f)
                            value = (1f - (py / h)).coerceIn(0f, 1f)
                        }
                    }
            ) {
                Box(modifier = Modifier.matchParentSize().background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black))))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slider Arcoiris
            Box(
                modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp)).background(brush = Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)))
            )
            Slider(
                value = hue,
                onValueChange = { hue = it },
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth().offset(y = (-10).dp),
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.Transparent, inactiveTrackColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (imageUri != null && title.isNotBlank() && category.isNotBlank()) {
                        val rutaImagenGuardada = ImageUtils.copiarImagenAlmacenamiento(context, imageUri!!)
                        if (rutaImagenGuardada != null) {
                            ropaViewModel.agregarPrenda(
                                Prenda(
                                    titulo = title,
                                    categoria = category,
                                    color = colorHex,
                                    imagenUri = rutaImagenGuardada
                                )
                            )
                            navController.navigate("miRopa")
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Error al guardar imagen") }
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Faltan datos") }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Prenda", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}