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
        println("Categorías disponibles: ${prendasUsuario.map { it.categoria }.distinct()}")

        val prendasPorCategoria = prendasUsuario.groupBy { it.categoria }
        val seleccion = mutableListOf<Prenda>()

        // Buscar superiores
        val categoriasSuperiores = listOf("Polera", "Poleron", "Chaqueta", "Parka", "Camisa", "Remera", "Blusa", "Sweater")
        val superiores = categoriasSuperiores.flatMap { prendasPorCategoria[it] ?: emptyList() }

        // Buscar inferiores
        val categoriasInferiores = listOf("Pantalones", "Pantalón", "Jeans", "Short", "Falda", "Vestido")
        val inferiores = categoriasInferiores.flatMap { prendasPorCategoria[it] ?: emptyList() }

        // Buscar calzados
        val categoriasCalzados = listOf("Zapatilla", "Zapatillas", "Zapato", "Zapatos", "Calzado", "Tenis")
        val calzados = categoriasCalzados.flatMap { prendasPorCategoria[it] ?: emptyList() }

        println("Superiores encontrados: ${superiores.size}")
        println("Inferiores encontrados: ${inferiores.size}")
        println("Calzados encontrados: ${calzados.size}")

        // Intentar crear un outfit balanceado
        if (superiores.isNotEmpty()) {
            superiores.shuffled().firstOrNull()?.let { seleccion.add(it) }
        }

        if (inferiores.isNotEmpty()) {
            inferiores.shuffled().firstOrNull()?.let { seleccion.add(it) }
        }

        if (calzados.isNotEmpty()) {
            calzados.shuffled().firstOrNull()?.let { seleccion.add(it) }
        }

        // Si no tenemos suficientes prendas de categorías específicas, completar con cualquier prenda
        if (seleccion.size < 2) {
            val otrasPrendas = prendasUsuario.filter { it !in seleccion }.shuffled()
            val necesitamos = 2 - seleccion.size
            seleccion.addAll(otrasPrendas.take(necesitamos))
        }

        println("Outfit generado con ${seleccion.size} prendas")

        return if (seleccion.isNotEmpty()) {
            OutfitSugerido(
                nombre = "Outfit Aleatorio",
                combinacion = seleccion
            )
        } else {
            null
        }
    }

    fun generarSugerenciasOutfits(prendasUsuario: List<Prenda>): List<OutfitSugerido> {
        if (prendasUsuario.size < 2) {
            println("No hay suficientes prendas para generar sugerencias")
            return emptyList()
        }

        val sugerencias = mutableListOf<OutfitSugerido>()
        val prendasPorCategoria = prendasUsuario.groupBy { it.categoria }

        // Categorías flexibles
        val superiores = listOf("Polera", "Poleron", "Chaqueta", "Parka", "Camisa", "Remera", "Sweater")
            .flatMap { prendasPorCategoria[it] ?: emptyList() }

        val inferiores = listOf("Pantalones", "Pantalón", "Jeans", "Short", "Falda")
            .flatMap { prendasPorCategoria[it] ?: emptyList() }

        val calzados = listOf("Zapatilla", "Zapatillas", "Zapato", "Tenis")
            .flatMap { prendasPorCategoria[it] ?: emptyList() }

        val maxSuggestions = 3

        // Outfit completo: superior + inferior + calzado
        if (superiores.isNotEmpty() && inferiores.isNotEmpty() && calzados.isNotEmpty()) {
            repeat(minOf(maxSuggestions, superiores.size, inferiores.size, calzados.size)) { index ->
                val top = superiores.shuffled().first()
                val bottom = inferiores.shuffled().first()
                val shoes = calzados.shuffled().first()

                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Outfit Completo ${index + 1}",
                        combinacion = listOf(top, bottom, shoes)
                    )
                )
            }
        }

        // Solo superior + inferior
        if (sugerencias.size < maxSuggestions && superiores.isNotEmpty() && inferiores.isNotEmpty()) {
            val combinacionesNecesarias = maxSuggestions - sugerencias.size
            repeat(minOf(combinacionesNecesarias, superiores.size, inferiores.size)) { index ->
                val top = superiores.shuffled().first()
                val bottom = inferiores.shuffled().first()

                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Combinación Casual ${index + 1}",
                        combinacion = listOf(top, bottom)
                    )
                )
            }
        }

        // Combinaciones aleatorias simples como fallback
        if (sugerencias.isEmpty()) {
            val shuffled = prendasUsuario.shuffled()
            val cantidad = minOf(maxSuggestions, prendasUsuario.size / 2)

            repeat(cantidad) { index ->
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

        println("Generadas ${sugerencias.size} sugerencias")
        return sugerencias.take(maxSuggestions)
    }
}