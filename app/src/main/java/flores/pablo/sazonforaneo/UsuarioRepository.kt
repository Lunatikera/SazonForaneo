package flores.pablo.sazonforaneo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import flores.pablo.sazonforaneo.ui.Usuario

class UsuarioRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registrarUsuario(
        nombre: String,
        correo: String,
        fecha: String,
        genero: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        onError("Error inesperado: usuario no encontrado")
                        return@addOnCompleteListener
                    }
                    val imagenPredeterminadaUrl = "https://res.cloudinary.com/dx8nxf9xf/image/upload/v1752053087/vector-chef-avatar-illustration_vfmxgg.jpg"

                    val userMap = hashMapOf(
                        "nombre" to nombre,
                        "correo" to correo,
                        "fechaNacimiento" to fecha,
                        "genero" to genero,
                        "imagenPerfil" to imagenPredeterminadaUrl
                    )
                    db.collection("usuarios").document(uid).set(userMap)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError("Error al guardar datos: ${e.message}") }
                } else {
                    onError(task.exception?.message ?: "Error al registrar usuario")
                }
            }
    }


    fun obtenerNombreUsuarioActual(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onError("Usuario no autenticado")
            return
        }

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onError("No existe el documento para el usuario $uid")
                    return@addOnSuccessListener
                }
                val nombre = snapshot.getString("nombre")
                if (nombre != null) {
                    onSuccess(nombre)
                } else {
                    onError("Campo 'nombre' no encontrado en Firestore para el usuario $uid")
                }
            }
            .addOnFailureListener { e ->
                onError("Error al obtener usuario: ${e.message}")
            }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid


    fun obtenerNombrePorId(
        uid: String,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val nombre = snapshot.getString("nombre")
                if (nombre != null) {
                    onSuccess(nombre)
                } else {
                    onError()
                }
            }
            .addOnFailureListener {
                onError()
            }
    }

    fun obtenerUsuarioActual(onComplete: (Usuario?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onComplete(null)
            return
        }
        FirebaseFirestore.getInstance().collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    onComplete(usuario)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


}
