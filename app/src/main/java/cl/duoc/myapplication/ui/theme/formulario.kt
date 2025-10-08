package cl.duoc.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.myapplication.R


@Composable
fun formulario(){

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var modal by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
){
    Text(text = "Nombre")
        //input 1
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Ingrese su nombre") },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Text)
        )

        Text(text = "Correo")
        //input 1
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Ingrese su correo") },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Text)
        )
        Text(text = "edad")
        //input 1
        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Ingrese su edad") },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Text)
        )

        if(modal){
            AlertDialog(
                onDismissRequest = { modal = false },
                title = { Text("Mensaje") },
                text = { Text("Se debe crear el usuario: $nombre con la contraseña:$correo") },
                confirmButton = {
                    Button(onClick = { modal = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}