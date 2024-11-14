package     com.rubdashen.finsimple.menu.wallet.bills.adapter

import      android.view.LayoutInflater
import      android.view.View
import      android.view.ViewGroup
import      android.widget.AdapterView.OnItemClickListener
import      androidx.recyclerview.widget.RecyclerView.Adapter
import      com.rubdashen.finsimple.R



public final class BillsViewAdapter : Adapter<BillViewPrototype>
{
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//				    Members and Fields
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //					Variables
    //	-------------------------------------------
    private val d_Bills: MutableList<BillView>
    private val d_Listener: OnItemClickListener



//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|
//			        Functions and Methods
//	|-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-|

    //	-------------------------------------------
    //			Constructors and ~Destructor~
    //	-------------------------------------------
    public constructor(
        bills: MutableList<BillView>,
        listener: OnItemClickListener
    ) {
        this.d_Bills    = bills
        this.d_Listener = listener
    }

    //	-------------------------------------------
    //			    Loading Functions
    //	-------------------------------------------
    public override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BillViewPrototype {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.bill_item_view, parent, false)

        return BillViewPrototype(view)
    }

    //	-------------------------------------------
    //			        Functions
    //	-------------------------------------------
    public override fun onBindViewHolder(
        holder: BillViewPrototype, position: Int
    ): Unit {
        holder.bind(this.d_Bills[position], this.d_Listener, position)
    }

    //	-------------------------------------------
    //			    Setters and Getters
    //	-------------------------------------------
    public override fun getItemCount(): Int = this.d_Bills.size
}