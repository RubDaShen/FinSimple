package     com.rubdashen.finsimple.menu.exit

import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import      android.widget.Button
import      android.widget.TextView
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings



public final class ExitFragment : Fragment(R.layout.fragment_exit)
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
        return inflater.inflate(R.layout.fragment_exit, container, false)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.exitApp()
        this.configure()
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    private fun exitApp(): Unit {
        val exitButton: Button = view?.findViewById(R.id.exit_app_button)!!
        exitButton.setOnClickListener {
            activity?.finish()
        }
    }
    private fun configure(): Unit {
        //  > Company text
        val companyText: TextView = view?.findViewById(R.id.exit_company_text)!!
        companyText.text = UserWrapperSettings.company
    }
}