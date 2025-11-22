package cl.duoc.myapplication.database.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitInstance {
    private const val BASE_URL = "http://98.89.8.213:8080/" // cambiar por la de aws

    val api: RopaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RopaApiService::class.java)
    }
}