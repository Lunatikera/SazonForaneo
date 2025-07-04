package flores.pablo.sazonforaneo
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat // pa las fechas
import java.util.Calendar // pa las fechas


class RegistroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var etFecha: EditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        etFecha = findViewById(R.id.etFecha)
        val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmar = findViewById<EditText>(R.id.etConfirmar)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val tvIniciarSesion = findViewById<TextView>(R.id.tvIniciarSesion)

        val opcionesGenero = arrayOf("Selecciona", "Hombre", "Mujer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGenero)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        // listener para abrir el DatePicker
        etFecha.setOnClickListener {
            showDatePickerDialog()
        }

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

            if (fecha.isEmpty()) {
                etFecha.error = "Selecciona tu fecha de nacimiento"
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

            // Aquí se realiza el registro con Firebase
            auth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Tu cuenta ha sido registrada", Toast.LENGTH_SHORT)
                            .show()
                        // Redirigir a MainActivity SOLO si el registro es exitoso
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Mostrar error específico de Firebase
                        Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }

        // listener para el  "Iniciar Sesión"
        tvIniciarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // funcioon para mostrar el datePicker
    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val formattedDate = dateFormat.format(calendar.time)
                etFecha.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // esto hace que no permita fechas futuras
        datePickerDialog.show()
    }
}
