package     com.rubdashen.finsimple.shared.api.bill.request



public final data class BillCreationRequest(
    public val userId: Int,
    public val title: String,
    public val description: String,
    public val billCreatedDate: String,
    public val billDueDate: String,
    public val billDiscountedDate: String,
    public val nominalValue: Double,
    public val bankTypeId: Int,
    public val tea: Double,
    public val degravamen: Double,
    public val assignedRetention: Double
)