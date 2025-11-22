package cl.duoc.myapplication.ui.components

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.myapplication.R
import cl.duoc.myapplication.ui.utils.ImageUtils
import java.io.File

// --- AGREGA ESTA NUEVA FUNCIÓN AL FINAL DE TU ARCHIVO MiRopa.kt ---
@Composable
fun PrendaImagen(imagenPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(imagenPath) {
        try {
            val file = File(imagenPath)
            if (file.exists()) {
                val bitmapBruto = BitmapFactory.decodeFile(file.absolutePath)
                // AQUI LA MAGIA: Corregimos la rotación antes de mostrar
                bitmap = ImageUtils.corregirRotacion(file.absolutePath, bitmapBruto)
            } else {
                // Fallback para URIs antiguas
                val uri = android.net.Uri.parse(imagenPath)
                bitmap = ImageUtils.loadBitmap(context, uri)
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.balenciaga),
            contentDescription = "Placeholder",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

// 2. TARJETA DE ESTADÍSTICAS (Movida desde Inicio) 📊
@Composable
fun StatCard(title: String, count: String, icon: Int, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp), contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = count, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// 3. TARJETA DE ESTADO VACÍO (Movida desde Inicio) ⭕
@Composable
fun EmptyStateCard(text: String, buttonText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onClick) { Text(buttonText) }
        }
    }
}
