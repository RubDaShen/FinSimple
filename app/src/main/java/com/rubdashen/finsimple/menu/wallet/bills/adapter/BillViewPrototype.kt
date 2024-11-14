package     com.rubdashen.finsimple.menu.wallet.bills.adapter

import      android.view.View
import      android.widget.AdapterView.OnItemClickListener
import      android.widget.TextView
import      androidx.recyclerview.widget.RecyclerView
import      com.rubdashen.finsimple.R
import com.rubdashen.finsimple.menu.wallet.bills.models.BillView


public final class BillViewPrototype : RecyclerView.ViewHolder
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//				    Members and Fields
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //					Variables
    //	-------------------------------------------
    private val m_Title: TextView
    private val m_Description: TextView
    private val m_Date: TextView



//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			Constructors and ~Destructor~
    //	-------------------------------------------
    public constructor(
        itemView: View
    ): super(itemView) {
        this.m_Title        = itemView.findViewById(R.id.bill_title_text)
        this.m_Description  = itemView.findViewById(R.id.bill_description_text)
        this.m_Date         = itemView.findViewById(R.id.bill_date_text)
    }

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    public fun bind(
        bill: BillView, listener: OnItemClickListener,
        position: Int
    ): Unit {
        this.m_Title.text       = bill.title
        this.m_Description.text = bill.description
        this.m_Date.text        = bill.date

        this.itemView.setOnClickListener {
            listener.onItemClick(null, this.itemView, position, 0)
        }
    }
}