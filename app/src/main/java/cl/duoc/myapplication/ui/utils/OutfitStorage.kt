package cl.duoc.myapplication.ui.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cl.duoc.myapplication.model.OutfitSugerido

class OutfitStorage(context: Context) {
    // Nombre del archivo interno donde guardaremos los datos
    private val prefs: SharedPreferences = context.getSharedPreferences("outfit_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Guardar la lista completa de outfits
    fun guardarOutfits(lista: List<OutfitSugerido>) {
        val json = gson.toJson(lista) // Convertimos objetos a Texto JSON
        prefs.edit().putString("mis_outfits", json).apply()
    }

    // Cargar la lista al iniciar la app
    fun cargarOutfits(): List<OutfitSugerido> {
        val json = prefs.getString("mis_outfits", null) ?: return emptyList()

        // Truco de Gson para entender listas complejas
        val type = object : TypeToken<List<OutfitSugerido>>() {}.type
        return gson.fromJson(json, type)
    }

    fun limpiar() {
        prefs.edit().clear().apply()
    }
}

