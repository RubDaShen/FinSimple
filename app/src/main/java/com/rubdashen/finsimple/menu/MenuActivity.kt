package     com.rubdashen.finsimple.menu

import      android.os.Bundle
import      androidx.appcompat.app.AppCompatActivity
import      androidx.core.view.ViewCompat
import      androidx.core.view.WindowInsetsCompat
import      androidx.fragment.app.Fragment
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.google.android.material.bottomnavigation.BottomNavigationView
import      com.rubdashen.finsimple.menu.exit.ExitFragment
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.wallet.subject.WalletFragment
import      com.rubdashen.finsimple.menu.user.UserFragment
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings
import      retrofit2.Call
import      retrofit2.Callback
import      retrofit2.Response



public final class MenuActivity : AppCompatActivity()
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//				    Members and Fields
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //					Variables
    //	-------------------------------------------



//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    protected override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.listenToNavigation()
        this.replaceFragment(WalletFragment())
        this.configure()
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    private fun listenToNavigation(): Unit {
        val navigation: BottomNavigationView = findViewById(R.id.navigation_menu)
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_wallet -> {
                    this.replaceFragment(WalletFragment())
                    true
                }
                R.id.navigation_user -> {
                    this.replaceFragment(UserFragment())
                    true
                }
                R.id.navigation_exit -> {
                    this.replaceFragment(ExitFragment())
                    true
                }
                else -> false
            }
        }
    }
    private fun replaceFragment(fragment: Fragment): Unit {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }

    private fun configure(): Unit {
        val call: Call<UserInformationResponse> = ApiWorker.userInformation()

        call.enqueue(object: Callback<UserInformationResponse> {
            public override fun onResponse(
                call: Call<UserInformationResponse>,
                response: Response<UserInformationResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val userInformationResponse: UserInformationResponse? = response.body()
                    userInformationResponse?.let {
                        UserWrapperSettings.company = it.company
                    }
                }
            }

            public override fun onFailure(
                call: Call<UserInformationResponse>,
                t: Throwable
            ): Unit { }
        })
    }
}