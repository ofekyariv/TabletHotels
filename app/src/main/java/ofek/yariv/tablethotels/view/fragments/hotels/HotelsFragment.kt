package ofek.yariv.tablethotels.view.fragments.hotels

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.databinding.FragmentHotelsBinding
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.utils.managers.InternetManager
import ofek.yariv.tablethotels.view.adapters.HotelAdapter
import ofek.yariv.tablethotels.view.fragments.BaseFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HotelsFragment : BaseFragment(R.layout.fragment_hotels) {
    private val hotelsViewModel: HotelsViewModel by viewModel()
    private val internetManager: InternetManager by inject()
    private lateinit var binding: FragmentHotelsBinding
    private var hotelAdapter: HotelAdapter? = null
    override fun getFragmentName() = ReportConstants.HOTELS
    override fun getFragmentItemId() = R.id.hotelFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHotelsBinding.bind(view)
        initiateLayout()
    }

    private fun initiateLayout() {
        observeChanges()

        hotelAdapter = HotelAdapter(
            onClickListener = { hotel ->
                requireActivity().startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.fromParts("tel", hotel.phone, null)
                    )
                )
            }
        )
        binding.rvHotels.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHotels.adapter = hotelAdapter
        if (internetManager.isInternetAvailable()) {
            binding.hotelsProgressBar.visibility = View.VISIBLE
            hotelsViewModel.fetchHotels()
        } else {
            Toast.makeText(requireContext(), R.string.no_connection, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeChanges() {
        lifecycleScope.launch {
            launch {
                hotelsViewModel.hotels.collect { hotels ->
                    if (hotels.isNotEmpty()) {
                        hotelAdapter?.submitList(hotels)
                        binding.hotelsProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}