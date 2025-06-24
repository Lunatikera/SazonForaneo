package flores.pablo.sazonforaneo

import java.io.Serializable

data class Receta(
    var nombre: String,
    var descripcion: String,
    var categorias: List<String>,
    var etiquetas: List<String>,
    var visibilidad: String,
    var ingredientes: MutableList<String> = mutableListOf()
) : java.io.Serializable
