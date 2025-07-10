package flores.pablo.sazonforaneo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritosRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun agregarFavorito(
        recetaId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: run {
            onError("Usuario no autenticado")
            return
        }
        val data = hashMapOf("recetaId" to recetaId)

        db.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .document(recetaId)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error agregando favorito") }
    }

    fun eliminarFavorito(
        recetaId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: run {
            onError("Usuario no autenticado")
            return
        }

        db.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .document(recetaId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error eliminando favorito") }
    }

    fun esFavorito(
        recetaId: String,
        onResult: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: run {
            onError("Usuario no autenticado")
            return
        }

        db.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .document(recetaId)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.exists())
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error consultando favorito")
            }
    }
}
