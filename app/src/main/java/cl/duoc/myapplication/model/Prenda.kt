package cl.duoc.myapplication.model


data class Prenda(
    val id: Long = System.currentTimeMillis(),
    val titulo: String,
    val categoria: String,
    val color: String,
    val imagenUri: String
)

