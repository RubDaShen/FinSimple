package     com.rubdashen.finsimple.menu.wallet.bills.models

import      com.rubdashen.finsimple.shared.api.bill.request.BillCreationRequest
import      com.rubdashen.finsimple.shared.bill.types.BankType
import      com.rubdashen.finsimple.shared.user.UserWrapperSettings



public final data class BillCreationInformation(
    public val title: String,
    public val description: String,
    public val billCreatedDate: String,
    public val billDueDate: String,
    public val billDiscountedDays: String,
    public val nominalValue: Double,
    public val bankType: BankType,
    public val tea: Double,
    public val desgravamen: Double,
    public val assignedRetention: Double
) {
    public fun toCreationRequest(): BillCreationRequest {
        return BillCreationRequest(
            UserWrapperSettings.userId,
            this.title, this.description, this.billCreatedDate, this.billDueDate,
            this.billDiscountedDays, this.nominalValue, this.bankType.ordinal, this.tea,
            this.desgravamen, this.assignedRetention
        )
    }
}