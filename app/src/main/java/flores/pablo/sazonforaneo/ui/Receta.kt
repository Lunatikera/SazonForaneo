package flores.pablo.sazonforaneo.ui

import android.net.Uri
import java.io.Serializable

data class Receta(
    var nombre: String = "",
    var descripcion: String = "",
    var categorias: List<String> = emptyList(),
    var etiquetas: ArrayList<String>,
    var visibilidad: String = "",
    var ingredientes: List<String> = emptyList(),
    var instrucciones: String = "",
    var fuente: String = "",
    var imagenUri: Uri? = null,
    var owner: Usuario
) : Serializable
