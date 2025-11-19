package cl.duoc.myapplication.model

data class OutfitSugerido(
    val id: Long = System.currentTimeMillis(),
    val nombre: String,
    val combinacion: List<Prenda>,

)