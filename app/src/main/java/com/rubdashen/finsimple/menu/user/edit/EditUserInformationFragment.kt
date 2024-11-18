package     com.rubdashen.finsimple.menu.user.edit

import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import      android.widget.ImageButton
import android.widget.TextView
import      android.widget.Toast
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.user.subject.UserFragment
import com.rubdashen.finsimple.shared.api.ApiWorker
import com.rubdashen.finsimple.shared.api.user.request.UserUpdateRequest
import com.rubdashen.finsimple.shared.api.user.response.UserInformationResponse
import com.rubdashen.finsimple.shared.api.user.response.UserUpdateResponse
import com.rubdashen.finsimple.shared.tools.Functions
import com.rubdashen.finsimple.shared.user.UserConstraints
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
        val company: String = view?.findViewById<TextView>(R.id.edit_text_user_company)!!.text.toString()
        val email: String   = view?.findViewById<TextView>(R.id.edit_text_user_email)!!.text.toString()
        val ruc: String     = view?.findViewById<TextView>(R.id.edit_text_user_ruc)!!.text.toString()

        //  > Inputs
        if ((email.isEmpty() || company.isEmpty() || ruc.isEmpty())) {
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

        return true
    }
    private fun saveChanges(): Unit {
        val company: String = view?.findViewById<EditText>(R.id.edit_text_user_company)!!.text.toString()
        val email: String   = view?.findViewById<EditText>(R.id.edit_text_user_email)!!.text.toString()
        val ruc: String     = view?.findViewById<EditText>(R.id.edit_text_user_ruc)!!.text.toString()
        val saveButton: Button      = view?.findViewById(R.id.button_accept_edit_user)!!
        val originalText: String    = saveButton.text.toString()

        saveButton.text = "Actualizando..."
        val call: Call<UserUpdateResponse> = ApiWorker.updateUserInformation(
            UserUpdateRequest(UserWrapperSettings.userId, company, email, ruc)
        )
        call.enqueue(object: Callback<UserUpdateResponse> {
            public override fun onResponse(
                call: Call<UserUpdateResponse>,
                response: Response<UserUpdateResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val userInformationResponse: UserUpdateResponse? = response.body()
                    userInformationResponse?.let {
                        if (it.success) {
                            UserWrapperSettings.email   = email
                            UserWrapperSettings.company = company
                            UserWrapperSettings.ruc     = ruc

                            this@EditUserInformationFragment.replaceFragment(UserFragment())
                            this@EditUserInformationFragment.makeToast("Información actualizada exitosamente")
                        }
                    }
                }

                saveButton.text = originalText
            }

            public override fun onFailure(
                call: Call<UserUpdateResponse>,
                t: Throwable
            ): Unit {
                saveButton.text = originalText
                this@EditUserInformationFragment.makeToast("Error al actualizar la información")
            }
        })
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