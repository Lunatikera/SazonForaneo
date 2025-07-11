package flores.pablo.sazonforaneo

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class RecetaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("recetas")

    fun obtenerRecetas(
        onSuccess: (List<Receta>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection
            .whereEqualTo("visibilidad", "publico")
            .get()
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

    fun obtenerRecetasFavoritasPor(
        userId: String,
        onSuccess: (List<Receta>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .get()
            .addOnSuccessListener { favoritosSnapshot ->
                val recetaIds = favoritosSnapshot.documents.mapNotNull { it.getString("recetaId") }
                if (recetaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val listasChunk = recetaIds.chunked(10)
                val tareas = mutableListOf<com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot>>()
                val recetasAcumuladas = mutableListOf<Receta>()

                for (chunk in listasChunk) {
                    val task = firestore.collection("recetas")
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                    tareas.add(task)
                }

                com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.QuerySnapshot>(tareas)
                    .addOnSuccessListener { resultados ->
                        for (resultado in resultados) {
                            val recetas = resultado.toObjects(Receta::class.java)
                            recetasAcumuladas.addAll(
                                recetas.filter { it.visibilidad == "publico" || it.autorId == userId }
                            )
                        }
                        onSuccess(recetasAcumuladas)
                    }
                    .addOnFailureListener {
                        onError(it.message ?: "Error al obtener recetas favoritas")
                    }
            }
            .addOnFailureListener { onError(it.message ?: "Error al obtener favoritas") }
    }




    fun obtenerRecetasCalificadasPor(
        userId: String,
        onSuccess: (List<Receta>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("recetas")
            .get()
            .addOnSuccessListener { snapshot ->
                val recetas = mutableListOf<Receta>()
                val tareasPendientes = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                for (doc in snapshot.documents) {
                    val receta = doc.toObject(Receta::class.java)
                    if (receta != null) {
                        val calificacionTask = firestore.collection("recetas")
                            .document(doc.id)
                            .collection("calificaciones")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { calificacionDoc ->
                                if (calificacionDoc.exists() &&
                                    (receta.visibilidad == "publico" || receta.autorId == userId)
                                ) {
                                    recetas.add(receta)
                                }
                            }
                        tareasPendientes.add(calificacionTask)
                    }
                }

                com.google.android.gms.tasks.Tasks.whenAllComplete(tareasPendientes)
                    .addOnSuccessListener {
                        onSuccess(recetas)
                    }
                    .addOnFailureListener {
                        onError(it.message ?: "Error al obtener calificaciones")
                    }
            }
            .addOnFailureListener { onError(it.message ?: "Error al obtener recetas") }
    }



    fun obtenerRecetasCreadasYFavoritasPor(
        userId: String,
        onSuccess: (List<Receta>) -> Unit,
        onError: (String) -> Unit
    ) {
        obtenerRecetasPorAutor(userId, onSuccess = { creadas ->
            obtenerRecetasFavoritasPor(userId, onSuccess = { favoritas ->
                val todas = (creadas + favoritas).distinctBy { it.id }
                onSuccess(todas)
            }, onError = onError)
        }, onError = onError)
    }

    fun obtenerRecetasPorAutor(
        autorId: String,
        onSuccess: (List<Receta>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("recetas")
            .whereEqualTo("autorId", autorId)
            .get()
            .addOnSuccessListener { snapshot ->
                val recetas = snapshot.documents.mapNotNull { it.toObject(Receta::class.java) }
                onSuccess(recetas)
            }
            .addOnFailureListener { onError(it.message ?: "Error al obtener recetas del autor") }
    }

    fun eliminarReceta(
        recetaId: String?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (recetaId.isNullOrEmpty()) {
            onFailure(Exception("ID de receta inválido"))
            return
        }

        collection.document(recetaId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun actualizarCampoVisibilidad(
        recetaId: String,
        nuevaVisibilidad: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val recetaRef = collection.document(recetaId)
        recetaRef.update("visibilidad", nuevaVisibilidad)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun obtenerRecetasDestacadas(
        onSuccess: (List<Receta>) -> Unit,
        onError: (String) -> Unit
    ) {
        val db = Firebase.firestore
        db.collection("recetas")
            .whereEqualTo("visibilidad", "publico")
            .whereGreaterThanOrEqualTo("rating", 4.0)
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recetas = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Receta::class.java)?.apply { id = doc.id }
                }
                onSuccess(recetas)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error desconocido")
            }
    }




}
