package flores.pablo.sazonforaneo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecetaViewModel : ViewModel() {

    private val repository = RecetaRepository()
    private val etiquetaRepository = EtiquetasRepository()

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

    fun cargarRecetasPorCategoria(nombreCategoria: String) {
        repository.obtenerRecetas(
            onSuccess = { lista ->
                val filtradas = lista.filter { it.categorias.contains(nombreCategoria) }
                _recetas.postValue(filtradas)
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
                etiquetaRepository.agregarOActualizarEtiquetas(receta.etiquetas)
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

    fun eliminarReceta(receta: Receta) {
        repository.eliminarReceta(
            receta.id,
            onSuccess = {
                etiquetaRepository.restarUsoEtiquetas(receta.etiquetas)
                _guardadoExitoso.postValue(true)  // o un LiveData separado para eliminar si quieres diferenciar
                _error.postValue(null)
                cargarRecetasPorAutor(receta.autorId)
            },
            onFailure = { e ->
                _error.postValue(e)
                _guardadoExitoso.postValue(false)
            }
        )
    }

    fun cargarRecetasPorAutor(autorId: String) {
        repository.obtenerRecetasPorAutor(
            autorId,
            onSuccess = { lista ->
                _recetas.postValue(lista)
                _error.postValue(null)
            },
            onError = { errorMsg ->
                _error.postValue(Exception(errorMsg))
            }
        )
    }

    fun cargarRecetasFavoritasPor(userId: String) {
        repository.obtenerRecetasFavoritasPor(
            userId,
            onSuccess = { lista ->
                _recetas.postValue(lista)
                _error.postValue(null)
            },
            onError = { errorMsg ->
                _error.postValue(Exception(errorMsg))
            }
        )
    }

    fun actualizarReceta(receta: Receta) {
        repository.guardarReceta(
            receta,
            onSuccess = {
                etiquetaRepository.agregarOActualizarEtiquetas(receta.etiquetas) // si quieres manejar etiquetas
                _guardadoExitoso.postValue(true)
                _error.postValue(null)
                cargarRecetasPorAutor(receta.autorId)
            },
            onFailure = { e ->
                _error.postValue(e)
                _guardadoExitoso.postValue(false)
            }
        )
    }

    fun actualizarVisibilidad(recetaId: String, nuevaVisibilidad: String) {
        repository.actualizarCampoVisibilidad(
            recetaId,
            nuevaVisibilidad,
            onSuccess = {
                _guardadoExitoso.postValue(true)
                cargarRecetas() // Vuelve a cargar las recetas después de actualizar
            },
            onFailure = { e ->
                _error.postValue(e)
                _guardadoExitoso.postValue(false)
            }
        )
    }


    fun cargarRecetasDestacadas() {
        repository.obtenerRecetasDestacadas(
            onSuccess = { lista ->
                _recetas.postValue(lista)
                _error.postValue(null)
            },
            onError = { errorMsg ->
                _error.postValue(Exception(errorMsg))
            }
        )
    }



}
