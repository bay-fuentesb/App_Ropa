package cl.duoc.myapplication.model

data class OutfitSugerido(
    val id: Int = 0,
    val nombre: String,
    val combinacion: List<Prenda>,
    val puntuacion: Int,
    val motivo: String
)