package     com.rubdashen.finsimple.menu.wallet.information

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.rubdashen.finsimple.R
import com.rubdashen.finsimple.menu.wallet.bills.models.BillInformation
import com.rubdashen.finsimple.menu.wallet.bills.models.BillView
import com.rubdashen.finsimple.menu.wallet.creation.CreateBillFragment
import com.rubdashen.finsimple.menu.wallet.subject.WalletFragment
import com.rubdashen.finsimple.shared.api.ApiWorker
import com.rubdashen.finsimple.shared.api.bill.response.BillInformationResponse
import com.rubdashen.finsimple.shared.bill.BillConstraints
import com.rubdashen.finsimple.shared.bill.types.BankType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.pow


public final class BillInformationFragment : Fragment
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//				    Members and Fields
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //					Variables
    //	-------------------------------------------
    private val d_BillView: BillView



//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //	        Constructors and ~Destructor~
    //	-------------------------------------------
    public constructor(
        billView: BillView
    ): super(R.layout.fragment_create_bill) {
        this.d_BillView = billView
    }

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
        return inflater.inflate(R.layout.fragment_bill_information, container, false)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ): Unit {
        super.onViewCreated(view, savedInstanceState)

        this.editBillInformation()
        this.eliminateBill()
        this.billInformation()
        this.backToBills()
    }

    private fun backToBills(): Unit {
        val backButton: ImageButton = view?.findViewById(R.id.bill_information_back_button)!!
        backButton.setOnClickListener {
            this.replaceFragment(WalletFragment())
        }
    }
    private fun editBillInformation(): Unit {

    }
    private fun eliminateBill(): Unit {

    }

    private fun billInformation(): Unit {
        val call: Call<BillInformationResponse> = ApiWorker.billInformation(this.d_BillView.id)

        call.enqueue(object: Callback<BillInformationResponse> {
            public override fun onResponse(
                call: Call<BillInformationResponse>,
                response: Response<BillInformationResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val billInformationResponse: BillInformationResponse? = response.body()

                    billInformationResponse?.let {
                        try {
                            val result: BillInformation = this@BillInformationFragment.calculateBill(billInformationResponse)
                            this@BillInformationFragment.setBillInformation(result)
                        }
                        catch (_: Exception) { }
                    }
                }
            }

            public override fun onFailure(
                call: Call<BillInformationResponse>,
                t: Throwable
            ): Unit { }
        })
    }
    private fun calculateBill(response: BillInformationResponse): BillInformation {
        val formatter: SimpleDateFormat     = SimpleDateFormat(BillConstraints.timeFormat)

        val discountedDateString: String = response.billDiscountedDate
        val dueDateString: String = response.billDueDate
        val nominalValue: Double = response.nominalValue
        val desgravamen: Double = response.degravamen / 100.0
        val retentionPercentage: Double = response.assignedRetention / 100.0
        val tea: Double = response.tea / 100.0
        val discountedDate: Date = formatter.parse(discountedDateString)
        val dueDate: Date = formatter.parse(dueDateString)
        val diffInMillis: Long = kotlin.math.abs(discountedDate.time - dueDate.time)
        val discountedDays: Long = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        val tep: Double = (1 + tea).pow(discountedDays.toDouble() / 360) - 1
        val discountedRate: Double = tep / (1 + tep)
        val discount: Double = nominalValue * discountedRate
        val netWorth: Double = nominalValue - discount
        val initialCosts: Double = nominalValue * desgravamen
        val retention: Double = retentionPercentage * nominalValue
        val receivedValue: Double = netWorth - retention - initialCosts
        val deliveredValue: Double = nominalValue - retention
        val tcea: Double = (deliveredValue / receivedValue).pow(360.0 / discountedDays) - 1

        return BillInformation(
            response.title,
            response.description,
            response.billCreatedDate,
            response.billDueDate,
            response.billDiscountedDate,
            response.nominalValue,
            response.bankTypeId,
            response.tea,
            response.degravamen,
            response.assignedRetention,
            tep,
            discountedDays.toInt(),
            discountedRate,
            netWorth,
            receivedValue,
            deliveredValue,
            tcea
        )
    }
    private fun setBillInformation(information: BillInformation): Unit {
        val titleView: TextView = view?.findViewById(R.id.tvbi_title)!!
        val descriptionView: TextView = view?.findViewById(R.id.tvbi_description)!!
        val createdDateView: TextView = view?.findViewById(R.id.tvbi_created_date)!!
        val dueDateView: TextView = view?.findViewById(R.id.tvbi_due_date)!!
        val discountedDateView: TextView = view?.findViewById(R.id.tvbi_discounted_date)!!
        val nominalValueView: TextView = view?.findViewById(R.id.tvbi_nominal_value)!!
        val bankTypeView: TextView = view?.findViewById(R.id.tvbi_bank_name)!!
        val teaView: TextView = view?.findViewById(R.id.tvbi_tea)!!
        val desgravamenView: TextView = view?.findViewById(R.id.tvbi_desgravamen)!!
        val assignedRetentionView: TextView = view?.findViewById(R.id.tvbi_retention)!!
        val tepView: TextView = view?.findViewById(R.id.tvbi_tep)!!
        val periodView: TextView = view?.findViewById(R.id.tvbi_period)!!
        val discountedRateView: TextView = view?.findViewById(R.id.tvbi_discounted_rate)!!
        val netWorthView: TextView = view?.findViewById(R.id.tvbi_net_worth)!!
        val deliveredValueView: TextView = view?.findViewById(R.id.tvbi_delivered_value)!!
        val receivedValueView: TextView = view?.findViewById(R.id.tvbi_received_value)!!
        val tceaView: TextView = view?.findViewById(R.id.tvbi_tcea)!!

        val df7: DecimalFormat  = DecimalFormat("#.#######")
        val df2: DecimalFormat  = DecimalFormat("#.##")

        titleView.text = information.title
        descriptionView.text = information.description
        createdDateView.text = information.billCreatedDate
        dueDateView.text = information.billDueDate
        discountedDateView.text = information.billDiscountedDate
        nominalValueView.text = information.nominalValue.toString()
        bankTypeView.text = BankType.toString(BankType.entries[information.bankTypeId])
        teaView.text = df7.format(information.tea * 100)
        desgravamenView.text = df7.format(information.degravamen * 100) + "%"
        assignedRetentionView.text = df7.format(information.assignedRetention * 100)
        tepView.text =  df7.format(information.tep * 100) + "%"
        periodView.text = information.period.toString() + " días"
        discountedRateView.text = df7.format(information.discountedRate * 100) + "%"
        netWorthView.text = "S/" + df2.format(information.netWorth)
        deliveredValueView.text = "S/" + df2.format(information.deliveredValue)
        receivedValueView.text = "S/" + df2.format(information.receivedValue)
        tceaView.text = df7.format(information.tcea * 100) + "%"
    }

    private fun replaceFragment(fragment: Fragment): Unit {
        val fragmentManager: FragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }
}