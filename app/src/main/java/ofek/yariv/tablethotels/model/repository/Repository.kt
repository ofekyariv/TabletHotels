package ofek.yariv.tablethotels.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val autoCompleteAPI: AutoCompleteAPI,
) {
    suspend fun getAutoCompleteSuggestions(query: String) =
        withContext(Dispatchers.IO) {
            autoCompleteAPI.getAutoCompleteSuggestions(query)
        }
}

