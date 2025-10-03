package cl.duoc.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.myapplication.R

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "pagina1") {
        composable("pagina1") { Pagina1(navController) }
        composable("pagina2") { Pagina2(navController) }
    }
}

@Composable
fun pagina1 (navController: NavController)){

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
        Button(onClick = { navController.navigate("pagina2") }) {
            Text(text = "ver poleron YEEZY")

        }
    }

}

@Composable
fun pagina2 (navController: NavController)){

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "pagina numero 2")
        Text(text = "polero yeezy")
        Image(
            painter = painterResource(id = R.drawable.yeezy),
            contentDescription = "imagen poleron yeezy",
            contentScale = ContentScale.Crop
        )
        Button(onClick = { navController.navigate("pagina1") }) {
            Text(text = "ver poleron BALENCIAGA")
        }
    }
}
