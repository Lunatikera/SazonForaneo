package flores.pablo.sazonforaneo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.ui.recetas.MisRecetasFragment

class PerfilConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_config)

        val editarInfo: TextView = findViewById(R.id.infoPersonal)
        editarInfo.setOnClickListener{

            val intento = Intent(this, editar_informacion_perfil_activity::class.java)
            startActivity(intento)

        }

        val misRecetas: TextView = findViewById(R.id.misRecetas)
        misRecetas.setOnClickListener{

            val intent = Intent(this, ExplorarActivity::class.java)
            intent.putExtra("fragment_to_show", "mis_recetas")
            startActivity(intent)

        }

        val recetasGuardadas: TextView = findViewById(R.id.recetasGuardadas)
        recetasGuardadas.setOnClickListener{

            val intent = Intent(this, ExplorarActivity::class.java)
            intent.putExtra("fragment_to_show", "mis_recetas")
            startActivity(intent)

        }

    }
}