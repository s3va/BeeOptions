package tk.kvakva.beeoptions

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.text.StringBuilder

class OptionRecyViewAdap : RecyclerView.Adapter<OptionRecyViewAdap.ViewHolder>() {
    var data = listOf<BeeOptionsData.Service>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder.from(parent)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = data.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTV: TextView = itemView.findViewById(R.id.nameTxView)
        private val entityName: TextView = itemView.findViewById(R.id.entityNameTxView)
        private val entityDesc: TextView = itemView.findViewById(R.id.entityDescTxView)
        private val baseFtch: TextView = itemView.findViewById(R.id.baseFeaturesTxView)

        fun bind(item: BeeOptionsData.Service) {
            Log.d("M_ViewHolder", "!!!!!!!!!!!!!!!!!!!!! ITEM !!!!!!!!!!!!!! $item")
            //val res = itemView.context.resources
            nameTV.text = item.name
            entityName.text = item.entityName
            entityDesc.text = item.entityDesc
            val sb = StringBuilder()
            item.baseFeatures.forEach {
                sb.append(it.code)
                sb.append(", ")
            }
            if (item.baseFeatures.isNotEmpty()) {
                sb.deleteCharAt(sb.lastIndex)
                sb.deleteCharAt(sb.lastIndex)
            }
            Log.d("M_ViewHolder", "baseFeatures ${item.baseFeatures} $sb")
            baseFtch.text = sb
            nameTV.setOnLongClickListener {
                val inflpup =
                    itemView.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val pupv = inflpup.inflate(R.layout.onefulloption, null, false) as View
                pupv.findViewById<TextView>(R.id.nameTextView2).text = "name: ${item.name}"
                pupv.findViewById<TextView>(R.id.entityNameTextView2).text =
                    "entityName: ${item.entityName}"
                pupv.findViewById<TextView>(R.id.entityDescTextView2).text =
                    "entityDesc: ${item.entityDesc}"
                pupv.findViewById<TextView>(R.id.rcRateTextView2).text = "rcRate: ${item.rcRate}"
                pupv.findViewById<TextView>(R.id.rcRatePeriodTextView2).text =
                    "rcRatePeriod: ${item.rcRatePeriod}"
                pupv.findViewById<TextView>(R.id.rcRatePeriodTextTextView2).text =
                    "rcRatePeriodText: ${item.rcRatePeriodText}"
                pupv.findViewById<TextView>(R.id.categoryTextView2).text =
                    "category: ${item.category}"
                pupv.findViewById<TextView>(R.id.sdbSizeTextView2).text = "sdbSize: ${item.sdbSize}"
                pupv.findViewById<TextView>(R.id.viewIndTextView2).text = "viewInd: ${item.viewInd}"
                pupv.findViewById<TextView>(R.id.removeIndTextView2).text =
                    "removeInd: ${item.removeInd}"
                pupv.findViewById<TextView>(R.id.effDateTextView2).text = "effDate: ${item.effDate}"
                pupv.findViewById<TextView>(R.id.archiveIndTextView2).text =
                    "archiveInd: ${item.archiveInd}"
                val sbl = StringBuilder("codes: ")
                item.baseFeatures.forEach {
                    sbl.append(it.code)
                    sbl.append(", ")
                }
                if (item.baseFeatures.isNotEmpty()) {
                    sbl.deleteCharAt(sbl.lastIndex)
                    sbl.deleteCharAt(sbl.lastIndex)
                }
                pupv.findViewById<TextView>(R.id.baseFeaturesTextView2).text = "baseFeatures: $sbl"

                val pupw = PopupWindow(
                    pupv,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
                )
                //pupw.isFocusable = true
                pupv.setBackgroundColor(Color.WHITE)

                pupw.showAtLocation(itemView.rootView, Gravity.CENTER, 0, 0)
                pupv.setOnTouchListener { v, event ->
                    pupw.dismiss()
                    false
                }

                true
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.optionitem, parent, false)

                return ViewHolder(view)
            }
        }
    }


}