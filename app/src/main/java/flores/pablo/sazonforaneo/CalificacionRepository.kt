package flores.pablo.sazonforaneo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CalificacionRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun guardarCalificacion(
        recetaId: String,
        rating: Float,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("Usuario no autenticado")
            return
        }

        val calificacionData = hashMapOf(
            "rating" to rating,
            "userId" to userId
        )

        val calificacionesRef = db.collection("recetas")
            .document(recetaId)
            .collection("calificaciones")

        // Guardar o actualizar la calificación del usuario
        calificacionesRef.document(userId)
            .set(calificacionData)
            .addOnSuccessListener {
                // Luego de guardar la calificación, actualizar el promedio
                obtenerPromedioCalificacion(recetaId, { promedio ->
                    // Actualizar el campo rating en la receta
                    db.collection("recetas").document(recetaId)
                        .update("rating", promedio)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError("Error al actualizar promedio: ${e.message}") }
                }, { errorMsg ->
                    onError(errorMsg)
                })
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar calificación") }
    }

    fun obtenerPromedioCalificacion(
        recetaId: String,
        onResult: (Float) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("recetas")
            .document(recetaId)
            .collection("calificaciones")
            .get()
            .addOnSuccessListener { documentos ->
                val total = documentos.size()
                if (total == 0) {
                    onResult(0f)
                    return@addOnSuccessListener
                }

                var suma = 0f
                for (doc in documentos) {
                    suma += doc.getDouble("rating")?.toFloat() ?: 0f
                }
                val promedio = suma / total
                onResult(promedio)
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al obtener calificaciones") }
    }
}
