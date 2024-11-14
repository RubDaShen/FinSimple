package     com.rubdashen.finsimple.shared.api.bill.response



public final data class BillInformationResponse(
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
) {

}