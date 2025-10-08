package cl.duoc.myapplication.model

data class UsuarioForm(
    var nombre: String = "",
    var correo: String = "",
    var edad: Int? = null
)
data class UsuarioFormError(
    val nombre: String? = null,
    val correo: String? = null,
    val edad: String? = null,

)