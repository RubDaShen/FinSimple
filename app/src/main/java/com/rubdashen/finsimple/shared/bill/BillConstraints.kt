package     com.rubdashen.finsimple.shared.bill



public final class BillConstraints
{
    companion object {
        public const val timeFormat: String             = "dd-MM-yyyy"
        public const val minNominalValue: Double        = 500.00
        public const val minTeaValue: Double            = 5.00
        public const val maxTeaValue: Double            = 75.00
        public const val minDesgravamenValue: Double    = 0.001
        public const val maxDesgravamenValue: Double    = 0.5
        public const val minCreatedDate: String         = "01-01-2020"
        public const val maxCreatedDate: String         = "31-12-2030"
        public const val minExpirationInDays: Long      = 7
        public const val minRetentionValue: Double      = 0.0
        public const val maxRetentionValue: Double      = 25.0

        public const val invalidBillId: Int            = 0
    }
}