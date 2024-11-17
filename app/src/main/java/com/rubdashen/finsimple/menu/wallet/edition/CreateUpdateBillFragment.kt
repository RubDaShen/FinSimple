package     com.rubdashen.finsimple.menu.wallet.edition

import      android.app.DatePickerDialog
import      android.icu.util.Calendar
import      android.os.Bundle
import      androidx.fragment.app.Fragment
import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import      android.widget.AdapterView
import      android.widget.ArrayAdapter
import      android.widget.Button
import      android.widget.EditText
import      android.widget.ImageButton
import      android.widget.RadioGroup
import      android.widget.Spinner
import      android.widget.TextView
import      android.widget.Toast
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.wallet.subject.WalletFragment
import      com.rubdashen.finsimple.menu.wallet.bills.models.BillBaseInformation
import      com.rubdashen.finsimple.menu.wallet.edition.types.EditTypeAction
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.bill.response.BillCreationResponse
import com.rubdashen.finsimple.shared.api.bill.response.BillInformationResponse
import com.rubdashen.finsimple.shared.api.bill.response.BillUpdateResponse
import      com.rubdashen.finsimple.shared.bill.types.BankType
import      com.rubdashen.finsimple.shared.bill.BillConstraints
import      com.rubdashen.finsimple.shared.bill.types.BankTypeConstraint
import      com.rubdashen.finsimple.shared.tools.Functions
import      com.rubdashen.finsimple.shared.tools.types.DateComparisonResult
import      retrofit2.Call
import      retrofit2.Callback
import      retrofit2.Response
import      java.text.SimpleDateFormat
import      java.util.Locale



public final class CreateUpdateBillFragment : Fragment
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//				    Members and Fields
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //					Variables
    //	-------------------------------------------
    private val m_Action: EditTypeAction
    private val m_BillId: Int



