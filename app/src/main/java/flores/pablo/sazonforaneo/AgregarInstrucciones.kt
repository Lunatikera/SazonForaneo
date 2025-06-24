package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarInstrucciones : AppCompatActivity() {
    private lateinit var receta: Receta
    private lateinit var etInstrucciones: EditText
    private lateinit var btnContinuar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_instrucciones)

        receta = intent.getSerializableExtra("receta") as Receta

        etInstrucciones = findViewById(R.id.etInstrucciones)
        btnContinuar = findViewById(R.id.btnContinuar)

        btnContinuar.setOnClickListener {
            val instrucciones = etInstrucciones.text.toString().trim()

            if (instrucciones.isEmpty()) {
                etInstrucciones.error = "Por favor escribe las instrucciones"
                etInstrucciones.requestFocus()
                return@setOnClickListener
            }

            receta.instrucciones = instrucciones

            val intent = Intent(this, AgregarImagenFuente::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
    }
}