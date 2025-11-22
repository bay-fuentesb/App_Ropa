package cl.duoc.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.repository.RopaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RopaViewModel : ViewModel() {

    // Instancia del repositorio para conectar con AWS
    private val repository = RopaRepository()

    private val _prendas = mutableStateListOf<Prenda>()
    val prendas: List<Prenda> = _prendas

    private val _outfits = mutableStateListOf<OutfitSugerido>()
    val outfits: List<OutfitSugerido> = _outfits

    // Estado de carga (Mantenemos tu lógica de lista)
    private val _isLoading = mutableStateListOf<Boolean>()
    val isLoading: Boolean
        get() = _isLoading.isNotEmpty() && _isLoading.first()

    // Mensajes de error
    private val _errorMessage = mutableStateListOf<String?>()
    val errorMessage: String?
        get() = _errorMessage.firstOrNull()

    // ------------------------------------------------------
    // 🚀 BLOQUE DE INICIALIZACIÓN (Carga desde la Nube)
    // ------------------------------------------------------
    init {
        cargarPrendasDesdeApi()
    }

    fun cargarPrendasDesdeApi() {
        viewModelScope.launch(Dispatchers.IO) {
            setLoading(true)
            try {
                // 1. Pedimos datos a AWS
                val listaRemota = repository.obtenerPrendaEnApi()

                withContext(Dispatchers.Main) {
                    if (listaRemota != null) {
                        _prendas.clear()
                        _prendas.addAll(listaRemota)
                        println("✅ Prendas cargadas desde AWS: ${listaRemota.size}")
                    } else {
                        _errorMessage.add("Error al conectar con el servidor")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.add("Error de red: ${e.message}")
                }
            } finally {
                setLoading(false)
            }
        }
    }

    // ------------------------------------------------------
    // ☁️ MÉTODOS CONECTADOS A LA API
    // ------------------------------------------------------

    fun agregarPrenda(prenda: Prenda) {
        viewModelScope.launch(Dispatchers.IO) {
            setLoading(true)
            // 1. Enviamos a AWS
            val prendaCreada = repository.crearPrendaEnApi(prenda)

            withContext(Dispatchers.Main) {
                if (prendaCreada != null) {
                    // 2. Si AWS responde OK, agregamos a la lista local la prenda QUE VOLVIÓ DEL SERVIDOR
                    // (Esto es importante porque la que vuelve tiene el ID real de MySQL)
                    _prendas.add(prendaCreada)
                    println("✅ Prenda guardada en Nube: ${prendaCreada.titulo}. ID: ${prendaCreada.id}")
                } else {
                    // Fallback: Si falla internet, podrías guardarla localmente o avisar
                    _errorMessage.add("No se pudo guardar en la nube. Revisa tu conexión.")
                    // Opcional: _prendas.add(prenda) // Solo si quieres persistencia local temporal
                }
                setLoading(false)
            }
        }
    }

    fun eliminarPrenda(prenda: Prenda) {
        if (prenda.id == null) {
            // Si no tiene ID, solo la borramos de memoria local
            _prendas.remove(prenda)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            setLoading(true)
            // 1. Borramos en AWS
            val exito = repository.eliminarPrendaEnApi(prenda.id) // Asegúrate de implementar esto en el Repo

            withContext(Dispatchers.Main) {
                if (exito) {
                    _prendas.remove(prenda)
                    println("🗑️ Prenda eliminada de Nube y Local: ${prenda.titulo}")
                } else {
                    _errorMessage.add("Error al eliminar la prenda del servidor")
                }
                setLoading(false)
            }
        }
    }

    // ------------------------------------------------------
    // ⚙️ MÉTODOS AUXILIARES (Lógica Local)
    // ------------------------------------------------------
    // Estos se mantienen casi igual porque trabajan sobre la lista _prendas ya cargada

    private fun setLoading(loading: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.clear()
            if (loading) _isLoading.add(true)
        }
    }

    fun eliminarPrendaPorId(id: Long) {
        val prenda = _prendas.find { it.id == id }
        if (prenda != null) {
            eliminarPrenda(prenda)
        }
    }

    // Nota: Para actualizar, necesitarías un endpoint PUT en tu API Java.
    // Por ahora lo dejamos local, pero recuerda que al reiniciar la app volverá el dato viejo.
    fun actualizarPrenda(prendaActualizada: Prenda) {
        val index = _prendas.indexOfFirst { it.id == prendaActualizada.id }
        if (index != -1) {
            _prendas[index] = prendaActualizada
            println("Prenda actualizada localmente: ${prendaActualizada.titulo}")
        }
    }

    // --- LÓGICA DE OUTFITS (Local por ahora, a menos que crees API de Outfits) ---

    fun agregarOutfit(outfit: OutfitSugerido) {
        _outfits.add(0, outfit)
        println("Outfit agregado: ${outfit.nombre}. Total: ${_outfits.size}")
    }

    fun eliminarOutfit(outfit: OutfitSugerido) {
        _outfits.remove(outfit)
    }

    fun eliminarOutfitPorId(id: Long) {
        _outfits.removeAll { it.id == id }
    }

    fun obtenerPrendaPorId(id: Long): Prenda? {
        return _prendas.find { it.id == id }
    }

    fun obtenerOutfitPorId(id: Long): OutfitSugerido? {
        return _outfits.find { it.id == id }
    }

    fun limpiarError() {
        _errorMessage.clear()
    }

    // Cargar ejemplos (Solo local, útil para testing)
    fun cargarPrendasDeEjemplo() {
        val prendasEjemplo = listOf(
            Prenda(titulo = "Polera Negra", categoria = "Polera", color = "Negro", imagenUri = ""),
            Prenda(titulo = "Jeans Azul", categoria = "Pantalones", color = "Azul", imagenUri = "")
        )
        // Opcional: Podrías recorrer esta lista y llamar a agregarPrenda(p) para subirlas todas a AWS
        _prendas.addAll(prendasEjemplo)
    }

    fun limpiarPrendas() {
        _prendas.clear()
        // Ojo: Esto solo limpia la pantalla, no borra la base de datos de AWS masivamente
    }

    fun limpiarOutfits() {
        _outfits.clear()
    }

    fun obtenerPrendasPorCategoria(categoria: String): List<Prenda> {
        return _prendas.filter { it.categoria.equals(categoria, ignoreCase = true) }
    }

    fun obtenerPrendasPorColor(color: String): List<Prenda> {
        return _prendas.filter { it.color.equals(color, ignoreCase = true) }
    }

    fun puedeGenerarOutfit(): Boolean {
        return _prendas.size >= 2
    }

    fun obtenerEstadisticas(): Map<String, Any> {
        val totalPrendas = _prendas.size
        val totalOutfits = _outfits.size
        val categorias = _prendas.map { it.categoria }.distinct()
        val colores = _prendas.map { it.color }.distinct()

        return mapOf(
            "totalPrendas" to totalPrendas,
            "totalOutfits" to totalOutfits,
            "categorias" to categorias,
            "colores" to colores
        )
    }

    fun filtrarPrendas(categoria: String? = null, color: String? = null, query: String? = null): List<Prenda> {
        return _prendas.filter { prenda ->
            (categoria == null || prenda.categoria.equals(categoria, ignoreCase = true)) &&
                    (color == null || prenda.color.equals(color, ignoreCase = true)) &&
                    (query == null || prenda.titulo.contains(query, ignoreCase = true) || prenda.categoria.contains(query, ignoreCase = true))
        }
    }

    fun existePrenda(titulo: String, categoria: String): Boolean {
        return _prendas.any {
            it.titulo.equals(titulo, ignoreCase = true) && it.categoria.equals(categoria, ignoreCase = true)
        }
    }
}