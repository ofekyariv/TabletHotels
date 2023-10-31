package ofek.yariv.tablethotels.model.repository

import ofek.yariv.tablethotels.model.network.AutoCompleteService

class AutoCompleteAPI(private val autoCompleteService: AutoCompleteService) {
    suspend fun getAutoCompleteSuggestions(query: String): List<String> {
        val results = mutableListOf<String>()
        try {
            val response = autoCompleteService.getAutoCompleteSuggestions(query = query)
            for (i in 0 until minOf(response.size, 5)) {
                response[i].name?.let { results.add(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return results
    }
}