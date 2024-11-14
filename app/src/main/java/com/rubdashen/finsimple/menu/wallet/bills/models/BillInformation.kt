package     com.rubdashen.finsimple.menu.wallet.bills.models



public final data class BillInformation(
    public val title: String,
    public val description: String,
    public val billCreatedDate: String,
    public val billDueDate: String,
    public val billDiscountedDate: String,
    public val nominalValue: Double,
    public val bankTypeId: Int,
    public val tea: Double,
    public val degravamen: Double,
    public val assignedRetention: Double,
    public val tep: Double,
    public val period: Int,
    public val discountedRate: Double,
    public val netWorth: Double,
    public val receivedValue: Double,
    public val deliveredValue: Double,
    public val tcea: Double
)
