package     com.rubdashen.finsimple.lobby

import      android.content.Intent
import      android.os.Bundle
import      android.widget.Button
import      androidx.appcompat.app.AppCompatActivity
import      androidx.core.view.ViewCompat
import      androidx.core.view.WindowInsetsCompat
import      com.rubdashen.finsimple.R



public final class MainActivity : AppCompatActivity()
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    protected override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.changeToLogin()
        this.changeToRegister()
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    private fun changeToLogin(): Unit {
        val loginButton: Button = findViewById(R.id.main_loginButton)
        loginButton.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }
    private fun changeToRegister(): Unit {
        val registerButton: Button = findViewById(R.id.main_registerButton)
        registerButton.setOnClickListener {
            val intent: Intent = Intent(this, RegisterActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }
}