package flores.pablo.sazonforaneo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import flores.pablo.sazonforaneo.ui.ExplorarActivity
import java.util.*

class AgregarImagenFuente : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        private const val CLOUD_NAME = "dx8nxf9xf"
        private const val UPLOAD_PRESET = "recetas-preset"
    }

    private lateinit var receta: Receta
    private lateinit var imageView: ImageView
    private lateinit var etFuente: EditText
    private lateinit var btnFinalizar: Button
    private lateinit var tvTitulo: TextView
    private var imagenUriLocal: Uri? = null

    private val recetaViewModel: RecetaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_imagen_fuente)

        // recuperar la receta (que puede ser nueva o para editar)
        receta = intent.getSerializableExtra("receta") as? Receta ?: Receta() // se asegura de tener un objeto Receta

        imageView = findViewById(R.id.imageViewPlatillo)
        etFuente = findViewById(R.id.etFuente)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        tvTitulo = findViewById(R.id.tvTitulo)

        try {
            val config = hashMapOf("cloud_name" to CLOUD_NAME)
            MediaManager.init(this, config)
        } catch (_: IllegalStateException) {
            // ya inicializado, o error al inicializar. Se captura la excepcion.
        }

        // cambia titulo si estamos en modo edicion
        if (receta.id.isNotEmpty()) {
            tvTitulo.text = "Editar Receta"
        } else {
            tvTitulo.text = "Nueva Receta"
        }


        // precargar datos si estamos editando
        if (receta.id.isNotEmpty()) { // si la receta ya tiene un ID, estamos editando
            etFuente.setText(receta.fuente)
            receta.imagenUriString?.let { url ->
                // cargar imagen de Cloudinary si existe
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.imagen_platillo_predeterminada)
                    .into(imageView)
                // importante no establecer imagenUriLocal aqui, ya que es la URL remota
            }
        }


        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        btnFinalizar.setOnClickListener {
            val fuente = etFuente.text.toString().trim()
            if (fuente.isEmpty()) {
                etFuente.error = "Por favor indica la fuente"
                etFuente.requestFocus()
                return@setOnClickListener
            }

            // si es una nueva receta y no se selecciono imagen, o si se esta editando y no hay imagen ni URI local nueva
            if (receta.imagenUriString.isNullOrEmpty() && imagenUriLocal == null) {
                Toast.makeText(this, "Selecciona una imagen para el platillo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnFinalizar.isEnabled = false

            if (imagenUriLocal != null) {
                // hay una nueva imagen seleccionada (o se selecciono una por primera vez)
                MediaManager.get().upload(imagenUriLocal)
                    .unsigned(UPLOAD_PRESET)
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Toast.makeText(this@AgregarImagenFuente, "Subiendo imagen...", Toast.LENGTH_SHORT).show()
                        }
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                            val url = resultData?.get("secure_url") as? String
                            receta.fuente = fuente
                            receta.imagenUriString = url // guarda la nueva URL de la imagen
                            recetaViewModel.guardarReceta(receta)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Toast.makeText(this@AgregarImagenFuente, "Error al subir imagen: ${error?.description}", Toast.LENGTH_LONG).show()
                            btnFinalizar.isEnabled = true
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            } else {
                // si no se selecciono una nueva imagen, pero ya habia una URL de imagen (modo edicion)
                receta.fuente = fuente
                recetaViewModel.guardarReceta(receta)
            }
        }

        recetaViewModel.guardadoExitoso.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "Receta guardada con éxito", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ExplorarActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("mostrarFragmento", "explorar")
                }
                startActivity(intent)
                finish()
            }
        }

        recetaViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_LONG).show()
                btnFinalizar.isEnabled = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedUri = data?.data ?: return
            imagenUriLocal = selectedUri // almacenar la URI local seleccionada
            imageView.setImageURI(imagenUriLocal)
        }
    }
}