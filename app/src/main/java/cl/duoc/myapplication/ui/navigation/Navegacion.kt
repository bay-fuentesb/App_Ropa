package cl.duoc.myapplication.ui.navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.ui.components.SessionManager
import cl.duoc.myapplication.ui.screens.LoginScreen
import cl.duoc.myapplication.ui.screens.Inicio
import cl.duoc.myapplication.ui.screens.UsuarioFormScreen
import cl.duoc.myapplication.ui.screens.AgregarRopa
import cl.duoc.myapplication.ui.screens.CamaraFotos
import cl.duoc.myapplication.ui.screens.CrearOutfitScreen
import cl.duoc.myapplication.ui.screens.MisPrendas
import cl.duoc.myapplication.ui.screens.OutfitSugeridoScreen
import cl.duoc.myapplication.ui.screens.OutfitsScreen
import cl.duoc.myapplication.ui.screens.Recomendaciones
import cl.duoc.myapplication.viewmodel.RopaViewModel
import cl.duoc.myapplication.viewmodel.UsuarioFormViewModel

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val ropaViewModel: RopaViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Verificar sesión al iniciar
    LaunchedEffect(Unit) {
        if (sessionManager.isLoggedIn()) {
            navController.navigate("inicio") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("registro") {
            val viewModel: UsuarioFormViewModel = viewModel()
            UsuarioFormScreen(
                navController = navController
            )
        }

        composable("inicio") {
            // Proteger esta pantalla - verificar sesión
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            }

            Inicio(
                navController = navController,
                ropaViewModel = ropaViewModel
            )
        }

        composable("recomendaciones") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("recomendaciones") { inclusive = true }
                    }
                }
            }

            Recomendaciones(navController)
        }

        composable("agregar") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("agregar") { inclusive = true }
                    }
                }
            }

            AgregarRopa(
                navController = navController,
                ropaViewModel = ropaViewModel
            )
        }

        composable("camara") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("camara") { inclusive = true }
                    }
                }
            }

            CamaraFotos()
        }

        composable("miRopa") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("miRopa") { inclusive = true }
                    }
                }
            }

            MisPrendas(
                ropaViewModel = ropaViewModel,
                navController = navController
            )
        }

        composable("outfits") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("outfits") { inclusive = true }
                    }
                }
            }

            OutfitsScreen(
                navController = navController,
                ropaViewModel = ropaViewModel
            )
        }

        composable("crearOutfit") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("crearOutfit") { inclusive = true }
                    }
                }
            }


            CrearOutfitScreen(
                navController = navController,
                ropaViewModel = ropaViewModel
            )
        }

        composable("outfitSugerido") {
            // Proteger esta pantalla
            LaunchedEffect(Unit) {
                if (!sessionManager.isLoggedIn()) {
                    navController.navigate("login") {
                        popUpTo("outfitSugerido") { inclusive = true }
                    }
                }
            }

            OutfitSugeridoScreen(
                navController = navController,
                ropaViewModel = ropaViewModel
            )
        }
    }
}