package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarInstrucciones : AppCompatActivity() {
    private lateinit var receta: Receta
    private lateinit var etInstrucciones: EditText
    private lateinit var btnContinuar: Button
    private lateinit var tvTitulo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_instrucciones)

        // recuperar la receta (que puede ser nueva o para editar)
        receta = intent.getSerializableExtra("receta") as? Receta ?: Receta() // se asegura de tener un objeto Receta

        etInstrucciones = findViewById(R.id.etInstrucciones)
        btnContinuar = findViewById(R.id.btnContinuar)
        tvTitulo = findViewById(R.id.tvTitulo)

        // cambia titulo si estamos en modo edicion
        if (receta.id.isNotEmpty()) {
            tvTitulo.text = "Editar Receta"
        } else {
            tvTitulo.text = "Nueva Receta"
        }


        // precargar datos si estamos editando
        if (receta.id.isNotEmpty()) { // si la receta ya tiene un ID, estamos editando
            etInstrucciones.setText(receta.instrucciones)
        }

        btnContinuar.setOnClickListener {
            val instrucciones = etInstrucciones.text.toString().trim()

            if (instrucciones.isEmpty()) {
                etInstrucciones.error = "Por favor escribe las instrucciones"
                etInstrucciones.requestFocus()
                return@setOnClickListener
            }

            receta.instrucciones = instrucciones // actualizar instrucciones en el objeto

            val intent = Intent(this, AgregarImagenFuente::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
    }
}