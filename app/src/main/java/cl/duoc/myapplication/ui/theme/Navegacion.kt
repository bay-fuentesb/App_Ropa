package cl.duoc.myapplication.ui.theme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.viewmodel.RopaViewModel

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val ropaViewModel: RopaViewModel = viewModel()
    NavHost(navController, startDestination = "form") {
        composable("form") { UsuarioFormScreen(navController) }
        composable("inicio") { Inicio(navController) }
        composable("agregar") { AgregarRopa(navController, ropaViewModel= ropaViewModel) }
        composable("camara") { CamaraFotos() }
        composable("TuRopa"){
            MisPrendas(ropaViewModel=ropaViewModel, navController = navController)
        }
    }
}
