package     com.rubdashen.finsimple.shared.bill.types

import com.rubdashen.finsimple.shared.api.bill.exceptions.BankNotFound


public final enum class BankType
{
    //  @Values
    Interbank,
    Scotiabank,
    Pichincha,
    Bbva,
    Bcp;

    companion object {
        //  @Functions
        public fun toStringList(): List<String> {
            return listOf(
                "Interbank", "Scotiabank", "Banco Pichincha", "Banco Bilbao Vizcaya Argentaria (BBVA)", "Banco de Crédito del Perú (BCP)"
            )
        }
        public fun fromString(value: String): BankType {
            return when (value) {
                "Interbank" -> Interbank
                "Scotiabank" -> Scotiabank
                "Banco Pichincha" -> Pichincha
                "Banco Bilbao Vizcaya Argentaria (BBVA)" -> Bbva
                "Banco de Crédito del Perú (BCP)" -> Bcp

                else -> throw BankNotFound()
            }
        }
    }
}