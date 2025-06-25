package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import flores.pablo.sazonforaneo.ui.ExplorarActivity


class MainActivity : AppCompatActivity() {
        private lateinit var etCorreo: EditText
        private lateinit var etPassword: EditText
        private lateinit var btnEntrar: Button
        private lateinit var btnRegistrarme: Button
        private lateinit var tvOlvidaste: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            etCorreo = findViewById(R.id.etCorreo)
            etPassword = findViewById(R.id.etPassword)
            btnEntrar = findViewById(R.id.btnEntrar)
            btnRegistrarme = findViewById(R.id.btnRegistrarme)
            tvOlvidaste = findViewById(R.id.tvOlvidaste)

            btnEntrar.setOnClickListener {
                val correo = etCorreo.text.toString().trim()
                val password = etPassword.text.toString().trim()

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
            btnRegistrarme.setOnClickListener {
                val intent = Intent(this, RegistroActivity::class.java)
                startActivity(intent)
            }

            tvOlvidaste.setOnClickListener {
                val intent = Intent(this, RecuperarPasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }