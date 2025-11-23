package cl.duoc.myapplication.repository

import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.model.Prenda

class OutfitRepository {

    fun generarOutfitAleatorio(prendasUsuario: List<Prenda>): OutfitSugerido? {
        if (prendasUsuario.isEmpty()) {
            println("No hay prendas disponibles")
            return null
        }

        println("Generando outfit con ${prendasUsuario.size} prendas")
        val prendasPorCategoria = prendasUsuario.groupBy { it.categoria }
        val seleccion = mutableListOf<Prenda>()

        // Listas de palabras clave
        val categoriasSuperiores = listOf("Polera", "Poleron", "Chaqueta", "Parka", "Camisa", "Remera", "Blusa", "Sweater")
        val categoriasInferiores = listOf("Pantalones", "Pantalón", "Jeans", "Short", "Falda", "Vestido")
        val categoriasCalzados = listOf("Zapatilla", "Zapatillas", "Zapato", "Zapatos", "Calzado", "Tenis")

        // Filtrado
        val superiores = categoriasSuperiores.flatMap { prendasPorCategoria[it] ?: emptyList() }
        val inferiores = categoriasInferiores.flatMap { prendasPorCategoria[it] ?: emptyList() }
        val calzados = categoriasCalzados.flatMap { prendasPorCategoria[it] ?: emptyList() }

        // Selección Aleatoria (Intento de balanceo)
        if (superiores.isNotEmpty()) superiores.shuffled().firstOrNull()?.let { seleccion.add(it) }
        if (inferiores.isNotEmpty()) inferiores.shuffled().firstOrNull()?.let { seleccion.add(it) }
        if (calzados.isNotEmpty()) calzados.shuffled().firstOrNull()?.let { seleccion.add(it) }

        // Fallback: Rellenar si falta ropa para llegar a 2 prendas mínimo
        if (seleccion.size < 2) {
            val otrasPrendas = prendasUsuario.filter { it !in seleccion }.shuffled()
            val necesitamos = 2 - seleccion.size
            seleccion.addAll(otrasPrendas.take(necesitamos))
        }

        return if (seleccion.isNotEmpty()) {
            OutfitSugerido(
                nombre = "Outfit Aleatorio",
                combinacion = seleccion
            )
        } else {
            null
        }
    }

    /**
     * Genera sugerencias.
     * @param cantidad (Opcional) Número máximo de sugerencias. Por defecto es 3.
     */
    fun generarSugerenciasOutfits(prendasUsuario: List<Prenda>, cantidad: Int = 3): List<OutfitSugerido> {
        if (prendasUsuario.size < 2) {
            println("No hay suficientes prendas para generar sugerencias")
            return emptyList()
        }

        val sugerencias = mutableListOf<OutfitSugerido>()
        val prendasPorCategoria = prendasUsuario.groupBy { it.categoria }

        val superiores = listOf("Polera", "Poleron", "Chaqueta", "Parka", "Camisa", "Remera", "Sweater")
            .flatMap { prendasPorCategoria[it] ?: emptyList() }
        val inferiores = listOf("Pantalones", "Pantalón", "Jeans", "Short", "Falda")
            .flatMap { prendasPorCategoria[it] ?: emptyList() }
        val calzados = listOf("Zapatilla", "Zapatillas", "Zapato", "Tenis")
            .flatMap { prendasPorCategoria[it] ?: emptyList() }

        // Usamos el parámetro 'cantidad' aquí
        val maxSuggestions = cantidad

        // 1. Intentar Outfits completos (Top + Bottom + Shoes)
        if (superiores.isNotEmpty() && inferiores.isNotEmpty() && calzados.isNotEmpty()) {
            repeat(minOf(maxSuggestions, superiores.size, inferiores.size, calzados.size)) { index ->
                val top = superiores.shuffled().first()
                val bottom = inferiores.shuffled().first()
                val shoes = calzados.shuffled().first()

                // Evitar duplicados exactos si es posible (simple check)
                val nuevaComb = listOf(top, bottom, shoes)
                // Solo agregamos si no existe una sugerencia idéntica (opcional, pero recomendado)
                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Outfit Completo ${sugerencias.size + 1}",
                        combinacion = nuevaComb
                    )
                )
            }
        }

        // 2. Si faltan sugerencias, intentar Top + Bottom
        if (sugerencias.size < maxSuggestions && superiores.isNotEmpty() && inferiores.isNotEmpty()) {
            val combinacionesNecesarias = maxSuggestions - sugerencias.size
            repeat(minOf(combinacionesNecesarias, superiores.size, inferiores.size)) {
                val top = superiores.shuffled().first()
                val bottom = inferiores.shuffled().first()

                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Combinación Casual ${sugerencias.size + 1}",
                        combinacion = listOf(top, bottom)
                    )
                )
            }
        }

        // 3. Fallback: Combinaciones aleatorias simples
        if (sugerencias.isEmpty()) {
            val shuffled = prendasUsuario.shuffled()
            val cantidadFallback = minOf(maxSuggestions, prendasUsuario.size / 2)

            repeat(cantidadFallback) { index ->
                val pair = shuffled.chunked(2).getOrNull(index)
                pair?.let {
                    sugerencias.add(
                        OutfitSugerido(
                            nombre = "Combinación ${index + 1}",
                            combinacion = it
                        )
                    )
                }
            }
        }

        // Aseguramos no devolver más de lo pedido
        return sugerencias.take(maxSuggestions)
    }
}