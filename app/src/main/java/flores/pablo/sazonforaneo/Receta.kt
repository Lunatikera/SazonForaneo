package flores.pablo.sazonforaneo

import android.net.Uri
import java.io.Serializable

data class Receta(
    var id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var categorias: List<String> = emptyList(),
    var etiquetas: List<String> = emptyList(),
    var visibilidad: String = "publica",
    var ingredientes: List<String> = emptyList(),
    var instrucciones: String = "",
    var fuente: String = "",
    var autorId: String = "",
    var rating: Float = 0.0f,
    var imagenUriString: String? = null
) : Serializable {

    fun getImagenUri(): Uri? = imagenUriString?.let { Uri.parse(it) }
}
