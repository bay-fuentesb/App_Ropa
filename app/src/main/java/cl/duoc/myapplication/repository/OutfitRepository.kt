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

        // REGLA 1: Combinar Polera/Poleron con cualquier pantalón (simulado)
        val prendasSuperiores = (prendasPorCategoria["Polera"] ?: emptyList()) +
                (prendasPorCategoria["Poleron"] ?: emptyList()) +
                (prendasPorCategoria["Chaqueta"] ?: emptyList()) +
                (prendasPorCategoria["Parka"] ?: emptyList())

        val prendasInferiores = (prendasPorCategoria["Zapatilla"] ?: emptyList()) // Simulamos como pantalones

        if (prendasSuperiores.isNotEmpty() && prendasInferiores.isNotEmpty()) {
            // Combinación básica superior + inferior
            for (i in 0 until minOf(2, prendasSuperiores.size)) {
                for (j in 0 until minOf(2, prendasInferiores.size)) {
                    val combinacion = listOf(prendasSuperiores[i], prendasInferiores[j])

                    sugerencias.add(
                        OutfitSugerido(
                            nombre = "Outfit Casual ${sugerencias.size + 1}",
                            combinacion = combinacion,
                            puntuacion = 7,
                            motivo = "Combinación equilibrada para el día a día"
                        )
                    )
                }
            }
        }

        // REGLA 2: Agregar accesorios a combinaciones existentes
        val accesorios = prendasPorCategoria["Accesorios"] ?: emptyList()
        val jockeys = prendasPorCategoria["Jockeys"] ?: emptyList()

        if ((accesorios.isNotEmpty() || jockeys.isNotEmpty()) && sugerencias.isNotEmpty()) {
            val accesorio = (accesorios + jockeys).firstOrNull()
            if (accesorio != null) {
                val outfitConAccesorio = sugerencias.first().copy(
                    combinacion = sugerencias.first().combinacion + accesorio,
                    puntuacion = 8,
                    motivo = "${sugerencias.first().motivo} con ${accesorio.titulo}"
                )
                sugerencias.add(0, outfitConAccesorio)
            }
        }

        // REGLA 3: Combinación aleatoria si hay pocas sugerencias
        if (sugerencias.isEmpty() && prendasUsuario.size >= 2) {
            val prenda1 = prendasUsuario.random()
            val prenda2 = prendasUsuario.filter { it != prenda1 }.randomOrNull()

            if (prenda2 != null) {
                sugerencias.add(
                    OutfitSugerido(
                        nombre = "Combinación Aleatoria",
                        combinacion = listOf(prenda1, prenda2),
                        puntuacion = 6,
                        motivo = "Combinación creada con lo que tienes disponible"
                    )
                )
            }
        }

        return sugerencias.take(3) // Máximo 3 sugerencias
    }

    fun generarOutfitAleatorio(prendasUsuario: List<Prenda>): OutfitSugerido? {
        if (prendasUsuario.size < 2) return null

        val prendasSeleccionadas = mutableListOf<Prenda>()

        // Seleccionar 2-3 prendas aleatorias
        val cantidad = minOf(3, prendasUsuario.size)
        val prendasMezcladas = prendasUsuario.shuffled()

        for (i in 0 until cantidad) {
            prendasSeleccionadas.add(prendasMezcladas[i])
        }

        return OutfitSugerido(
            nombre = "Outfit Aleatorio",
            combinacion = prendasSeleccionadas,
            puntuacion = 6,
            motivo = "Combinación aleatoria creada con tus prendas"
        )
    }
}