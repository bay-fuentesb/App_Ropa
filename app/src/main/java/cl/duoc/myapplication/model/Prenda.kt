package cl.duoc.myapplication.model

import com.google.gson.annotations.SerializedName


data class Prenda(
    val id: Long? = null,
    val titulo: String,
    val categoria: String,
    val color: String,

    // SerializedName asegura que coincida con el campo "imagenUri" de tu Java,
    // aunque tengan el mismo nombre, es una buena práctica de seguridad.
    @SerializedName("imagenUri")
    val imagenUri: String
)

