package flores.pablo.sazonforaneo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre

    private val _correo = MutableLiveData<String>()
    val correo: LiveData<String> = _correo

    private val _recetasCreadas = MutableLiveData<Int>()
    val recetasCreadas: LiveData<Int> = _recetasCreadas

    private val _imagenPerfilUrl = MutableLiveData<String?>()
    val imagenPerfilUrl: LiveData<String?> = _imagenPerfilUrl

    fun cargarDatosUsuario() {
        val user = auth.currentUser
        _correo.value = user?.email ?: "Correo no disponible"

        user?.uid?.let { uid ->
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    _nombre.value = doc.getString("nombre") ?: "Nombre desconocido"
                    _imagenPerfilUrl.value = doc.getString("imagenPerfil") // <- Aquí va la imagen
                }

            db.collection("recetas")
                .whereEqualTo("autor", uid)
                .get()
                .addOnSuccessListener { docs ->
                    _recetasCreadas.value = docs.size()
                }
        }
    }
    fun actualizarImagenPerfil(url: String) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // Actualiza en Firestore
        db.collection("usuarios").document(uid)
            .update("imagenPerfil", url)
            .addOnSuccessListener {
                // Actualiza el LiveData para reflejar el cambio en la UI
                _imagenPerfilUrl.value = url
            }
            .addOnFailureListener {
                // Opcional: maneja error
            }
    }


    fun cerrarSesion() {
        auth.signOut()
    }
}
