package ofek.yariv.tablethotels.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.model.data.Hotel

class HotelAdapter(
    private val onClickListener: (Hotel) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<Hotel>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hotel = items[position]
        (holder as HotelViewHolder).bind(hotel, onClickListener)
    }

    fun submitList(hotels: List<Hotel>) {
        val removedItems = items.size
        items.clear()
        notifyItemRangeRemoved(0, removedItems)
        items.addAll(hotels)
        notifyItemRangeInserted(0, items.size)
    }

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hotelName: TextView = itemView.findViewById(R.id.tvHotelName)

        fun bind(hotel: Hotel, onStationClickListener: (Hotel) -> Unit) {
            hotelName.text = hotel.name
            itemView.setOnClickListener { onStationClickListener(hotel) }
        }
    }
}