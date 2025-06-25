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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import flores.pablo.sazonforaneo.ui.ExplorarActivity

class AgregarImagenFuente : AppCompatActivity() {
    private lateinit var receta: Receta
    private lateinit var imageView: ImageView
    private lateinit var etFuente: EditText
    private lateinit var btnFinalizar: Button

    private var imagenUri: Uri? = null
    private val SELECT_IMAGE_REQUEST = 1

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
            // Guardar la URI como string, no como Uri
            receta.imagenUriString = imagenUri?.toString()

            Toast.makeText(this, "Receta completada con éxito", Toast.LENGTH_LONG).show()

            val intent = Intent(this, ExplorarActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.data
            imageView.setImageURI(imagenUri)
        }
    }
}