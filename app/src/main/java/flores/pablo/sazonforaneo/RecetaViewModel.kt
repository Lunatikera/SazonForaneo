package flores.pablo.sazonforaneo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecetaViewModel : ViewModel() {

    private val repository = RecetaRepository()

    private val _recetas = MutableLiveData<List<Receta>>()
    val recetas: LiveData<List<Receta>> = _recetas

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> = _error

    private val _guardadoExitoso = MutableLiveData<Boolean>()
    val guardadoExitoso: LiveData<Boolean> = _guardadoExitoso

    fun cargarRecetas() {
        repository.obtenerRecetas(
            onSuccess = { lista ->
                _recetas.postValue(lista)
                _error.postValue(null)
            },
            onFailure = { e ->
                _error.postValue(e)
            }
        )
    }

    fun guardarReceta(receta: Receta) {
        repository.guardarReceta(
            receta,
            onSuccess = {
                _guardadoExitoso.postValue(true)
                _error.postValue(null)
                cargarRecetas()
            },
            onFailure = { e ->
                _error.postValue(e)
                _guardadoExitoso.postValue(false)
            }
        )
    }
}
