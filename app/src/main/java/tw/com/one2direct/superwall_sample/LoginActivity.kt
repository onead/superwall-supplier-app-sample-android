package tw.com.one2direct.superwall_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val editTextAccount=findViewById<android.widget.EditText>(R.id.editTextAccount)

        val editTextPassword=findViewById<android.widget.EditText>(R.id.editTextPassword)
        val buttonLogin=findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener{
            val account=editTextAccount.text.toString()
            val password=editTextPassword.text.toString()
            if(account.isEmpty() ||account.length!=10){
                editTextAccount.error="Account is error"
                return@setOnClickListener
            }
            if(password.isEmpty() || password!="1234"){
                editTextPassword.error="Password is error"
                return@setOnClickListener
            }
            Firebase.messaging.token
            val sp =this.getSharedPreferences("Login", MODE_PRIVATE)
            sp.edit().putString("memberID",account).apply()
            sp.edit().putInt("birthYear",1990).apply()
            sp.edit().putString("gender","M").apply()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}