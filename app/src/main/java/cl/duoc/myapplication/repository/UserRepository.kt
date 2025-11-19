package cl.duoc.myapplication.repository
import cl.duoc.myapplication.database.dao.UserDao
import cl.duoc.myapplication.database.dao.SessionDao
import cl.duoc.myapplication.model.User
import cl.duoc.myapplication.model.Session

class UserRepository(private val userDao: UserDao, private val sessionDao: SessionDao) {

    suspend fun register(user: User): Boolean {
        return try {
            userDao.insertUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val user = userDao.login(email, password)
            user?.let {
                sessionDao.createSession(Session(userId = email, email = email))
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val activeSession = sessionDao.getActiveSession()
            activeSession?.let {
                sessionDao.logout(it.userId)
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            sessionDao.getCurrentUser()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return try {
            sessionDao.getActiveSession() != null
        } catch (e: Exception) {
            false
        }
    }
}