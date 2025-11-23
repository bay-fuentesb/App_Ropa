package cl.duoc.myapplication

import cl.duoc.myapplication.model.Prenda
import cl.duoc.myapplication.repository.OutfitRepository
import org.junit.Assert.*
import org.junit.Test

class OutfitRepositoryTest {

    private val repository = OutfitRepository()

    // --- TEST 1: Verificar NULL cuando está vacío ---
    @Test
    fun `generarOutfitAleatorio retorna NULL si la lista esta vacia`() {
        val listaVacia = emptyList<Prenda>()
        val resultado = repository.generarOutfitAleatorio(listaVacia)
        assertNull("Si no hay ropa, debe retornar null", resultado)
    }

    // --- TEST 2: Verificar Outfit Completo ---
    @Test
    fun `generarOutfitAleatorio crea outfit completo si tiene las 3 partes`() {
        val roperoCompleto = listOf(
            crearPrenda("Polera", "Superior"),
            crearPrenda("Jeans", "Inferior"),
            crearPrenda("Zapatillas", "Calzado")
        )

        val resultado = repository.generarOutfitAleatorio(roperoCompleto)

        assertNotNull(resultado)
        assertEquals("El outfit debería tener 3 piezas", 3, resultado!!.combinacion.size)
    }

    // --- TEST 3: Verificar Fallback (Relleno) ---
    @Test
    fun `generarOutfitAleatorio rellena si faltan categorias`() {
        // Solo 2 poleras, sin pantalones
        val soloTops = listOf(
            crearPrenda("Polera", "Superior"),
            crearPrenda("Poleron", "Superior")
        )

        val resultado = repository.generarOutfitAleatorio(soloTops)

        assertNotNull(resultado)
        assertTrue("Debería tener al menos 2 prendas aunque sean de la misma categoria", resultado!!.combinacion.size >= 2)
    }

    // --- TEST 4: Sugerencias por defecto (3) ---
    @Test
    fun `generarSugerenciasOutfits usa 3 como maximo por defecto`() {
        val muchaRopa = generarMuchaRopa() // Helper abajo

        // Llamamos SIN parámetros (debe usar el default 3)
        val resultado = repository.generarSugerenciasOutfits(muchaRopa)

        assertEquals("Por defecto debe devolver 3", 3, resultado.size)
    }

    // --- TEST 5: Sugerencias Personalizadas (NUEVO) ---
    @Test
    fun `generarSugerenciasOutfits respeta la cantidad solicitada como parametro`() {
        val muchaRopa = generarMuchaRopa()
        val cantidadDeseada = 5

        // Llamamos CON parámetro
        val resultado = repository.generarSugerenciasOutfits(muchaRopa, cantidadDeseada)

        assertEquals("Debe devolver 5 sugerencias si se solicitan", 5, resultado.size)
    }

    // --- HELPERS (Funciones de ayuda para los tests) ---

    private fun crearPrenda(categoria: String, tipoSimulado: String): Prenda {
        return Prenda(
            id = System.currentTimeMillis(),
            titulo = "Item Test",
            categoria = categoria, // Esto debe coincidir con los strings de tu repo (Polera, Jeans, etc)
            color = "#000000",
            imagenUri = ""
        )
    }

    private fun generarMuchaRopa(): List<Prenda> {
        val lista = mutableListOf<Prenda>()
        repeat(10) {
            lista.add(crearPrenda("Polera", "Superior"))
            lista.add(crearPrenda("Jeans", "Inferior"))
            lista.add(crearPrenda("Zapatillas", "Calzado"))
        }
        return lista
    }
}