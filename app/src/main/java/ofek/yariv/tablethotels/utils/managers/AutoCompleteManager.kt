package ofek.yariv.tablethotels.utils.managers

import ofek.yariv.tablethotels.model.repository.Repository

class AutoCompleteManager(
    private val repository: Repository,
    private val internetManager: InternetManager,
) {

    suspend fun getAutoCompleteSuggestions(query: String): List<String> {
        if (internetManager.isInternetAvailable()) {
            return repository.getAutoCompleteSuggestions(query)
        }
        return emptyList()
    }
}