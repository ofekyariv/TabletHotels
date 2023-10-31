package ofek.yariv.tablethotels.view.fragments.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.databinding.FragmentHomeBinding
import ofek.yariv.tablethotels.utils.Constants
import ofek.yariv.tablethotels.utils.Constants.TAG
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.utils.ReportConstants.BTN_MIC
import ofek.yariv.tablethotels.utils.ReportConstants.BTN_MY_LOCATION
import ofek.yariv.tablethotels.utils.ReportConstants.CLICKED
import ofek.yariv.tablethotels.utils.ReportConstants.CTA
import ofek.yariv.tablethotels.utils.ReportConstants.ERROR
import ofek.yariv.tablethotels.utils.ReportConstants.ERROR_GETTING_LOCATION
import ofek.yariv.tablethotels.utils.Resource
import ofek.yariv.tablethotels.utils.managers.InternetManager
import ofek.yariv.tablethotels.utils.managers.LocationManager
import ofek.yariv.tablethotels.utils.managers.PermissionsManager
import ofek.yariv.tablethotels.utils.managers.SpeechToTextManager
import ofek.yariv.tablethotels.view.activities.webViewActivity.WebViewActivity
import ofek.yariv.tablethotels.view.adapters.SuggestionAdapter
import ofek.yariv.tablethotels.view.fragments.BaseFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.logger.KOIN_TAG
import org.koin.core.parameter.parametersOf

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    override fun getFragmentName() = ReportConstants.HOME
    override fun getFragmentItemId() = R.id.homeFragment

    private val internetManager: InternetManager by inject()
    private val locationManager: LocationManager by inject()
    private val destinationAdapter: SuggestionAdapter by inject {
        parametersOf(requireActivity())
    }
    private val permissionsManager: PermissionsManager by inject {
        parametersOf(requireActivity())
    }
    private val speechToTextManager: SpeechToTextManager by inject {
        parametersOf(requireActivity())
    }
    private val homeFragmentViewModel: HomeFragmentViewModel by viewModel {
        parametersOf(requireActivity())
    }
    private val locationPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (permissionsManager.hasLocationPermission()) {
                permissionsManager._locationPermission.value = true
            } else {
                Toast.makeText(
                    requireContext(),
                    requireActivity().getString(R.string.precise_location_needed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initiateLayout()
    }

    private fun initiateLayout() {
        setAutoCompleteAdapters()
        observeChanges()
        setOnClickListeners()
        setTextChangedListeners()
        if (permissionsManager.hasLocationPermission()) {
            setOriginAddressFromLocation()
        }
    }

    private fun setAutoCompleteAdapters() {
        destinationAdapter.lifecycleScope = lifecycleScope
        binding.destinationAutoCompleteView.setAdapter(destinationAdapter)
    }

    private fun observeChanges() {
        lifecycleScope.launch {
            launch {
                permissionsManager.locationPermission.collect {
                    if (it) setOriginAddressFromLocation()
                }
            }
            launch {
                homeFragmentViewModel.searchResource.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            binding.searchProgressBar.visibility = View.VISIBLE
                            binding.btnSearch.isEnabled = false
                        }

                        is Resource.Failure -> {
                            binding.searchProgressBar.visibility = View.GONE
                            binding.btnSearch.isEnabled = true
                            showToast(getString(R.string.error))
                            analyticsManager.report(ERROR, ERROR)
                        }

                        is Resource.Success -> {
                            binding.searchProgressBar.visibility = View.GONE
                            binding.btnSearch.isEnabled = true
                            val intent = Intent(requireActivity(), WebViewActivity::class.java)
                            intent.putExtra(
                                Constants.SEARCH_QUERY,
                                binding.destinationAutoCompleteView.text.toString()
                            )
                            startActivity(intent)
                        }

                        is Resource.None -> {
                            binding.searchProgressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.originAutoCompleteView.setDropDownBackgroundResource(R.color.md_theme_surface)
        binding.destinationAutoCompleteView.setDropDownBackgroundResource(R.color.md_theme_surface)
        binding.originAutoCompleteView.setOnItemClickListener { _: AdapterView<*>, _: View, _: Int, _: Long -> binding.destinationAutoCompleteView.requestFocus() }
        binding.destinationAutoCompleteView.setOnItemClickListener { _: AdapterView<*>, _: View, _: Int, _: Long ->
            val imm =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.destinationAutoCompleteView.windowToken, 0)
        }
        binding.btnMyLocation.setOnClickListener {
            analyticsManager.report("$BTN_MY_LOCATION $CLICKED", CLICKED)
            if (permissionsManager.hasLocationPermission()) {
                setOriginAddressFromLocation()
            } else {
                permissionsManager.requestLocationPermission(locationPermissionResult)
            }
        }
        binding.btnMicOrigin.setOnClickListener {
            analyticsManager.report("$BTN_MIC $CLICKED", CLICKED)
            lifecycleScope.launch {
                speechToTextManager.startSpeechRecognition().also { result ->
                    speechToTextManager.unregisterLauncher()
                    binding.originAutoCompleteView.setText(result)
                }
            }
        }
        binding.btnClearOrigin.setOnClickListener { binding.originAutoCompleteView.setText("") }
        binding.btnMicDestination.setOnClickListener {
            analyticsManager.report("$BTN_MIC $CLICKED", CLICKED)
            lifecycleScope.launch {
                speechToTextManager.startSpeechRecognition().also { result ->
                    speechToTextManager.unregisterLauncher()
                    binding.destinationAutoCompleteView.setText(result)
                }
            }
        }
        binding.btnClearDestination.setOnClickListener {
            binding.destinationAutoCompleteView.setText("")
        }

        binding.btnSearch.setOnClickListener {
            if (validatesearch()) {
                analyticsManager.report("$CTA $CLICKED", CLICKED)
                homeFragmentViewModel.search(
                    origin = binding.originAutoCompleteView.text.toString(),
                    destination = binding.destinationAutoCompleteView.text.toString()
                )
            }
        }
    }

    private fun validatesearch(): Boolean {
        return when {
            binding.originAutoCompleteView.text.isEmpty() -> {
                showToast(getString(R.string.fill_origin))
                false
            }

            binding.destinationAutoCompleteView.text.isEmpty() -> {
                showToast(getString(R.string.fill_destination))
                false
            }

            !internetManager.isInternetAvailable() -> {
                showToast(getString(R.string.no_connection))
                false
            }

            else -> true
        }
    }

    private fun setTextChangedListeners() {
        binding.originAutoCompleteView.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                binding.apply {
                    btnMicOrigin.visibility = View.GONE
                    btnClearOrigin.visibility = View.VISIBLE
                    btnMyLocation.visibility = View.GONE
                }
            } else {
                binding.apply {
                    btnMicOrigin.visibility = View.VISIBLE
                    btnClearOrigin.visibility = View.GONE
                    btnMyLocation.visibility = View.VISIBLE
                }
            }
        }

        binding.destinationAutoCompleteView.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                binding.btnMicDestination.visibility = View.GONE
                binding.btnClearDestination.visibility = View.VISIBLE
            } else {
                binding.btnMicDestination.visibility = View.VISIBLE
                binding.btnClearDestination.visibility = View.GONE
            }
        }
    }

    private fun setOriginAddressFromLocation() {
        lifecycleScope.launch {
            binding.locationProgressBar.visibility = View.VISIBLE
            when (val resource = locationManager.getAddressFromLocation(maxDuration = 10_000L)) {
                is Resource.Success -> {
                    binding.originAutoCompleteView.setText(resource.data)
                }

                else -> {
                    showToast(getString(R.string.turn_on_location))
                    analyticsManager.report(ERROR_GETTING_LOCATION, ERROR)
                }
            }
            binding.locationProgressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}