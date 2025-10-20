package cl.duoc.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import cl.duoc.myapplication.model.UsuarioForm
import cl.duoc.myapplication.model.UsuarioFormError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsuarioFormViewModel : ViewModel() {

    val TAG = "MyActivity"

    private val _form = MutableStateFlow(UsuarioForm())
    val form: StateFlow<UsuarioForm> = _form

    private val _errors = MutableStateFlow(
        UsuarioFormError(
            "El nombre es obligatorio",
            "El correo es obligatorio",
            "La edad es obligatoria",
            "Debes aceptar los términos"
        )
    )
    val errors: StateFlow<UsuarioFormError> = _errors

    fun onNombreChange(value: String) {
        _form.value = _form.value.copy(nombre = value)
        validate()
    }

    fun onCorreoChange(value: String) {
        _form.value = _form.value.copy(correo = value)
        validate()
    }

    fun onEdadChange(value: Int?) {
        _form.value = _form.value.copy(edad = value)
        validate()
    }

    fun onAceptaTerminosChange(value: Boolean) {
        _form.value = _form.value.copy(aceptaTerminos = value)
        validate()
    }

    fun onQuiereNotificacionesChange(value: Boolean) {
        _form.value = _form.value.copy(quiereNotificaciones = value)
    }

    fun isFormValid(errors: UsuarioFormError): Boolean {
        Log.d(TAG, "in isFormValid")
        Log.d(TAG, "errors.nombre: ${errors.nombre}")
        Log.d(TAG, "errors.correo: ${errors.correo}")
        Log.d(TAG, "errors.edad: ${errors.edad}")
        Log.d(TAG, "errors.terminos: ${errors.terminos}")
        return errors.nombre == null && errors.correo == null && errors.edad == null && errors.terminos == null
    }

    private fun validate() {
        val f = _form.value
        _errors.value = UsuarioFormError(
            if (f.nombre.isBlank()) "El nombre es obligatorio" else null,
            if (!f.correo.matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))) "Correo inválido" else null,
            if (f.edad == null || f.edad !in 18..99) "Edad entre 18 y 99" else null,
            if (!f.aceptaTerminos) "Debes aceptar los términos" else null
        )
    }
}