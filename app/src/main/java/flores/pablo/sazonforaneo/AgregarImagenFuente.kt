package flores.pablo.sazonforaneo

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
import android.app.Activity
import flores.pablo.sazonforaneo.ui.ExplorarActivity
import java.io.File
import java.io.FileOutputStream

class AgregarImagenFuente : AppCompatActivity() {

    private lateinit var receta: Receta
    private lateinit var imageView: ImageView
    private lateinit var etFuente: EditText
    private lateinit var btnFinalizar: Button
    private var imagenUri: Uri? = null
    private val SELECT_IMAGE_REQUEST = 1

    private val recetaViewModel: RecetaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_imagen_fuente)

        receta = intent.getSerializableExtra("receta") as Receta

        imageView = findViewById(R.id.imageViewPlatillo)
        etFuente = findViewById(R.id.etFuente)
        btnFinalizar = findViewById(R.id.btnFinalizar)

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        btnFinalizar.setOnClickListener {
            val fuente = etFuente.text.toString().trim()
            if (fuente.isEmpty()) {
                etFuente.error = "Por favor indica la fuente"
                etFuente.requestFocus()
                return@setOnClickListener
            }

            receta.fuente = fuente
            receta.imagenUriString = imagenUri?.toString()

            recetaViewModel.guardarReceta(receta)
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
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val sourceUri = data.data ?: return
            val inputStream = contentResolver.openInputStream(sourceUri)
            val fileName = "receta_${System.currentTimeMillis()}.jpg"
            val outputFile = File(cacheDir, fileName)
            val outputStream = FileOutputStream(outputFile)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            val copiedUri = Uri.fromFile(outputFile)
            imagenUri = copiedUri
            imageView.setImageURI(imagenUri)
        }
    }

}
