package cl.duoc.myapplication.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.myapplication.repository.UserRepository
import cl.duoc.myapplication.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val success = userRepository.login(email, password)
            _loginState.value = success
            if (success) {
                checkCurrentUser()
            }
            success // RETORNAR el resultado
        } catch (e: Exception) {
            _loginState.value = false
            false // RETORNAR false en caso de error
        }
    }

    suspend fun register(user: User): Boolean {
        return try {
            val success = userRepository.register(user)
            if (success) {
                login(user.email, user.password)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _currentUser.value = null
            _loginState.value = false
        }
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userRepository.getCurrentUser()
        }
    }
}
