package cl.duoc.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.model.OutfitSugerido

class RopaViewModel : ViewModel() {

    private val _prendas = mutableStateListOf<Prenda>()
    val prendas: List<Prenda> = _prendas

    private val _outfits = mutableStateListOf<OutfitSugerido>()
    val outfits: List<OutfitSugerido> = _outfits

    fun agregarPrenda(prenda: Prenda) {
        _prendas.add(prenda)
    }

    fun agregarOutfit(outfit: OutfitSugerido) {
        _outfits.add(0, outfit) // agregar al inicio
    }
}
