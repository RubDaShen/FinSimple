package     com.rubdashen.finsimple.lobby

import      android.content.Intent
import      android.os.Bundle
import      android.widget.Button
import      android.widget.EditText
import      android.widget.ImageButton
import      android.widget.TextView
import      android.widget.Toast
import      androidx.appcompat.app.AppCompatActivity
import      androidx.core.view.ViewCompat
import      androidx.core.view.WindowInsetsCompat
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.MenuActivity
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.user.request.UserLoginRequest
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings
import      okhttp3.Call
import      okhttp3.Callback
import      okhttp3.OkHttpClient
import      okhttp3.Request
import      okhttp3.Response
import      okio.IOException
import      org.json.JSONObject



public final class LoginActivity : AppCompatActivity()
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    protected override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.loginButton()
        this.changeToRegister()
        this.changeToMain()
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    private fun loginButton(): Unit {
        val loginButton: Button = findViewById(R.id.login_now_button)
        loginButton.setOnClickListener {
            val email: String       = findViewById<EditText>(R.id.email_input_text).text.toString()
            val password: String    = findViewById<EditText>(R.id.password_input_text).text.toString()

            if (!this.checkLoginInput()) return@setOnClickListener

            this.login(email, password)
        }
    }
    private fun login(email: String, password: String): Unit {
        val loginButton: Button     = findViewById(R.id.login_now_button)
        val client: OkHttpClient    = ApiWorker.client()
        val originalText: String    = loginButton.text.toString()
        loginButton.text            = "Iniciando..."

        var request: Request = ApiWorker.loginUser(UserLoginRequest(password, email))
        client.newCall(request).enqueue(object: Callback {
            public override fun onFailure(call: Call, e: IOException): Unit {
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión", Toast.LENGTH_SHORT
                    ).show()
                }
                loginButton.text = originalText
            }

            public override fun onResponse(call: Call, response: Response): Unit {
                response.body?.let {
                    val responseBody: String = it.string()
                    runOnUiThread {
                        try {
                            val json = JSONObject(responseBody)
                            UserWrapperSettings.userId  = json.getInt("userId")
                            UserWrapperSettings.token   = json.getString("token")

                            val intent: Intent = Intent(this@LoginActivity, MenuActivity::class.java)
                            this@LoginActivity.startActivity(intent)
                            this@LoginActivity.finish()

                            Toast.makeText(
                                this@LoginActivity,
                                "Inicio de sesión exitoso", Toast.LENGTH_SHORT
                            ).show()
                        }
                        catch (e: Exception) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Correo o contraseña incorrectos", Toast.LENGTH_SHORT
                            ).show()
                        }

                        loginButton.text = originalText
                    }
                }
            }
        })
    }
    private fun checkLoginInput(): Boolean {
        val email: String       = findViewById<EditText>(R.id.email_input_text).text.toString()
        val password: String    = findViewById<EditText>(R.id.password_input_text).text.toString()

        //  > Inputs
        if (email.isEmpty() || password.isEmpty()) {
            this.makeToast("Por favor, rellene todos los campos")

            return false
        }

        return true
    }

    private fun changeToRegister(): Unit {
        val registerNowText: TextView = findViewById(R.id.go_to_register_text)
        registerNowText.setOnClickListener {
            val intent: Intent = Intent(this, RegisterActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }
    private fun changeToMain(): Unit {
        val registerNowText: ImageButton = findViewById(R.id.back_button_login)
        registerNowText.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }

    private fun makeToast(message: String): Unit {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}