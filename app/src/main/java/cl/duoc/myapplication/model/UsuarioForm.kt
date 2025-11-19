package cl.duoc.myapplication.model

data class UsuarioForm(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val edad: Int? = null,
    val aceptaTerminos: Boolean = false,
    val quiereNotificaciones: Boolean = false
)

