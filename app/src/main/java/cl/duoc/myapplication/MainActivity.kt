package cl.duoc.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavController
import cl.duoc.myapplication.ui.theme.CamaraFotos
import cl.duoc.myapplication.ui.theme.Inicio
import cl.duoc.myapplication.ui.theme.Navegacion
import cl.duoc.myapplication.ui.theme.TiendaRopaTheme
import cl.duoc.myapplication.ui.theme.UsuarioFormScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiendaRopaTheme {
             Navegacion()
                //Inicio()
               // CamaraFotos()
//UsuarioFormScreen(navController = NavController(this) )
            }
        }
    }
}
