package flores.pablo.sazonforaneo

import com.google.firebase.firestore.FirebaseFirestore

class RecetaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("recetas")

    fun obtenerRecetas(
        onSuccess: (List<Receta>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { snapshot ->
                val recetas = snapshot.toObjects(Receta::class.java)
                onSuccess(recetas)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun guardarReceta(
        receta: Receta,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (receta.id.isNullOrEmpty()) {
            val docRef = collection.document()
            receta.id = docRef.id
            docRef.set(receta)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        } else {
            collection.document(receta.id!!)
                .set(receta)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }
}
