package cl.duoc.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import cl.duoc.myapplication.model.Prenda

class RopaViewModel : ViewModel() {

    private val _prendas = mutableStateListOf<Prenda>()
    val prendas: List<Prenda> = _prendas

    fun agregarPrenda(prenda: Prenda) {
        _prendas.add(prenda)
    }
}