package flores.pablo.sazonforaneo.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import flores.pablo.sazonforaneo.R
import java.text.SimpleDateFormat
import java.util.*

class editar_informacion_perfil_activity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var spinnerGenero: Spinner
    private lateinit var btnGuardar: Button

    private val calendar = Calendar.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_informacion_perfil)

        // Inicializar views
        etNombre = findViewById(R.id.nombreUsuario)
        etFechaNacimiento = findViewById(R.id.fechaNacimiento)
        spinnerGenero = findViewById(R.id.spinnerGenero)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Opciones para el spinner
        val opcionesGenero = arrayOf("Selecciona", "Hombre", "Mujer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGenero)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { documento ->
                    if (documento.exists()) {
                        etNombre.setText(documento.getString("nombre") ?: "")
                        etFechaNacimiento.setText(documento.getString("fechaNacimiento") ?: "")
                        val genero = documento.getString("genero") ?: ""
                        val index = opcionesGenero.indexOf(genero)
                        if (index >= 0) {
                            spinnerGenero.setSelection(index)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar perfil: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            btnGuardar.setOnClickListener {
                val nombre = etNombre.text.toString().trim()
                val fecha = etFechaNacimiento.text.toString().trim()
                val generoSeleccionado = spinnerGenero.selectedItem.toString()

                if (nombre.isEmpty()) {
                    etNombre.error = "Campo obligatorio"
                    return@setOnClickListener
                }

                if (generoSeleccionado == "Selecciona") {
                    Toast.makeText(this, "Selecciona un género válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (fecha.isEmpty()) {
                    etFechaNacimiento.error = "Selecciona tu fecha de nacimiento"
                    return@setOnClickListener
                }

                val nuevosDatos = mapOf(
                    "nombre" to nombre,
                    "fechaNacimiento" to fecha,
                    "genero" to generoSeleccionado
                )

                db.collection("usuarios").document(uid).update(nuevosDatos)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // DatePicker
        etFechaNacimiento.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etFechaNacimiento.setText(dateFormat.format(calendar.time))
                },
                year, month, day
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }
}
