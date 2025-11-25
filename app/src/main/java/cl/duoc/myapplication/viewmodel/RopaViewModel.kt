package cl.duoc.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.repository.OutfitRepository
import cl.duoc.myapplication.repository.RopaRepository
import cl.duoc.myapplication.ui.utils.OutfitStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ⚠️ CAMBIO 1: Heredamos de AndroidViewModel para tener acceso al Contexto (necesario para guardar Outfits)
class RopaViewModel(application: Application) : AndroidViewModel(application) {

    // --- REPOSITORIOS Y ALMACENAMIENTO ---
    private val ropaRepository = RopaRepository()
    private val outfitRepository = OutfitRepository() // ⚠️ CAMBIO 2: Instanciamos aquí, no en la UI
    private val outfitStorage = OutfitStorage(application.applicationContext) // Para guardar localmente

    // --- ESTADOS (STATES) ---

    // 1. Lista Maestra (Todas las prendas de la API)
    private val _prendas = mutableStateListOf<Prenda>()
    val prendas: List<Prenda> = _prendas

    // 2. Lista Filtrada (La que verá la UI en "Mis Prendas")
    // ⚠️ CAMBIO 3: La UI observará esto en vez de filtrar ella misma
    private val _prendasFiltradas = mutableStateListOf<Prenda>()
    val prendasFiltradas: List<Prenda> = _prendasFiltradas

    // 3. Lista de Outfits (Cargada desde memoria local)
    private val _outfits = mutableStateListOf<OutfitSugerido>().apply {
        addAll(outfitStorage.cargarOutfits()) // Carga inicial automática
    }
    val outfits: List<OutfitSugerido> = _outfits

    // 4. Estado para el Outfit Sugerido (Pantalla de generación)
    private val _outfitSugerido = mutableStateOf<OutfitSugerido?>(null)
    val outfitSugerido: OutfitSugerido? get() = _outfitSugerido.value

    // Estados de Carga y Error
    private val _isLoading = mutableStateListOf<Boolean>()
    val isLoading: Boolean get() = _isLoading.isNotEmpty() && _isLoading.first()

    private val _errorMessage = mutableStateListOf<String?>()
    val errorMessage: String? get() = _errorMessage.firstOrNull()


    // --- INICIALIZACIÓN ---
    init {
        cargarPrendasDesdeApi()
    }

    // ------------------------------------------------------
    // 🚀 LÓGICA DE PRENDAS (API AWS)
    // ------------------------------------------------------

    fun cargarPrendasDesdeApi() {
        viewModelScope.launch(Dispatchers.IO) {
            setLoading(true)
            try {
                val listaRemota = ropaRepository.obtenerPrendaEnApi()
                withContext(Dispatchers.Main) {
                    if (listaRemota != null) {
                        _prendas.clear()
                        _prendas.addAll(listaRemota)

                        // ⚠️ CAMBIO 4: Al cargar, reseteamos el filtro para mostrar todo
                        filtrarPrendas(null, null)

                        println("✅ Prendas cargadas: ${listaRemota.size}")
                    } else {
                        _errorMessage.add("Error al conectar con el servidor")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { _errorMessage.add("Error de red: ${e.message}") }
            } finally {
                setLoading(false)
            }
        }
    }

    fun agregarPrenda(prenda: Prenda) {
        viewModelScope.launch(Dispatchers.IO) {
            setLoading(true)
            val prendaCreada = ropaRepository.crearPrendaEnApi(prenda)
            withContext(Dispatchers.Main) {
                if (prendaCreada != null) {
                    _prendas.add(prendaCreada)
                    // Actualizamos también la lista filtrada
                    _prendasFiltradas.add(prendaCreada)
                } else {
                    _errorMessage.add("No se pudo guardar en la nube.")
                }
                setLoading(false)
            }
        }
    }

    fun eliminarPrenda(prenda: Prenda) {
        if (prenda.id == null) {
            _prendas.remove(prenda)
            _prendasFiltradas.remove(prenda)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            setLoading(true)
            val exito = ropaRepository.eliminarPrendaEnApi(prenda.id)
            withContext(Dispatchers.Main) {
                if (exito) {
                    _prendas.remove(prenda)
                    _prendasFiltradas.remove(prenda)
                    println("🗑️ Eliminado correctamente")
                } else {
                    _errorMessage.add("Error al eliminar del servidor")
                }
                setLoading(false)
            }
        }
    }

    // ------------------------------------------------------
    // 🔍 LÓGICA DE FILTRADO (MVVM PURO)
    // ------------------------------------------------------

    // ⚠️ CAMBIO 5: Esta función actualiza el state, no retorna nada.
    fun filtrarPrendas(categoria: String?, color: String?) {
        val resultado = if (categoria == null && color == null) {
            _prendas // Si no hay filtros, mostramos todo
        } else {
            _prendas.filter { prenda ->
                (categoria == null || prenda.categoria.equals(categoria, ignoreCase = true)) &&
                        (color == null || prenda.color.equals(color, ignoreCase = true))
            }
        }

        _prendasFiltradas.clear()
        _prendasFiltradas.addAll(resultado)
    }

    // ------------------------------------------------------
    // 👗 LÓGICA DE OUTFITS (LOCAL + PERSISTENCIA)
    // ------------------------------------------------------

    fun agregarOutfit(outfit: OutfitSugerido) {
        _outfits.add(0, outfit)
        outfitStorage.guardarOutfits(_outfits) // Guardamos en celular
    }

    fun eliminarOutfit(outfit: OutfitSugerido) {
        _outfits.remove(outfit)
        outfitStorage.guardarOutfits(_outfits) // Actualizamos celular
    }

    // ⚠️ CAMBIO 6: Lógica de generación movida al ViewModel
    fun generarOutfitSugerido(): String? {
        if (_prendas.isEmpty()) return "No hay prendas para generar outfits."

        // Intentamos generar aleatorio
        val nuevoOutfit = outfitRepository.generarOutfitAleatorio(_prendas)
        if (nuevoOutfit != null) {
            _outfitSugerido.value = nuevoOutfit
            agregarOutfit(nuevoOutfit) // Lo guardamos automáticamente en el historial
            return null // Null significa "sin error"
        }

        // Fallback: Sugerencias predefinidas
        val sugerencias = outfitRepository.generarSugerenciasOutfits(_prendas)
        if (sugerencias.isNotEmpty()) {
            val fallback = sugerencias.first()
            _outfitSugerido.value = fallback
            agregarOutfit(fallback)
            return null
        }

        return "No se pudo generar un outfit. Intenta agregar más variedad de ropa."
    }

    fun limpiarOutfitSugerido() {
        _outfitSugerido.value = null
    }

    // ------------------------------------------------------
    // ⚙️ AUXILIARES
    // ------------------------------------------------------

    private fun setLoading(loading: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.clear()
            if (loading) _isLoading.add(true)
        }
    }

    fun limpiarError() {
        _errorMessage.clear()
    }

    // Carga de datos de prueba si el usuario no tiene nada
    fun cargarPrendasDeEjemplo() {
        val ejemplos = listOf(
            Prenda(titulo = "Polera Básica", categoria = "Polera", color = "#000000", imagenUri = ""),
            Prenda(titulo = "Jeans", categoria = "Pantalones", color = "#0000FF", imagenUri = "")
        )
        _prendas.addAll(ejemplos)
        _prendasFiltradas.addAll(ejemplos)
    }
}