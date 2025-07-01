package flores.pablo.sazonforaneo
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class RegistroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmar = findViewById<EditText>(R.id.etConfirmar)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val tvIniciarSesion = findViewById<TextView>(R.id.tvIniciarSesion)

        // Opciones del spinner
        val opcionesGenero = arrayOf("Selecciona", "Hombre", "Mujer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGenero)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        btnContinuar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val genero = spinnerGenero.selectedItem.toString()
            val pass = etPassword.text.toString().trim()
            val confirmar = etConfirmar.text.toString().trim()

            if (nombre.isEmpty()) {
                etNombre.error = "Campo obligatorio"
                return@setOnClickListener
            }

            if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Correo inválido"
                return@setOnClickListener
            }

            if (!fecha.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) {
                etFecha.error = "Formato debe ser dd/mm/aaaa"
                return@setOnClickListener
            }

            if (genero == "Selecciona") {
                Toast.makeText(this, "Selecciona un género", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 6) {
                etPassword.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            if (pass != confirmar) {
                etConfirmar.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Tu cuenta ha sido registrada", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this, "Tu cuenta ha sido registrada", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                }

            tvIniciarSesion.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
