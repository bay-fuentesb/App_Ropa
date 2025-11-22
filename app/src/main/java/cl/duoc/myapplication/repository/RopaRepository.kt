package cl.duoc.myapplication.repository

import android.util.Log
import cl.duoc.myapplication.database.remote.RetroFitInstance
import cl.duoc.myapplication.model.Prenda

class RopaRepository{

    private val apiService = RetroFitInstance.api

    //Funciones API nube

    //1. Obtener prendas
    suspend fun obtenerPrendaEnApi(): List<Prenda>?{
        return try {
            val response = apiService.obtenerPrendas()
            if(response.isSuccessful){
                response.body()
            } else {
                Log.e("API_ERROR" , " Error: ${response.code()}")
                null
            }
        }catch (e:Exception){
            Log.e("API_ERROR", "Fallo conexion: ${e.message}")
            null
        }
    }

    //2. Crear prenda nueva
    suspend fun crearPrendaEnApi(prenda:Prenda): Prenda?{
        return try {
            val response = apiService.crearPrenda(prenda)
            if(response.isSuccessful){
                response.body()
            } else {
                Log.e("API_ERROR" , " Error al crear: ${response.code()}")
                null
            }
        }catch (e:Exception){
            Log.e("API_ERROR", "Fallo conexion al crear: ${e.message}")
            null
        }
    }

    // 3. Eliminar prenda en AWS
    suspend fun eliminarPrendaEnApi(id: Long): Boolean {
        return try {
            val response = apiService.eliminarPrenda(id) // Necesitas agregar esto a tu Interfaz RopaApiService primero*
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("API_ERROR", "Fallo al eliminar: ${e.message}")
            false
        }
    }
}