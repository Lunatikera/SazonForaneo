package flores.pablo.sazonforaneo.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import flores.pablo.sazonforaneo.MainActivity
import flores.pablo.sazonforaneo.R
import androidx.activity.viewModels

class PerfilConfigActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        private const val CLOUD_NAME = "dx8nxf9xf"
        private const val UPLOAD_PRESET = "usuarios-preset"
    }

    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvRecetasCreadas: TextView
    private lateinit var ivFotoPerfil: ImageView
    private lateinit var ivEditarFoto: ImageView

    private val viewModel: UsuarioViewModel by viewModels()

    private var imagenUriTemporal: Uri? = null
    private var imagenUriOriginal: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_config)

        try {
            val config = hashMapOf("cloud_name" to CLOUD_NAME)
            MediaManager.init(this, config)
        } catch (e: IllegalStateException) {
        }

        tvNombre = findViewById(R.id.nombreTextView)
        tvCorreo = findViewById(R.id.correoTextView)
        tvRecetasCreadas = findViewById(R.id.recetasCreadasTextView)
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        ivEditarFoto = findViewById(R.id.ivEditarFoto)

        observarViewModel()
        configurarListeners()

        viewModel.cargarDatosUsuario()
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarDatosUsuario()
    }

    private fun observarViewModel() {
        viewModel.nombre.observe(this) { tvNombre.text = it }
        viewModel.correo.observe(this) { tvCorreo.text = it }
        viewModel.recetasCreadas.observe(this) { tvRecetasCreadas.text = it.toString() }

        viewModel.imagenPerfilUrl.observe(this) { url ->
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
                    .circleCrop()
                    .into(ivFotoPerfil)
                imagenUriOriginal = null
            } else {
                ivFotoPerfil.setImageResource(R.drawable.imagen_predeterminada)
            }
        }
    }

    private fun configurarListeners() {
        findViewById<TextView>(R.id.infoPersonal).setOnClickListener {
            startActivity(Intent(this, editar_informacion_perfil_activity::class.java))
        }

        ivEditarFoto.setOnClickListener {
            abrirSelectorImagen()
        }

        findViewById<TextView>(R.id.misRecetas).setOnClickListener {
            val intent = Intent(this, ExplorarActivity::class.java)
            intent.putExtra("fragment_to_show", "mis_recetas")
            intent.putExtra("where_from", true)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.recetasGuardadas).setOnClickListener {
            val intent = Intent(this, ExplorarActivity::class.java)
            intent.putExtra("fragment_to_show", "mis_recetas")
            intent.putExtra("where_from", false)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnCerrarSesion).setOnClickListener {
            viewModel.cerrarSesion()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun abrirSelectorImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uriSeleccionada = data?.data ?: return

            // Guardamos URI original por si cancela
            imagenUriTemporal = uriSeleccionada

            // Mostrar preview temporal
            ivFotoPerfil.setImageURI(imagenUriTemporal)

            // Preguntar si confirma
            mostrarDialogoConfirmacion()
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar cambio")
            .setMessage("¿Quieres cambiar la foto de perfil?")
            .setPositiveButton("Sí") { _, _ -> subirImagenCloudinary() }
            .setNegativeButton("No") { _, _ ->
                // Volver a la imagen original
                if (viewModel.imagenPerfilUrl.value.isNullOrEmpty()) {
                    ivFotoPerfil.setImageResource(R.drawable.imagen_predeterminada)
                } else {
                    Glide.with(this)
                        .load(viewModel.imagenPerfilUrl.value)
                        .circleCrop()
                        .into(ivFotoPerfil)
                }
                imagenUriTemporal = null
            }
            .setCancelable(false)
            .show()
    }

    private fun subirImagenCloudinary() {
        val uri = imagenUriTemporal ?: return

        MediaManager.get().upload(uri)
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String
                    if (url != null) {
                        viewModel.actualizarImagenPerfil(url)
                        Toast.makeText(this@PerfilConfigActivity, "Foto actualizada", Toast.LENGTH_SHORT).show()
                    }
                    imagenUriTemporal = null
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(this@PerfilConfigActivity, "Error al subir imagen: ${error?.description}", Toast.LENGTH_LONG).show()
                    // Revertir imagen a original
                    if (viewModel.imagenPerfilUrl.value.isNullOrEmpty()) {
                        ivFotoPerfil.setImageResource(R.drawable.imagen_predeterminada)
                    } else {
                        Glide.with(this@PerfilConfigActivity)
                            .load(viewModel.imagenPerfilUrl.value)
                            .circleCrop()
                            .into(ivFotoPerfil)
                    }
                    imagenUriTemporal = null
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }
}
