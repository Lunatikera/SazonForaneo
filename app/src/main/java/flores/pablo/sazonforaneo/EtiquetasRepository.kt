package flores.pablo.sazonforaneo

import com.google.firebase.firestore.FirebaseFirestore

class EtiquetasRepository {

    private val db = FirebaseFirestore.getInstance()
    private val etiquetasCollection = db.collection("etiquetas") // Nombre de tu colección

    fun obtenerEtiquetas(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        etiquetasCollection.get()
            .addOnSuccessListener { documents ->
                val listaEtiquetas = mutableListOf<String>()
                for (doc in documents) {
                    val tag = doc.getString("nombre")
                    if (!tag.isNullOrEmpty()) {
                        listaEtiquetas.add(tag)
                    }
                }
                onSuccess(listaEtiquetas)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun agregarOActualizarEtiquetas(etiquetas: List<String>) {
        for (nombre in etiquetas) {
            val docRef = etiquetasCollection.document(nombre.lowercase())

            docRef.get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val usosActuales = doc.getLong("usos") ?: 1
                    docRef.update("usos", usosActuales + 1)
                } else {
                    val nuevaEtiqueta = Etiqueta(nombre = nombre, usos = 1)
                    docRef.set(nuevaEtiqueta)
                }
            }
        }
    }
    fun restarUsoEtiquetas(etiquetas: List<String>) {
        for (nombre in etiquetas) {
            val docRef = etiquetasCollection.document(nombre.lowercase())

            docRef.get().addOnSuccessListener { doc ->
                val usosActuales = doc.getLong("usos") ?: return@addOnSuccessListener
                if (usosActuales <= 1) {
                    docRef.delete()
                } else {
                    docRef.update("usos", usosActuales - 1)
                }
            }
        }
    }

    fun reemplazarEtiquetas(anteriores: List<String>, nuevas: List<String>) {
        val aEliminar = anteriores.minus(nuevas.toSet())
        val aAgregar = nuevas.minus(anteriores.toSet())

        restarUsoEtiquetas(aEliminar)
        agregarOActualizarEtiquetas(aAgregar)
    }


}
