package flores.pablo.sazonforaneo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.util.Calendar

class RegistroActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModels()

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etFecha: EditText
    private lateinit var spinnerGenero: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmar: EditText
    private lateinit var btnContinuar: Button
    private lateinit var tvIniciarSesion: TextView

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etFecha = findViewById(R.id.etFecha)
        spinnerGenero = findViewById(R.id.spinnerGenero)
        etPassword = findViewById(R.id.etPassword)
        etConfirmar = findViewById(R.id.etConfirmar)
        btnContinuar = findViewById(R.id.btnContinuar)
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion)

        val opcionesGenero = arrayOf("Selecciona un Genero", "Hombre", "Mujer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGenero)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        etFecha.setOnClickListener { showDatePickerDialog() }

        btnContinuar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val genero = spinnerGenero.selectedItem.toString()
            val pass = etPassword.text.toString().trim()
            val confirmar = etConfirmar.text.toString().trim()

            viewModel.registrarUsuario(
                nombre, correo, fecha, genero, pass
            )
        }

        tvIniciarSesion.setOnClickListener {
            // Por ejemplo volver al login o MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Observadores LiveData para mostrar mensajes o navegar
        viewModel.error.observe(this, Observer { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.registroExitoso.observe(this, Observer { exito ->
            if (exito) {
                Toast.makeText(this, "Tu cuenta ha sido registrada", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                etFecha.setText(dateFormat.format(calendar.time))
            },
            year, month, day
        )
        // Aseguramos que el usuario tenga almenos 12 años de edad.
        // Calculamos la fecha de hace 12 años a partir de hoy.
        val minAgeCalendar = Calendar.getInstance()
        minAgeCalendar.add(Calendar.YEAR, -12) // Restamos 12 años al año actual

        datePickerDialog.datePicker.maxDate = minAgeCalendar.timeInMillis

        //Ponemos de limite 100 años
         datePickerDialog.datePicker.minDate = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 365 * 100L)

        datePickerDialog.show()
    }
}
