package cl.duoc.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import cl.duoc.myapplication.R

@Composable
fun Imagen(){

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "pagina numero 1")
        Text(text = "polero balenciaga")
        Image(
            painter = painterResource(id = R.drawable.balenciaga),
            contentDescription = "imagen poleron balenciaga",
            contentScale = ContentScale.Crop
        )
    }
}