package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
<<<<<<< Updated upstream
import androidx.appcompat.app.AppCompatActivity
import flores.pablo.sazonforaneo.ui.ExplorarActivity


class MainActivity : AppCompatActivity() {
        private lateinit var etCorreo: EditText
        private lateinit var etPassword: EditText
        private lateinit var btnEntrar: Button
        private lateinit var tvRegistrarme: TextView
        private lateinit var tvOlvidaste: TextView
=======
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var etCorreo: EditText
    lateinit var etPassword: EditText
    lateinit var btnEntrar: Button
    lateinit var btnRegistrarme: Button
    lateinit var tvOlvidaste: TextView //solo la declaramos aqui para evitar errores.
>>>>>>> Stashed changes

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

<<<<<<< Updated upstream
            etCorreo = findViewById(R.id.etCorreo)
            etPassword = findViewById(R.id.etPassword)
            btnEntrar = findViewById(R.id.btnEntrar)
            tvRegistrarme = findViewById(R.id.tvNoCuentaParte2)
            tvOlvidaste = findViewById(R.id.tvOlvidaste)

            btnEntrar.setOnClickListener {
                val correo = etCorreo.text.toString().trim()
                val password = etPassword.text.toString().trim()
=======
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnRegistrarme = findViewById(R.id.btnRegistrarme)
        tvOlvidaste = findViewById(R.id.tvOlvidaste) // inicializamos aqui.
>>>>>>> Stashed changes

                if (correo.isEmpty()) {
                    etCorreo.error = "El correo no puede estar vacío"
                } else if (password.isEmpty()) {
                    etPassword.error = "La contraseña no puede estar vacía"
                } else {
                    try {
                        val intent = Intent(this, ExplorarActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
            tvRegistrarme.setOnClickListener {
                val intent = Intent(this, RegistroActivity::class.java)
                startActivity(intent)
            }

<<<<<<< Updated upstream
            tvOlvidaste.setOnClickListener {
                val intent = Intent(this, RecuperarPasswordActivity::class.java)
                startActivity(intent)
=======
            if (correo.isEmpty()) {
                etCorreo.error = "El correo no puede estar vacío"
            } else if (password.isEmpty()) {
                etPassword.error = "La contraseña no puede estar vacía"
            } else {
                auth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, CategoriasActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "error al iniciar sesión: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
>>>>>>> Stashed changes
            }
        }
    }