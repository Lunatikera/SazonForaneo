package flores.pablo.sazonforaneo

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository()

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> = _registroExitoso

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun validarDatos(
        nombre: String,
        correo: String,
        fecha: String,
        genero: String,
        pass: String,
        confirmar: String
    ): Boolean {
        if (nombre.isEmpty()) {
            _error.value = "Nombre obligatorio"
            return false
        }
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _error.value = "Correo inválido"
            return false
        }
        if (fecha.isEmpty()) {
            _error.value = "Fecha de nacimiento obligatoria"
            return false
        }
        if (genero == "Selecciona un Genero") {
            _error.value = "Selecciona un género"
            return false
        }
        if (pass.length < 6) {
            _error.value = "La contraseña debe tener mínimo 6 caracteres"
            return false
        }
        if (pass != confirmar) {
            _error.value = "Las contraseñas no coinciden"
            return false
        }
        _error.value = null
        return true
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        fecha: String,
        genero: String,
        pass: String
    ) {
        usuarioRepository.registrarUsuario(nombre, correo, fecha, genero, pass,
            onSuccess = {
                _registroExitoso.postValue(true)
            },
            onError = { mensaje ->
                _error.postValue(mensaje)
                _registroExitoso.postValue(false)
            })
    }
}