//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			Constructors and ~Destructor~
    //	-------------------------------------------
    public constructor(
        billId: Int
    ): super(R.layout.fragment_create_update_bill) {
        this.m_Action = EditTypeAction.EditBill
        this.m_BillId = billId
    }

    public constructor(): super(R.layout.fragment_create_update_bill) {
        this.m_Action = EditTypeAction.CreateBill
        this.m_BillId = BillConstraints.invalidBillId
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
        return inflater.inflate(R.layout.fragment_create_update_bill, container, false)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ): Unit {
        super.onViewCreated(view, savedInstanceState)
        this.configureAction()
        this.configureBankSpinner(view)
        this.configureDatePickers()
        this.createOrEditButton()

        this.autofillEditInformation()
        this.backButton()
        this.bankAutocomplete()
    }

    private fun configureAction(): Unit {
        val title: TextView     = view?.findViewById(R.id.create_or_update_bill_text)!!
        val button: TextView    = view?.findViewById(R.id.create_or_update_bill_button)!!

        when (this.m_Action) {
            EditTypeAction.CreateBill -> {
                title.text = "Añadir una nueva letra"
                button.text = "Crear letra"
            }
            EditTypeAction.EditBill -> {
                title.text = "Editar letra"
                button.text = "Actualizar letra"
            }
        }
    }
    private fun configureBankSpinner(viewParent: View): Unit {
        val spinner: Spinner                = viewParent.findViewById(R.id.spinner_banks)!!
        val options: List<String>           = BankType.toStringList()
        val adapter: ArrayAdapter<String>   = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, options)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            public override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ): Unit {
                val selectedOption: String  = options[position]
                val bankType: BankType      = BankType.fromString(selectedOption)
                val radioGroup: RadioGroup  = viewParent.findViewById(R.id.radio_group_use_bank)!!

                if (radioGroup.checkedRadioButtonId != -1) {
                    this@CreateUpdateBillFragment.autofillBankInformation(bankType)
                }
            }

            public override fun onNothingSelected(parent: AdapterView<*>): Unit { }
        }
    }
    private fun configureDatePickers(): Unit {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(BillConstraints.timeFormat, Locale.US)

        //  # Date Pickers
        //      @CreatedDate
        val createdDatePicker: EditText = view?.findViewById(R.id.edit_text_bill_created_date)!!
        createdDatePicker.setOnClickListener {
            DatePickerDialog(
                this.requireContext(), { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    createdDatePicker.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        //      @DueDate
        val dueDatePicker: EditText = view?.findViewById(R.id.edit_text_bill_due_date)!!
        dueDatePicker.setOnClickListener {
            DatePickerDialog(
                this.requireContext(), { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    dueDatePicker.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        //      @DiscountedDays
        val discountedDays: EditText = view?.findViewById(R.id.edit_text_discounted_days)!!
        discountedDays.setOnClickListener {
            DatePickerDialog(
                this.requireContext(), { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    discountedDays.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun createOrEditButton(): Unit {
        val createButton: Button = view?.findViewById(R.id.create_or_update_bill_button)!!
        createButton.setOnClickListener {
            if (!this.checkBillInputs()) return@setOnClickListener

            when (this.m_Action) {
                EditTypeAction.CreateBill -> this.createBill()
                EditTypeAction.EditBill -> this.editBill()
            }
        }
    }

    private fun createBill(): Unit {
        if (this.m_Action != EditTypeAction.CreateBill) return

        val createButton: Button        = view?.findViewById(R.id.create_or_update_bill_button)!!
        val title: EditText             = view?.findViewById(R.id.edit_text_bill_title)!!
        val description: EditText       = view?.findViewById(R.id.editText_bill_description)!!
        val createdDate: EditText       = view?.findViewById(R.id.edit_text_bill_created_date)!!
        val dueDate: EditText           = view?.findViewById(R.id.edit_text_bill_due_date)!!
        val discountedDate: EditText    = view?.findViewById(R.id.edit_text_discounted_days)!!
        val bank: Spinner               = view?.findViewById(R.id.spinner_banks)!!
        val nominalValue: EditText      = view?.findViewById(R.id.edit_text_nominal_value)!!
        val tea: EditText               = view?.findViewById(R.id.edit_text_tea)!!
        val desgravamen: EditText       = view?.findViewById(R.id.edit_text_desgravamen)!!
        val assignedRetention: EditText = view?.findViewById(R.id.edit_text_retention)!!
        val originalText: String        = createButton.text.toString()

        createButton.text = "Creando..."
        val call: Call<BillCreationResponse> = ApiWorker.createBill(
            BillBaseInformation(
                title.text.toString(),
                description.text.toString(),
                createdDate.text.toString(),
                dueDate.text.toString(),
                discountedDate.text.toString(),
                nominalValue.text.toString().toDouble(),
                BankType.fromString(bank.selectedItem.toString()),
                tea.text.toString().toDouble(),
                desgravamen.text.toString().toDouble(),
                assignedRetention.text.toString().toDouble()
            )
        )

        call.enqueue(object: Callback<BillCreationResponse> {
            public override fun onResponse(
                call: Call<BillCreationResponse>,
                response: Response<BillCreationResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val billCreationResponse: BillCreationResponse? = response.body()

                    billCreationResponse?.let {
                        if (billCreationResponse.success) {
                            try {
                                Toast.makeText(
                                    this@CreateUpdateBillFragment.requireContext(),
                                    "Letra creada con éxito", Toast.LENGTH_SHORT
                                ).show()
                            }
                            catch (_: Exception) { }

                            this@CreateUpdateBillFragment.replaceFragment(WalletFragment())
                        }
                    }
                }

                createButton.text = originalText
            }

            public override fun onFailure(
                call: Call<BillCreationResponse>, t: Throwable
            ): Unit {
                createButton.text = originalText
            }
        })
    }
    private fun editBill(): Unit {
        if (this.m_Action != EditTypeAction.EditBill) return

        val createButton: Button        = view?.findViewById(R.id.create_or_update_bill_button)!!
        val title: EditText             = view?.findViewById(R.id.edit_text_bill_title)!!
        val description: EditText       = view?.findViewById(R.id.editText_bill_description)!!
        val createdDate: EditText       = view?.findViewById(R.id.edit_text_bill_created_date)!!
        val dueDate: EditText           = view?.findViewById(R.id.edit_text_bill_due_date)!!
        val discountedDate: EditText    = view?.findViewById(R.id.edit_text_discounted_days)!!
        val bank: Spinner               = view?.findViewById(R.id.spinner_banks)!!
        val nominalValue: EditText      = view?.findViewById(R.id.edit_text_nominal_value)!!
        val tea: EditText               = view?.findViewById(R.id.edit_text_tea)!!
        val desgravamen: EditText       = view?.findViewById(R.id.edit_text_desgravamen)!!
        val assignedRetention: EditText = view?.findViewById(R.id.edit_text_retention)!!
        val originalText: String        = createButton.text.toString()

        createButton.text = "Actualizando..."
        val call: Call<BillUpdateResponse> = ApiWorker.updateBill(
            this.m_BillId,
            BillBaseInformation(
                title.text.toString(),
                description.text.toString(),
                createdDate.text.toString(),
                dueDate.text.toString(),
                discountedDate.text.toString(),
                nominalValue.text.toString().toDouble(),
                BankType.fromString(bank.selectedItem.toString()),
                tea.text.toString().toDouble(),
                desgravamen.text.toString().toDouble(),
                assignedRetention.text.toString().toDouble()
            )
        )

        call.enqueue(object: Callback<BillUpdateResponse> {
            public override fun onResponse(
                call: Call<BillUpdateResponse>,
                response: Response<BillUpdateResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val billCreationResponse: BillUpdateResponse? = response.body()

                    billCreationResponse?.let {
                        if (billCreationResponse.success) {
                            try {
                                Toast.makeText(
                                    this@CreateUpdateBillFragment.requireContext(),
                                    "Letra actualizada con éxito", Toast.LENGTH_SHORT
                                ).show()
                            }
                            catch (_: Exception) { }

                            this@CreateUpdateBillFragment.replaceFragment(WalletFragment())
                        }
                    }
                }

                createButton.text = originalText
            }

            public override fun onFailure(
                call: Call<BillUpdateResponse>, t: Throwable
            ): Unit {
                createButton.text = originalText
            }
        })
    }
    private fun autofillEditInformation(): Unit {
        if (this.m_Action != EditTypeAction.EditBill) return

        val title: EditText             = view?.findViewById(R.id.edit_text_bill_title)!!
        val description: EditText       = view?.findViewById(R.id.editText_bill_description)!!
        val createdDate: EditText       = view?.findViewById(R.id.edit_text_bill_created_date)!!
        val dueDate: EditText           = view?.findViewById(R.id.edit_text_bill_due_date)!!
        val discountedDate: EditText    = view?.findViewById(R.id.edit_text_discounted_days)!!
        val bank: Spinner               = view?.findViewById(R.id.spinner_banks)!!
        val nominalValue: EditText      = view?.findViewById(R.id.edit_text_nominal_value)!!
        val tea: EditText               = view?.findViewById(R.id.edit_text_tea)!!
        val desgravamen: EditText       = view?.findViewById(R.id.edit_text_desgravamen)!!
        val assignedRetention: EditText = view?.findViewById(R.id.edit_text_retention)!!

        val call: Call<BillInformationResponse> = ApiWorker.billInformation(this.m_BillId)

        call.enqueue(object: Callback<BillInformationResponse> {
            public override fun onResponse(
                call: Call<BillInformationResponse>,
                response: Response<BillInformationResponse>
            ): Unit {
                if (response.isSuccessful) {
                    val billInformationResponse: BillInformationResponse? = response.body()

                    billInformationResponse?.let {
                        try {
                            val hres: BillInformationResponse = billInformationResponse

                            title.setText(hres.title)
                            description.setText(hres.description)
                            createdDate.setText(hres.billCreatedDate)
                            dueDate.setText(hres.billDueDate)
                            discountedDate.setText(hres.billDiscountedDate)
                            bank.setSelection(hres.bankTypeId)
                            nominalValue.setText(hres.nominalValue.toBigDecimal().toPlainString())
                            tea.setText(hres.tea.toBigDecimal().toPlainString())
                            desgravamen.setText(hres.degravamen.toBigDecimal().toPlainString())
                            assignedRetention.setText(hres.assignedRetention.toBigDecimal().toPlainString())
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

    private fun checkBillInputs(): Boolean {
        val title: EditText             = view?.findViewById(R.id.edit_text_bill_title)!!
        val description: EditText       = view?.findViewById(R.id.editText_bill_description)!!
        val createdDate: EditText       = view?.findViewById(R.id.edit_text_bill_created_date)!!
        val dueDate: EditText           = view?.findViewById(R.id.edit_text_bill_due_date)!!
        val discountedDate: EditText    = view?.findViewById(R.id.edit_text_discounted_days)!!
        val bank: Spinner               = view?.findViewById(R.id.spinner_banks)!!
        val nominalValue: EditText      = view?.findViewById(R.id.edit_text_nominal_value)!!
        val tea: EditText               = view?.findViewById(R.id.edit_text_tea)!!
        val desgravamen: EditText       = view?.findViewById(R.id.edit_text_desgravamen)!!
        val assignedRetention: EditText = view?.findViewById(R.id.edit_text_retention)!!

        //  > Empty inputs
        var emptyInput: Boolean = title.text.isEmpty()
        emptyInput = (description.text.isEmpty() || emptyInput)
        emptyInput = (createdDate.text.isEmpty() || emptyInput)
        emptyInput = (dueDate.text.isEmpty() || emptyInput)
        emptyInput = (discountedDate.text.isEmpty() || emptyInput)
        emptyInput = ((bank.selectedItemPosition == AdapterView.INVALID_POSITION) || emptyInput)
        emptyInput = (nominalValue.text.isEmpty() || emptyInput)
        emptyInput = (tea.text.isEmpty() || emptyInput)
        emptyInput = (desgravamen.text.isEmpty() || emptyInput)
        emptyInput = (assignedRetention.text.isEmpty() || emptyInput)
        if (emptyInput) {
            this.makeToast("Por favor, rellene todos los campos")
            return false
        }

        //  > Constraints
        //      <> Numeric
        if (nominalValue.text.toString().toDouble() < BillConstraints.minNominalValue) {
            this.makeToast("El valor nominal debe ser mayor o igual a ${BillConstraints.minNominalValue}")
            return false
        }
        if (
            (tea.text.toString().toDouble() < BillConstraints.minTeaValue) ||
            (tea.text.toString().toDouble() > BillConstraints.maxTeaValue)
        ) {
            this.makeToast("La TEA debe ser mayor o igual a ${BillConstraints.minTeaValue} y menor o igual a ${BillConstraints.maxTeaValue}")
            return false
        }
        if (
            (desgravamen.text.toString().toDouble() < BillConstraints.minDesgravamenValue) ||
            (desgravamen.text.toString().toDouble() > BillConstraints.maxDesgravamenValue)
        ) {
            this.makeToast("El desgravamen debe ser mayor o igual a ${BillConstraints.minDesgravamenValue} y menor o igual a ${BillConstraints.maxDesgravamenValue}")
            return false
        }
        if (
            (assignedRetention.text.toString().toDouble() < BillConstraints.minRetentionValue) ||
            (assignedRetention.text.toString().toDouble() > BillConstraints.maxRetentionValue)
        ) {
            this.makeToast("La retención debe ser mayor o igual a ${BillConstraints.minRetentionValue} y menor o igual a ${BillConstraints.maxRetentionValue}")
            return false
        }
        //      <> Dates
        //          |: CreatedDate < DueDate
        var result: DateComparisonResult = Functions.compareDates(
            createdDate.text.toString(), dueDate.text.toString()
        )
        if ((result == DateComparisonResult.Later) || (result == DateComparisonResult.Same)) {
            this.makeToast("La fecha de creación debe ser menor a la fecha de vencimiento")
            return false
        }
        //          |: DueDate < DiscountedDate
        result = Functions.compareDates(createdDate.text.toString(), discountedDate.text.toString())
        if (result == DateComparisonResult.Later) {
            this.makeToast("La fecha de vencimiento debe ser menor a la fecha de descuento")
            return false
        }
        //          |: DiscountedDate < DueDate
        result = Functions.compareDates(discountedDate.text.toString(), dueDate.text.toString())
        if (result == DateComparisonResult.Later) {
            this.makeToast("La fecha de descuento debe ser menor a la fecha de vencimiento")
            return false
        }
        //          |: CreatedDate < MinCreatedDate
        result = Functions.compareDates(createdDate.text.toString(), BillConstraints.minCreatedDate)
        if (result == DateComparisonResult.Earlier) {
            this.makeToast("La fecha de creación debe ser mayor o igual a ${BillConstraints.minCreatedDate}")
            return false
        }
        //          |: CreatedDate > MaxCreatedDate
        result = Functions.compareDates(createdDate.text.toString(), BillConstraints.maxCreatedDate)
        if (result == DateComparisonResult.Later) {
            this.makeToast("La fecha de creación debe ser menor o igual a ${BillConstraints.maxCreatedDate}")
            return false
        }
        //      <> DateRange
        if (Functions.daysBetweenDates(createdDate.text.toString(), dueDate.text.toString()) < BillConstraints.minExpirationInDays) {
            this.makeToast("El rango de fechas debe ser de al menos ${BillConstraints.minExpirationInDays} días")
            return false
        }


        return true
    }
    private fun backButton(): Unit {
        val backButton: ImageButton = view?.findViewById(R.id.create_bill_back_button)!!
        backButton.setOnClickListener {
            this.replaceFragment(WalletFragment())
        }
    }
    private fun bankAutocomplete(): Unit {
        val radioGroup: RadioGroup  = view?.findViewById(R.id.radio_group_use_bank)!!
        val bankSpinner: Spinner    = view?.findViewById(R.id.spinner_banks)!!

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val bankType: BankType          = BankType.fromString(bankSpinner.selectedItem.toString())
            val tea: EditText               = view?.findViewById(R.id.edit_text_tea)!!
            val desgravamen: EditText       = view?.findViewById(R.id.edit_text_desgravamen)!!
            val assignedRetention: EditText = view?.findViewById(R.id.edit_text_retention)!!

            when (checkedId) {
                R.id.radio_use_bank_yes -> {
                    this.autofillBankInformation(bankType)

                    tea.clearFocus()
                    tea.isFocusable = false
                    tea.isFocusableInTouchMode = false

                    desgravamen.clearFocus()
                    desgravamen.isFocusable = false
                    desgravamen.isFocusableInTouchMode = false

                    assignedRetention.clearFocus()
                    assignedRetention.isFocusable = false
                    assignedRetention.isFocusableInTouchMode = false
                }
                R.id.radio_use_bank_no -> {
                    tea.setText("")
                    tea.isFocusable = true
                    tea.isFocusableInTouchMode = true

                    desgravamen.setText("")
                    desgravamen.isFocusable = true
                    desgravamen.isFocusableInTouchMode = true

                    assignedRetention.setText("")
                    assignedRetention.isFocusable = true
                    assignedRetention.isFocusableInTouchMode = true
                }
            }
        }
    }
    private fun autofillBankInformation(bankType: BankType): Unit {
        val tea: EditText               = view?.findViewById(R.id.edit_text_tea)!!
        val desgravamen: EditText       = view?.findViewById(R.id.edit_text_desgravamen)!!
        val assignedRetention: EditText = view?.findViewById(R.id.edit_text_retention)!!

        when (bankType) {
            BankType.Pichincha -> {
                tea.setText(BankTypeConstraint.teaPichincha.toString())
                desgravamen.setText(BankTypeConstraint.desgravamenPichincha.toString())
                assignedRetention.setText(BankTypeConstraint.retentionPichincha.toString())
            }
            BankType.Scotiabank -> {
                tea.setText(BankTypeConstraint.teaScotiabank.toString())
                desgravamen.setText(BankTypeConstraint.desgravamenScotiabank.toString())
                assignedRetention.setText(BankTypeConstraint.retentionScotiabank.toString())
            }
            BankType.Interbank -> {
                tea.setText(BankTypeConstraint.teaInterbank.toString())
                desgravamen.setText(BankTypeConstraint.desgravamenInterbank.toString())
                assignedRetention.setText(BankTypeConstraint.retentionInterbank.toString())
            }
            BankType.Bbva -> {
                tea.setText(BankTypeConstraint.teaBbva.toString())
                desgravamen.setText(BankTypeConstraint.desgravamenBbva.toString())
                assignedRetention.setText(BankTypeConstraint.retentionBbva.toString())
            }
            BankType.Bcp -> {
                tea.setText(BankTypeConstraint.teaBcp.toString())
                desgravamen.setText(BankTypeConstraint.desgravamenBcp.toString())
                assignedRetention.setText(BankTypeConstraint.retentionBcp.toString())
            }
        }
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