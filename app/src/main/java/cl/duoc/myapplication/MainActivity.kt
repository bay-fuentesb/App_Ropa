package cl.duoc.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.myapplication.ui.theme.Navegacion
import cl.duoc.myapplication.ui.theme.TiendaRopaTheme
import cl.duoc.myapplication.ui.theme.pagina1
import cl.duoc.myapplication.ui.theme.pagina2

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


