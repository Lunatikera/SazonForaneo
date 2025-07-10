package flores.pablo.sazonforaneo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private var imagenUri: Uri? = null

    private val recetaViewModel: RecetaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_imagen_fuente)

        receta = intent.getSerializableExtra("receta") as Receta

        imageView = findViewById(R.id.imageViewPlatillo)
        etFuente = findViewById(R.id.etFuente)
        btnFinalizar = findViewById(R.id.btnFinalizar)

        try {
            val config = hashMapOf("cloud_name" to CLOUD_NAME)
            MediaManager.init(this, config)
        } catch (_: IllegalStateException) {
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

            if (imagenUri == null) {
                Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnFinalizar.isEnabled = false

            MediaManager.get().upload(imagenUri)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
                        receta.fuente = fuente
                        receta.imagenUriString = url
                        recetaViewModel.guardarReceta(receta)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Toast.makeText(this@AgregarImagenFuente, "Error al subir imagen: ${error?.description}", Toast.LENGTH_LONG).show()
                        btnFinalizar.isEnabled = true
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
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
            imagenUri = selectedUri
            imageView.setImageURI(imagenUri)
        }
    }
}
