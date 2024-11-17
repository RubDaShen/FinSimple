package     com.rubdashen.finsimple.menu.wallet.subject

import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import      android.widget.AdapterView
import      android.widget.AdapterView.OnItemClickListener
import      android.widget.Button
import      android.widget.TextView
import      android.widget.Toast
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      androidx.recyclerview.widget.LinearLayoutManager
import      androidx.recyclerview.widget.RecyclerView
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.wallet.edition.CreateUpdateBillFragment
import      com.rubdashen.finsimple.menu.wallet.bills.models.BillView
import      com.rubdashen.finsimple.menu.wallet.bills.adapter.BillsViewAdapter
import com.rubdashen.finsimple.menu.wallet.edition.types.EditTypeAction
import      com.rubdashen.finsimple.menu.wallet.information.BillInformationFragment
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.bill.response.BillViewInformationResponse
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings
import      retrofit2.Call
import      retrofit2.Callback
import      retrofit2.Response



public final class WalletFragment : Fragment(R.layout.fragment_wallet), OnItemClickListener
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//				    Members and Fields
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //					Variables
    //	-------------------------------------------
    private val m_Bills: MutableList<BillView> = mutableListOf()
    private lateinit var m_BillsViewAdapter: BillsViewAdapter



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
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    public override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ): Unit {
        super.onViewCreated(view, savedInstanceState)

        this.configure()
        this.billsView()
        this.changeToBillCreation()
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    private fun configure(): Unit {
        //  # Elements
        //      @BillsViewAdapter
        this.m_BillsViewAdapter = BillsViewAdapter(this.m_Bills, this)

        // # Final Touches
        //      @Title
        val companyText: TextView = view?.findViewById(R.id.home_company_text)!!
        companyText.text = UserWrapperSettings.company

        this.loadBills()
    }

    private fun billsView(): Unit {
        val billsRecyclerView = view?.findViewById<RecyclerView>(R.id.home_recycler_view)!!
        billsRecyclerView.adapter       = this.m_BillsViewAdapter
        billsRecyclerView.layoutManager = LinearLayoutManager(context)
    }
    private fun loadBills(): Unit {
        this.m_Bills.clear()
        val call: Call<List<BillViewInformationResponse>> = ApiWorker.billsViewInformation()
        val noBillsTextView: TextView = view?.findViewById(R.id.no_bills_text)!!
        val originalText: String = noBillsTextView.text.toString()
        noBillsTextView.text = "Cargando letras..."

        call.enqueue(object: Callback<List<BillViewInformationResponse>> {
            public override fun onResponse(
                call: Call<List<BillViewInformationResponse>>,
                response: Response<List<BillViewInformationResponse>>
            ): Unit {
                if (response.isSuccessful) {
                    val userInformationResponse: List<BillViewInformationResponse>? = response.body()

                    userInformationResponse?.let {
                        try {
                            val companyText: TextView = view?.findViewById(R.id.home_company_text)!!
                            companyText.text = UserWrapperSettings.company

                            if (it.isNotEmpty()) {
                                noBillsTextView.visibility = TextView.INVISIBLE

                                for (bill in it) {
                                    this@WalletFragment.m_Bills.add(bill.toBillView())
                                }

                                this@WalletFragment.billsView()
                            }
                            else {
                                noBillsTextView.visibility  = TextView.VISIBLE
                                noBillsTextView.text        = originalText
                            }

                            this@WalletFragment.makeToast("Letras cargadas")
                        }
                        catch (_: Exception) { }
                    }
                }
            }

            public override fun onFailure(
                call: Call<List<BillViewInformationResponse>>,
                t: Throwable
            ): Unit {
                try {
                    val companyText: TextView   = view?.findViewById(R.id.home_company_text)!!
                    companyText.text            = UserWrapperSettings.company
                    noBillsTextView.text        = originalText
                }
                catch (_: Exception) { }

                this@WalletFragment.makeToast("No se pudo cargar las letras")
            }
        })
    }
    private fun changeToBillCreation(): Unit {
        val createBillButton: Button = view?.findViewById(R.id.add_bill_button)!!
        createBillButton.setOnClickListener {
            this.replaceFragment(CreateUpdateBillFragment())
        }
    }

    public override fun onItemClick(
        parent: AdapterView<*>?,
        view: View?, position: Int, id: Long
    ): Unit {
        val bill: BillView = this.m_Bills[position]
        val fragment: Fragment = BillInformationFragment(bill)

        this.replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment): Unit {
        val fragmentManager: FragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }
    private fun makeToast(message: String): Unit {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}