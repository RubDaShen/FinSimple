package     com.rubdashen.finsimple.lobby

import      android.content.Intent
import      android.os.Bundle
import      android.widget.Button
import      android.widget.EditText
import      android.widget.ImageButton
import      android.widget.Toast
import      androidx.appcompat.app.AppCompatActivity
import      androidx.core.view.ViewCompat
import      androidx.core.view.WindowInsetsCompat
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.user.request.UserRegisterRequest
import      com.rubdashen.finsimple.shared.tools.Functions
import      com.rubdashen.finsimple.shared.user.UserConstraints
import      okhttp3.Call
import      okhttp3.Callback
import      okhttp3.OkHttpClient
import      okhttp3.Request
import      okhttp3.Response
import      okio.IOException



public final class RegisterActivity : AppCompatActivity()
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    protected override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.registerButton()
        this.changeToMain()
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    private fun registerButton(): Unit {
        val registerButton: Button = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            val company: String     = findViewById<EditText>(R.id.company_name_input).text.toString()
            val email: String       = findViewById<EditText>(R.id.email_input_register).text.toString()
            val ruc: String         = findViewById<EditText>(R.id.input_ruc_register).text.toString()
            val password: String    = findViewById<EditText>(R.id.password_register_input).text.toString()

            if (!this.checkRegisterInputs()) return@setOnClickListener

            this.register(company, email, ruc, password)
        }
    }
    private fun register(company: String, email: String, ruc: String, password: String): Unit {
        val registerButton: Button  = findViewById(R.id.register_button)
        val request: Request        = ApiWorker.registerUser(UserRegisterRequest(company, email, ruc, password))
        val client: OkHttpClient    = ApiWorker.client()

        val originalText: String    = registerButton.text.toString()
        registerButton.text         = "Registrando..."

        client.newCall(request).enqueue(object: Callback {
            public override fun onFailure(call: Call, e: IOException): Unit {
                runOnUiThread {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error de conexión", Toast.LENGTH_SHORT
                    ).show()
                }
                registerButton.text = originalText
            }

            public override fun onResponse(call: Call, response: Response): Unit {
                response.body?.let {
                    runOnUiThread {
                        val intent: Intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        this@RegisterActivity.startActivity(intent)
                        this@RegisterActivity.finish()

                        Toast.makeText(
                            this@RegisterActivity,
                            "Usuario registrado", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                registerButton.text = originalText
            }
        })
    }
    private fun checkRegisterInputs(): Boolean {
        val company: String     = findViewById<EditText>(R.id.company_name_input).text.toString()
        val email: String       = findViewById<EditText>(R.id.email_input_register).text.toString()
        val ruc: String         = findViewById<EditText>(R.id.input_ruc_register).text.toString()
        val password: String    = findViewById<EditText>(R.id.password_register_input).text.toString()

        //  > Inputs
        if ((email.isEmpty() || password.isEmpty() || company.isEmpty() || ruc.isEmpty())) {
            this.makeToast("Por favor, complete todos los campos")
            return false
        }
        //  > Company
        if (
            (company.length < UserConstraints.minUserCompanyCharacter) ||
            (company.length > UserConstraints.maxUserCompanyCharacter)
            ) {
            this.makeToast("El nombre de la empresa debe tener entre ${UserConstraints.minUserCompanyCharacter} y ${UserConstraints.maxUserCompanyCharacter} caracteres")
            return false
        }
        //  > Email
        if (!Functions.isValidEmail(email)) {
            this.makeToast("Correo electrónico inválido")
            return false
        }
        //  > RUC
        if (ruc.length != UserConstraints.userRucCharacter) {
            this.makeToast("El RUC debe tener ${UserConstraints.userRucCharacter} caracteres")
            return false
        }
        //  > Password
        if (password.length < UserConstraints.minUserPasswordCharacter) {
            this.makeToast("La contraseña debe tener al menos ${UserConstraints.minUserPasswordCharacter} caracteres")
            return false
        }

        return true
    }

    private fun changeToMain(): Unit {
        val registerNowText: ImageButton = findViewById(R.id.back_button_register)
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