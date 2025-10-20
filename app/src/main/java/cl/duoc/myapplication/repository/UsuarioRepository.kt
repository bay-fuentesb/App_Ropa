package cl.duoc.myapplication.repository

import cl.duoc.myapplication.model.FormularioModel
import cl.duoc.myapplication.model.MensajesError

class UsuarioRepository {
    private var formulario = FormularioModel()
    private var errores = MensajesError()

    fun getFormulario(): FormularioModel = formulario
    fun getMensajesError(): MensajesError = errores

    fun cambiarNombre(nuevoNombre: String) {
        formulario.nombre = nuevoNombre
    }

    fun validacionNombre(): Boolean {
        return formulario.nombre.isNotEmpty()
    }

    fun validacionCorreo(): Boolean {
        return Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$").matches(formulario.correo)
    }

    fun validacionEdad(): Boolean {
        val edadInt = formulario.edad.toIntOrNull()
        return !(edadInt == null || edadInt < 0 || edadInt > 120)
    }

    fun validacionTerminos(): Boolean {
        return formulario.terminos
    }
}

