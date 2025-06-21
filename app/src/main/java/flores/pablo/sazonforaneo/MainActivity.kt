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



class MainActivity : AppCompatActivity() {
    lateinit var etCorreo: EditText
    lateinit var etPassword: EditText
    lateinit var btnEntrar: Button
    lateinit var btnRegistrarme: Button
    val tvOlvidaste: TextView = findViewById(R.id.tvOlvidaste)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnRegistrarme = findViewById(R.id.btnRegistrarme)

        btnEntrar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (correo.isEmpty()) {
                etCorreo.error = "El correo no puede estar vacío"
            } else if (password.isEmpty()) {
                etPassword.error = "La contraseña no puede estar vacía"
            } else {
                //Meter la base de dato aqui si dio quiere
                val intent = Intent(this, InicioActivity::class.java)
                startActivity(intent)
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
