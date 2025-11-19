package cl.duoc.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val nombre: String,
    val apellido: String,
    val email: String,
    val edad: Int?,
    val password: String,
    val aceptaTerminos: Boolean = false,
    val quiereNotificaciones: Boolean = false,
    val fechaRegistro: Long = System.currentTimeMillis()
)