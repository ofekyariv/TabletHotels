package ofek.yariv.tablethotels.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface AutoCompleteService {
    @GET("suggest/all")
    suspend fun getAutoCompleteSuggestions(
        @Query("term") query: String,
    ): List<AutoCompleteResponse>
}

@JsonClass(generateAdapter = true)
data class AutoCompleteResponse(
    @Json(name = "label") val name: String? = null,
)