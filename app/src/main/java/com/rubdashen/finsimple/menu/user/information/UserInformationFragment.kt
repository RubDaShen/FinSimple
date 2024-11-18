package     com.rubdashen.finsimple.menu.user.information

import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import      android.widget.ImageButton
import      android.widget.TextView
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.user.subject.UserFragment
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings
import      retrofit2.Call
import      retrofit2.Callback
import      retrofit2.Response



public final class UserInformationFragment : Fragment(R.layout.fragment_user_information)
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
        return inflater.inflate(R.layout.fragment_user_information, container, false)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit {
        super.onViewCreated(view, savedInstanceState)

        this.backButton()
        this.userInformation()
    }

    private fun backButton(): Unit {
        val backButton: ImageButton = view?.findViewById(R.id.user_information_back_button)!!
        backButton.setOnClickListener {
            this.replaceFragment(UserFragment())
        }
    }
    private fun userInformation(): Unit {
        val companyText: TextView = view?.findViewById(R.id.user_information_company_name)!!
        val emailText: TextView = view?.findViewById(R.id.user_information_email)!!
        val rucText: TextView = view?.findViewById(R.id.user_information_ruc)!!

        if (
            UserWrapperSettings.company.isNotEmpty() &&
            UserWrapperSettings.email.isNotEmpty() &&
            UserWrapperSettings.ruc.isNotEmpty()
            ) {
            companyText.text    = UserWrapperSettings.company
            emailText.text      = UserWrapperSettings.email
            rucText.text        = UserWrapperSettings.ruc

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
                        emailText.text   = it.email
                        rucText.text     = it.ruc
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