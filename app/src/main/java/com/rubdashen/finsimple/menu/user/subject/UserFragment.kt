package     com.rubdashen.finsimple.menu.user.subject

import      android.content.Intent
import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import      android.widget.ImageButton
import android.widget.TextView
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.google.android.material.bottomnavigation.BottomNavigationView
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.lobby.MainActivity
import      com.rubdashen.finsimple.menu.user.edit.EditUserInformationFragment
import      com.rubdashen.finsimple.menu.user.information.UserInformationFragment
import com.rubdashen.finsimple.shared.api.ApiWorker
import com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import com.rubdashen.finsimple.shared.user.UserWrapperSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


public final class UserFragment : Fragment(R.layout.fragment_user)
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
    }

    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit {
        super.onViewCreated(view, savedInstanceState)

        this.backButton()
        this.userInformationButton()
        this.userEditInformationButton()
        this.userLogoutButton()

        this.userCompanyName()
    }

    private fun backButton(): Unit {
        val backButton: ImageButton = view?.findViewById(R.id.user_profile_back_button)!!
        backButton.setOnClickListener {
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.navigation_menu)
            bottomNavigationView.selectedItemId = R.id.navigation_wallet
        }
    }
    private fun userInformationButton(): Unit {
        val userInformationButton: View = view?.findViewById(R.id.view_go_to_user_information)!!
        userInformationButton.setOnClickListener {
            this.replaceFragment(UserInformationFragment())
        }
    }
    private fun userEditInformationButton(): Unit {
        val userSettingsButton: View = view?.findViewById(R.id.view_go_to_edit_user)!!
        userSettingsButton.setOnClickListener {
            this.replaceFragment(EditUserInformationFragment())
        }
    }
    private fun userLogoutButton(): Unit {
        val userLogoutButton: View = view?.findViewById(R.id.view_go_to_logout)!!
        userLogoutButton.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun userCompanyName(): Unit {
        val companyText: TextView = view?.findViewById(R.id.user_profile_company_text)!!
        if (UserWrapperSettings.company.isNotEmpty()) {
            companyText.text = UserWrapperSettings.company
            return
        }

        val call: Call<UserInformationResponse> = ApiWorker.userInformation()
        call.enqueue(object: Callback<UserInformationResponse> {
            public override fun onResponse(
                call: Call<UserInformationResponse>,
                response: Response<UserInformationResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val userInformationResponse: UserInformationResponse? = response.body()
                    userInformationResponse?.let {
                        companyText.text = it.company
                    }
                }
            }

            public override fun onFailure(
                call: Call<UserInformationResponse>,
                t: Throwable
            ): Unit { }
        })
    }

    private fun replaceFragment(fragment: Fragment): Unit {
        val fragmentManager: FragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }
}