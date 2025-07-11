package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AgregarNombreDescripcion : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnContinuar: Button
    private lateinit var tvTitulo: TextView  //este hace referencia al titulo

    private val usuarioRepo = UsuarioRepository()

    private var recetaEnEdicion: Receta? = null // variable para guardar la receta si estamos editando


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
        tvTitulo = findViewById(R.id.tvTitulo)

        //  obtener la receta si estamos en modo edicion
        recetaEnEdicion = intent.getSerializableExtra("receta_para_editar") as? Receta

        if (recetaEnEdicion != null) {
            // recargar datos
            tvTitulo.text = "Editar Receta"
            etNombre.setText(recetaEnEdicion!!.nombre)
            etDescripcion.setText(recetaEnEdicion!!.descripcion)
        } else {
            // asegurarse de que el título sea "Nueva Receta" si no estamos editando
            tvTitulo.text = "Nueva Receta"
        }


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

        // obtener el nombre del autor solo si es una nueva receta o si el autorId de la recetaEnEdicion esta vacio
        if (recetaEnEdicion == null || recetaEnEdicion!!.autorId.isEmpty()) {
            usuarioRepo.obtenerNombreUsuarioActual(
                onSuccess = { nombre ->
                    // No necesitamos 'autorNombre' si lo guardamos directamente en la receta
                    btnContinuar.isEnabled = true
                },
                onError = { errorMsg ->
                    Toast.makeText(this, "No se pudo obtener el nombre del autor: $errorMsg", Toast.LENGTH_SHORT).show()
                    btnContinuar.isEnabled = true
                }
            )
        } else {
            // Si estamos editando y la receta ya tiene un autorId, habilitar botón
            btnContinuar.isEnabled = true
        }



        btnContinuar.setOnClickListener {
            val nombreReceta = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()

            if (nombreReceta.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // 2. Crear o actualizar el objeto Receta
            val recetaActual: Receta = recetaEnEdicion ?: Receta(autorId = uid) // Si es nueva, crea una nueva con autorId

            recetaActual.nombre = nombreReceta
            recetaActual.descripcion = descripcion
            recetaActual.autorId = uid // Asegurarse de que el autorId esté siempre

            val intent = Intent(this, AgregarVisibilidad::class.java)
            intent.putExtra("receta", recetaActual)
            startActivity(intent)
        }
    }
}

