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
            nombre = "El nombre es obligatorio",
            apellido = "El apellido es obligatorio",
            email = "Correo inválido",
            edad = "Edad +18",
            terminos = "Debes aceptar los términos"
        )
    )
    val errors: StateFlow<UsuarioFormError> = _errors

    fun onNombreChange(value: String) {
        _form.value = _form.value.copy(nombre = value)
        validate()
    }

    fun onApellidoChange(value:String){
        _form.value = _form.value.copy(apellido = value)
        validate()
    }

    fun onEmailChange(value: String) {
        _form.value = _form.value.copy(email = value)
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
        Log.d(TAG, "errors.apellido: ${errors.apellido}")
        Log.d(TAG, "errors.email: ${errors.email}")
        Log.d(TAG, "errors.edad: ${errors.edad}")
        Log.d(TAG, "errors.terminos: ${errors.terminos}")
        return errors.nombre == null &&
                errors.apellido == null &&
                errors.email == null &&
                errors.edad == null &&
                errors.terminos == null
    }

    private fun validate() {
        val f = _form.value
        _errors.value = UsuarioFormError(
            if (f.nombre.isBlank()) "El nombre es obligatorio" else null,
            if (f.apellido.isBlank()) "El apellido es obligatorio" else null,
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(f.email).matches()) "Correo inválido" else null,
            if (f.edad == null || f.edad < 18) "Edad +18" else null,
            if (!f.aceptaTerminos) "Debes aceptar los términos" else null
        )
    }
}