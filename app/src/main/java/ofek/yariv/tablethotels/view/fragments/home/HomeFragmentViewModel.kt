package ofek.yariv.tablethotels.view.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ofek.yariv.tablethotels.utils.Resource

class HomeFragmentViewModel : ViewModel() {

    private val _searchResource =
        MutableStateFlow<Resource<Boolean>>(Resource.None())
    val searchResource = _searchResource.asStateFlow()

    fun search(origin: String, destination: String) {
        viewModelScope.launch {
            _searchResource.value = Resource.Loading()
            when (validateSomething(origin, destination)) {
                is Resource.Success -> {
                    _searchResource.value = Resource.Success(true)
                    //_searchResource.value = Resource.None()
                }

                else -> _searchResource.value = Resource.Failure()
            }
        }
    }

    private fun validateSomething(origin: String, destination: String): Resource.Success<Boolean> {
        return Resource.Success(true)
    }
}