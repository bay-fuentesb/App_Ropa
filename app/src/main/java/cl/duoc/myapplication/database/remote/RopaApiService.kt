package cl.duoc.myapplication.database.remote
import cl.duoc.myapplication.model.Prenda
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RopaApiService{

    @GET("api/prendas")
    suspend fun obtenerPrendas(): Response<List<Prenda>>


    @POST("api/prendas")
    suspend fun crearPrenda(@Body prenda: Prenda): Response<Prenda>

    @DELETE("api/prendas/{id}")
    suspend fun eliminarPrenda(@Path("id") id: Long): Response<Void>
}