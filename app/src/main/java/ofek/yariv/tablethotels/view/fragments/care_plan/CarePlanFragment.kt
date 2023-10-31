package ofek.yariv.tablethotels.view.fragments.care_plan

import android.os.Bundle
import android.view.View
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.databinding.FragmentCarePlanBinding
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.view.fragments.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CarePlanFragment : BaseFragment(R.layout.fragment_care_plan) {
    private val carePlanViewModel: CarePlanViewModel by viewModel()
    private lateinit var binding: FragmentCarePlanBinding
    override fun getFragmentName() = ReportConstants.CARE_PLAN
    override fun getFragmentItemId() = R.id.carePlanFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCarePlanBinding.bind(view)
        initiateLayout()
    }

    private fun initiateLayout() {
        observeChanges()
    }

    private fun observeChanges() {
    }
}