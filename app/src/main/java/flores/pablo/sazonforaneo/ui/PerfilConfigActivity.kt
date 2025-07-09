package flores.pablo.sazonforaneo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import flores.pablo.sazonforaneo.MainActivity
import flores.pablo.sazonforaneo.R

class PerfilConfigActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvRecetasCreadas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_config)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias
        tvNombre = findViewById(R.id.nombreTextView)
        tvCorreo = findViewById(R.id.correoTextView)
        tvRecetasCreadas = findViewById(R.id.recetasCreadasTextView)

        cargarDatosUsuario()

        // Editar perfil
        val editarInfo: TextView = findViewById(R.id.infoPersonal)
        editarInfo.setOnClickListener {
            val intento = Intent(this, editar_informacion_perfil_activity::class.java)
            startActivity(intento)
        }

        // Mis recetas
        val misRecetas: TextView = findViewById(R.id.misRecetas)
        misRecetas.setOnClickListener {
            val intent = Intent(this, ExplorarActivity::class.java)
            intent.putExtra("fragment_to_show", "mis_recetas")
            startActivity(intent)
        }

        // Recetas guardadas
        val recetasGuardadas: TextView = findViewById(R.id.recetasGuardadas)
        recetasGuardadas.setOnClickListener {
            val intent = Intent(this, ExplorarActivity::class.java)
            intent.putExtra("fragment_to_show", "recetas_guardadas")
            startActivity(intent)
        }

        // Cerrar sesión
        val btnCerrarSesion: TextView = findViewById(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    val nombre = doc.getString("nombre") ?: "Nombre desconocido"
                    tvNombre.text = nombre
                }
                .addOnFailureListener {
                    Toast.makeText(this, "No se pudo cargar el nombre", Toast.LENGTH_SHORT).show()
                }

            val correo = auth.currentUser?.email
            tvCorreo.text = correo ?: "Correo no disponible"

            db.collection("recetas")
                .whereEqualTo("autor", uid)
                .get()
                .addOnSuccessListener { docs ->
                    val cantidad = docs.size()
                    tvRecetasCreadas.text = cantidad.toString()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "No se pudieron contar las recetas", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
