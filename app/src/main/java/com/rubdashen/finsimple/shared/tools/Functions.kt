package     com.rubdashen.finsimple.shared.tools

import      com.rubdashen.finsimple.shared.bill.BillConstraints
import      com.rubdashen.finsimple.shared.tools.types.DateComparisonResult
import      java.text.SimpleDateFormat
import      java.util.Date
import      java.util.concurrent.TimeUnit



public class Functions
{
    companion object {
        public fun compareDates(dateString1: String, dateString2: String): DateComparisonResult {
            val dateFormat = SimpleDateFormat(BillConstraints.timeFormat)

            val date1: Date? = dateFormat.parse(dateString1)
            val date2: Date? = dateFormat.parse(dateString2)

            if ((date1 != null) && (date2 != null)) {
                return when {
                    date1.before(date2) -> DateComparisonResult.Earlier
                    date1.after(date2) -> DateComparisonResult.Later
                    else -> DateComparisonResult.Same
                }
            }

            return DateComparisonResult.Error
        }
        public fun daysBetweenDates(dateString1: String, dateString2: String): Long {
            val dateFormat = SimpleDateFormat(BillConstraints.timeFormat)

            val date1: Date? = dateFormat.parse(dateString1)
            val date2: Date? = dateFormat.parse(dateString2)

            if ((date1 != null) && (date2 != null)) {
                val diffInMillis: Long = kotlin.math.abs(date1.time - date2.time)
                return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
            }

            return -1
        }
        public fun isValidEmail(email: String): Boolean {
            val emailRegex: String = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
            return Regex(emailRegex).matches(email)
        }
    }
}