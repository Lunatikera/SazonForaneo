package flores.pablo.sazonforaneo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import flores.pablo.sazonforaneo.R

class editar_informacion_perfil_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_informacion_perfil)

        val botonGuardar: Button = findViewById(R.id.btnGuardar)
        botonGuardar.setOnClickListener{

            val intento = Intent(this, PerfilConfigActivity::class.java)
            startActivity(intento)

        }

    }
}