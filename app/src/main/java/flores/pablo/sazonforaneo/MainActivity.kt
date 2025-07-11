package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.ui.ExplorarActivity
import com.google.firebase.auth.FirebaseAuthException

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEntrar: Button
    private lateinit var tvRegistrarme: TextView
    private lateinit var tvOlvidaste: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización
        auth = FirebaseAuth.getInstance()
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnEntrar = findViewById(R.id.btnEntrar)
        tvRegistrarme = findViewById(R.id.tvNoCuentaParte2)
        tvOlvidaste = findViewById(R.id.tvOlvidaste)

        btnEntrar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (correo.isEmpty()) {
                etCorreo.error = "El correo no puede estar vacío"
            } else if (password.isEmpty()) {
                etPassword.error = "La contraseña no puede estar vacía"
            } else {
                auth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, ExplorarActivity::class.java)
                            intent.putExtra("fragment_to_show", "explorar")
                            startActivity(intent)
                            finish()
                        }
                        else {
                            val errorMessage = when (val errorCode = (task.exception as? FirebaseAuthException)?.errorCode) {
                                "ERROR_INVALID_EMAIL" -> "Correo electrónico no válido"
                                "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta"
                                "ERROR_USER_NOT_FOUND" -> "Usuario no encontrado"
                                "ERROR_USER_DISABLED" -> "Usuario deshabilitado"
                                "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Intenta más tarde"
                                else -> "Error al iniciar sesión. Verifica tus datos."
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        tvRegistrarme.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        tvOlvidaste.setOnClickListener {
            val intent = Intent(this, RecuperarPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
