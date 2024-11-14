package     com.rubdashen.finsimple.menu.wallet.creation

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
import      android.widget.Toast
import      androidx.fragment.app.FragmentManager
import      androidx.fragment.app.FragmentTransaction
import      com.rubdashen.finsimple.R
import      com.rubdashen.finsimple.menu.wallet.subject.WalletFragment
import      com.rubdashen.finsimple.menu.wallet.bills.models.BillCreationInformation
import      com.rubdashen.finsimple.shared.api.ApiWorker
import      com.rubdashen.finsimple.shared.api.bill.response.BillCreationResponse
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



public final class CreateBillFragment : Fragment(R.layout.fragment_create_bill)
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
        return inflater.inflate(R.layout.fragment_create_bill, container, false)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ): Unit {
        super.onViewCreated(view, savedInstanceState)
        this.configureBankSpinner(view)
        this.configureDatePickers()
        this.createButton()
        this.backButton()
        this.bankAutocomplete()
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
                    this@CreateBillFragment.autofillBankInformation(bankType)
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
    private fun replaceFragment(fragment: Fragment): Unit {
        val fragmentManager: FragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }

    private fun createButton(): Unit {
        val createButton = view?.findViewById<View>(R.id.create_bill_button)!!
        createButton.setOnClickListener {
            if (!this.checkCreationInputs()) return@setOnClickListener

            this.createBill()
        }
    }
    private fun createBill(): Unit {
        val createButton: Button        = view?.findViewById(R.id.create_bill_button)!!
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
            BillCreationInformation(
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
                                    this@CreateBillFragment.requireContext(),
                                    "Letra creada con éxito", Toast.LENGTH_SHORT
                                ).show()
                            }
                            catch (_: Exception) { }

                            this@CreateBillFragment.replaceFragment(WalletFragment())
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
    private fun checkCreationInputs(): Boolean {
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
}