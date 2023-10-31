package ofek.yariv.tablethotels.view.fragments.hotels

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ofek.yariv.tablethotels.model.data.Hotel

class HotelsViewModel : ViewModel() {

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels = _hotels.asStateFlow()


    fun fetchHotels() {
        val database = FirebaseDatabase.getInstance()
        val hotelsReference = database.reference

        val hotelsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hotels = mutableListOf<Hotel>()
                for (hotelSnapshot in dataSnapshot.children) {
                    val hotel = hotelSnapshot.getValue(Hotel::class.java)
                    if (hotel != null) {
                        hotels.add(hotel)
                    }
                }
                _hotels.value = hotels
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _hotels.value = emptyList()
            }
        }

        hotelsReference.addListenerForSingleValueEvent(hotelsListener)
    }
}