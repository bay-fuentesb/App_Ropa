package cl.duoc.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.myapplication.ui.theme.Navegacion
import cl.duoc.myapplication.ui.theme.TiendaRopaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiendaRopaTheme {
                Navegacion()
            }
        }
    }
}
