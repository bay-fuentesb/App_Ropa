package cl.duoc.myapplication.repository

import cl.duoc.myapplication.model.OutfitSugerido
import cl.duoc.myapplication.model.Prenda

class OutfitRepository {

    fun generarSugerenciasOutfits(prendasUsuario: List<Prenda>): List<OutfitSugerido> {
        if (prendasUsuario.size < 2) {
            return emptyList()
        }

        val sugerencias = mutableListOf<OutfitSugerido>()
        val prendasPorCategoria = prendasUsuario.groupBy { it.categoria }

        val superiores = (prendasPorCategoria["Polera"] ?: emptyList()) +
                (prendasPorCategoria["Poleron"] ?: emptyList()) +
                (prendasPorCategoria["Chaqueta"] ?: emptyList()) +
                (prendasPorCategoria["Parka"] ?: emptyList())

        val inferiores = prendasPorCategoria["Pantalones"] ?: emptyList()
        val calzados = prendasPorCategoria["Zapatilla"] ?: emptyList()

        val tops = superiores
        val bottoms = inferiores
        val shoes = calzados

        val maxSuggestions = 3

        if (tops.isNotEmpty() && bottoms.isNotEmpty() && shoes.isNotEmpty()) {
            val combos = mutableListOf<List<Prenda>>()
            for (t in tops.take(2)) {
                for (b in bottoms.take(2)) {
                    for (s in shoes.take(1)) {
                        combos.add(listOf(t, b, s))
                    }
                }
            }
            combos.shuffled().take(maxSuggestions).forEachIndexed { index, combinacion ->
                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Outfit Completo ${index + 1}",
                        combinacion = combinacion,

                    )
                )
            }
        }

        if (sugerencias.size < maxSuggestions && tops.isNotEmpty() && bottoms.isNotEmpty()) {
            val combos = mutableListOf<List<Prenda>>()
            for (t in tops) {
                for (b in bottoms) {
                    combos.add(listOf(t, b))
                }
            }
            combos.shuffled().take(maxSuggestions - sugerencias.size).forEachIndexed { index, combinacion ->
                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Outfit Superior+Inferior ${sugerencias.size + 1}",
                        combinacion = combinacion,

                    )
                )
            }
        }

        if (sugerencias.isEmpty()) {
            val mezcladas = prendasUsuario.shuffled()
            val cantidad = minOf(maxSuggestions, prendasUsuario.size)
            for (i in 0 until cantidad) {
                val first = mezcladas[i]
                val second = mezcladas.filter { it != first }.randomOrNull()
                if (second != null) {
                    sugerencias.add(
                        OutfitSugerido(
                            nombre = "Combinación Aleatoria ${i + 1}",
                            combinacion = listOf(first, second),

                        )
                    )
                }
            }
        }

        return sugerencias.take(maxSuggestions)
    }

    fun generarOutfitAleatorio(prendasUsuario: List<Prenda>): OutfitSugerido? {
        if (prendasUsuario.isEmpty()) return null

        val prendasPorCategoria = prendasUsuario.groupBy { it.categoria }

        val superiores = (prendasPorCategoria["Polera"] ?: emptyList()) +
                (prendasPorCategoria["Poleron"] ?: emptyList()) +
                (prendasPorCategoria["Chaqueta"] ?: emptyList()) +
                (prendasPorCategoria["Parka"] ?: emptyList())
        val inferiores = prendasPorCategoria["Pantalones"] ?: emptyList()
        val calzados = prendasPorCategoria["Zapatilla"] ?: emptyList()

        val seleccion = mutableListOf<Prenda>()

        superiores.shuffled().firstOrNull()?.let { seleccion.add(it) }
        inferiores.shuffled().firstOrNull()?.let { seleccion.add(it) }
        calzados.shuffled().firstOrNull()?.let { seleccion.add(it) }

        val restantes = prendasUsuario.filter { it !in seleccion }.shuffled()
        var idx = 0
        while (seleccion.size < minOf(3, prendasUsuario.size) && idx < restantes.size) {
            seleccion.add(restantes[idx])
            idx++
        }

        if (seleccion.size < 2) return null

        return OutfitSugerido(
            nombre = "Outfit Aleatorio",
            combinacion = seleccion,

        )
    }
}