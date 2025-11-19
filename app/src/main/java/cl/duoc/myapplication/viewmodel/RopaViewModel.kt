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

    // Estado de carga
    private val _isLoading = mutableStateListOf<Boolean>()
    val isLoading: Boolean
        get() = _isLoading.isNotEmpty() && _isLoading.first()

    // Mensajes de error
    private val _errorMessage = mutableStateListOf<String?>()
    val errorMessage: String?
        get() = _errorMessage.firstOrNull()

    // Mantener tus métodos originales
    fun agregarPrenda(prenda: Prenda) {
        _prendas.add(prenda)
        println("Prenda agregada: ${prenda.titulo}. Total: ${_prendas.size}")
    }

    fun agregarOutfit(outfit: OutfitSugerido) {
        _outfits.add(0, outfit) // agregar al inicio
        println("Outfit agregado: ${outfit.nombre}. Total: ${_outfits.size}")
    }

    // Nuevos métodos adicionales

    fun eliminarPrenda(prenda: Prenda) {
        _prendas.remove(prenda)
        println("Prenda eliminada: ${prenda.titulo}. Total: ${_prendas.size}")
    }

    fun eliminarPrendaPorId(id: Long) {
        _prendas.removeAll { it.id == id }
        println("Prenda eliminada por ID: $id. Total: ${_prendas.size}")
    }

    fun actualizarPrenda(prendaActualizada: Prenda) {
        val index = _prendas.indexOfFirst { it.id == prendaActualizada.id }
        if (index != -1) {
            _prendas[index] = prendaActualizada
            println("Prenda actualizada: ${prendaActualizada.titulo}")
        }
    }

    fun eliminarOutfit(outfit: OutfitSugerido) {
        _outfits.remove(outfit)
        println("Outfit eliminado: ${outfit.nombre}. Total: ${_outfits.size}")
    }

    fun eliminarOutfitPorId(id: Long) {
        _outfits.removeAll { it.id == id }
        println("Outfit eliminado por ID: $id. Total: ${_outfits.size}")
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

    // Método para cargar prendas de ejemplo (compatible con tu estructura)
    fun cargarPrendasDeEjemplo() {
        _isLoading.clear()
        _isLoading.add(true)

        try {
            val prendasEjemplo = listOf(
                Prenda(
                    titulo = "Polera Negra Básica",
                    categoria = "Polera",
                    color = "Negro",
                    imagenUri = ""
                ),
                Prenda(
                    titulo = "Jeans Azul Clásico",
                    categoria = "Pantalones",
                    color = "Azul",
                    imagenUri = ""
                ),
                Prenda(
                    titulo = "Zapatillas Blancas Deportivas",
                    categoria = "Zapatilla",
                    color = "Blanco",
                    imagenUri = ""
                ),
                Prenda(
                    titulo = "Chaqueta Denim",
                    categoria = "Chaqueta",
                    color = "Azul",
                    imagenUri = ""
                ),
                Prenda(
                    titulo = "Polerón Gris con Capucha",
                    categoria = "Poleron",
                    color = "Gris",
                    imagenUri = ""
                ),
                Prenda(
                    titulo = "Short Deportivo Negro",
                    categoria = "Short",
                    color = "Negro",
                    imagenUri = ""
                )
            )

            _prendas.clear()
            _prendas.addAll(prendasEjemplo)
            println("Prendas de ejemplo cargadas: ${prendasEjemplo.size} prendas")

        } catch (e: Exception) {
            _errorMessage.clear()
            _errorMessage.add("Error al cargar prendas: ${e.message}")
        } finally {
            _isLoading.clear()
        }
    }

    fun limpiarPrendas() {
        _prendas.clear()
        println("Todas las prendas han sido eliminadas")
    }

    fun limpiarOutfits() {
        _outfits.clear()
        println("Todos los outfits han sido eliminados")
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

    // Filtrado de prendas
    fun filtrarPrendas(
        categoria: String? = null,
        color: String? = null,
        query: String? = null
    ): List<Prenda> {
        return _prendas.filter { prenda ->
            (categoria == null || prenda.categoria.equals(categoria, ignoreCase = true)) &&
                    (color == null || prenda.color.equals(color, ignoreCase = true)) &&
                    (query == null || prenda.titulo.contains(query, ignoreCase = true) ||
                            prenda.categoria.contains(query, ignoreCase = true))
        }
    }

    // Método para verificar si una prenda ya existe
    fun existePrenda(titulo: String, categoria: String): Boolean {
        return _prendas.any {
            it.titulo.equals(titulo, ignoreCase = true) &&
                    it.categoria.equals(categoria, ignoreCase = true)
        }
    }
}