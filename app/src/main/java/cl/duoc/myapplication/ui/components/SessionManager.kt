package cl.duoc.myapplication.ui.components

import android.content.Context
import cl.duoc.myapplication.database.AppDatabase
import cl.duoc.myapplication.model.User
import cl.duoc.myapplication.repository.UserRepository

class SessionManager(context: Context) {
    private val userRepository: UserRepository by lazy {
        val database = AppDatabase.getInstance(context)
        UserRepository(database.userDao(), database.sessionDao())
    }

    suspend fun login(email: String, password: String): Boolean {
        return userRepository.login(email, password)
    }

    suspend fun register(user: User): Boolean {
        return userRepository.register(user)
    }

    suspend fun logout(): Boolean {
        return userRepository.logout()
    }

    suspend fun getCurrentUser(): String? {
        return userRepository.getCurrentUser()?.nombre
    }

    suspend fun isLoggedIn(): Boolean {
        return userRepository.isUserLoggedIn()
    }
}