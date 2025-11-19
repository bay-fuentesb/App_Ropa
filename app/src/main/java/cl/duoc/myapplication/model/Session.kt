package cl.duoc.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session (
    @PrimaryKey
    val userId: String,
    val email: String,
    val loginTime: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)