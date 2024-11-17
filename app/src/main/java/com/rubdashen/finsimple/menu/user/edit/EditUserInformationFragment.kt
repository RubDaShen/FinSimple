package     com.rubdashen.finsimple.menu.user.edit

import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import      android.widget.ImageButton
import      android.widget.Toast
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.user.subject.UserFragment
import com.rubdashen.finsimple.shared.api.ApiWorker
import com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import com.rubdashen.finsimple.shared.user.UserWrapperSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


public final class EditUserInformationFragment : Fragment(R.layout.fragment_edit_user_information)
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
        return inflater.inflate(R.layout.fragment_edit_user_information, container, false)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit {
        super.onViewCreated(view, savedInstanceState)

        this.backButton()
        this.fillUserInformation()
        this.saveChangesButton()
    }

    private fun backButton(): Unit {
        //  @BackButton
        val backButton: ImageButton = view?.findViewById(R.id.edit_user_information_back_button)!!
        backButton.setOnClickListener {
            this.replaceFragment(UserFragment())
        }

        //  @CancelButton
        val cancelButton: Button = view?.findViewById(R.id.button_cancel_edit_user)!!
        cancelButton.setOnClickListener {
            this.replaceFragment(UserFragment())
        }
    }
    private fun fillUserInformation(): Unit {
        val companyText: EditText   = view?.findViewById(R.id.edit_text_user_company)!!
        val emailText: EditText     = view?.findViewById(R.id.edit_text_user_email)!!
        val rucText: EditText       = view?.findViewById(R.id.edit_text_user_ruc)!!
        if (
            UserWrapperSettings.company.isNotEmpty() &&
            UserWrapperSettings.email.isNotEmpty() &&
            UserWrapperSettings.ruc.isNotEmpty()
            ) {
            companyText.setText(UserWrapperSettings.company)
            emailText.setText(UserWrapperSettings.email)
            rucText.setText(UserWrapperSettings.ruc)

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
                        companyText.setText(it.company)
                        emailText.setText(it.email)
                        rucText.setText(it.ruc)
                    }
                }
            }

            public override fun onFailure(
                call: Call<UserInformationResponse>,
                t: Throwable
            ): Unit { }
        })
    }

    private fun saveChangesButton(): Unit {
        val saveButton: Button = view?.findViewById(R.id.button_accept_edit_user)!!
        saveButton.setOnClickListener {
            if (!this.checkUserInputs()) return@setOnClickListener
            this.saveChanges()
        }
    }
    private fun checkUserInputs(): Boolean {
        val companyText: EditText   = view?.findViewById(R.id.edit_text_user_company)!!
        val emailText: EditText     = view?.findViewById(R.id.edit_text_user_email)!!
        val rucText: EditText       = view?.findViewById(R.id.edit_text_user_ruc)!!

        if (companyText.text.isEmpty()) {
            this.makeToast("Company name is empty")
            return false
        }
        if (emailText.text.isEmpty()) {
            this.makeToast("Email is empty")
            return false
        }
        if (rucText.text.isEmpty()) {
            this.makeToast("RUC is empty")
            return false
        }

        return true
    }
    private fun saveChanges(): Unit {

    }

    private fun makeToast(message: String): Unit {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun replaceFragment(fragment: Fragment): Unit {
        val fragmentManager: FragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }
}