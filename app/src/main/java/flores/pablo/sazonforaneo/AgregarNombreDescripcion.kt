package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AgregarNombreDescripcion : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnContinuar: Button

    private val usuarioRepo = UsuarioRepository()

    private var autorNombre: String = "Usuario"
    private var autorId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nombre_descripcion)

        val uid = usuarioRepo.getCurrentUserId()
        if (uid == null) {
            Toast.makeText(this, "Usuario no está autenticado", Toast.LENGTH_SHORT).show()
            return
        }
        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnContinuar = findViewById(R.id.btnContinuar)

        btnContinuar.isEnabled = false // deshabilitado al inicio

        autorId = usuarioRepo.getCurrentUserId() ?: ""

        usuarioRepo.obtenerNombreUsuarioActual(
            onSuccess = { nombre ->
                autorNombre = nombre
                btnContinuar.isEnabled = true
            },
            onError = { errorMsg ->
                Toast.makeText(this, "No se pudo obtener el nombre: $errorMsg", Toast.LENGTH_SHORT)
                    .show()
                btnContinuar.isEnabled = true
            }
        )

        btnContinuar.setOnClickListener {
            val nombreReceta = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()

            if (nombreReceta.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val receta = Receta(
                nombre = nombreReceta,
                descripcion = descripcion,
                autorId = autorId
            )

            val intent = Intent(this, AgregarVisibilidad::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
    }
}

