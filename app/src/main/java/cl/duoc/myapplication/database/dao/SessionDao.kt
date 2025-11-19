package cl.duoc.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.myapplication.model.Session
import cl.duoc.myapplication.model.User

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createSession(session: Session)

    @Query("UPDATE sessions SET isActive = 0 WHERE userId = :userId")
    suspend fun logout(userId: String)

    @Query("SELECT* FROM sessions WHERE isActive = 1 LIMIT 1 ")
    suspend fun getActiveSession(): Session?

    @Query("SELECT users. * FROM users INNER JOIN sessions ON users.email = sessions.email WHERE sessions.isActive = 1 LIMIT 1 ")
    suspend fun getCurrentUser(): User?
}