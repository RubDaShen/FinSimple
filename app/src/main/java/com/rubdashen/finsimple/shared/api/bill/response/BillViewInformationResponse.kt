package     com.rubdashen.finsimple.shared.api.bill.response

import      com.rubdashen.finsimple.menu.wallet.bills.models.BillView



public final data class BillViewInformationResponse(
    public val id: Int,
    public val title: String,
    public val description: String,
    public val date: String
) {
    public fun toBillView(): BillView {
        return BillView(
            id = this.id,
            title = this.title,
            description = this.description,
            date = this.date
        )
    }
}