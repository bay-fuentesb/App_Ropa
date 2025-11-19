package cl.duoc.myapplication.model

data class UsuarioFormError(
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val edad: String? = null,
    val terminos: String? = null
)

