package cl.duoc.myapplication.ui.theme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "form") {
        composable("form") { UsuarioFormScreen(navController) }
        composable("inicio") { Inicio(navController) }
        composable("agregar") { AgregarRopa(navController) }
        composable("camara") { CamaraFotos() }
    }
}
