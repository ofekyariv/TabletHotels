package ofek.yariv.tablethotels.utils.managers

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.Resource
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationManager(private val context: Context) {

    private val locationErrorMessage = context.getString(R.string.cant_find_location)
    private val turnOnLocationErrorMessage = context.getString(R.string.turn_on_location)

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(maxDuration: Long): Resource<Location> {
        return withTimeoutOrNull(maxDuration) {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val currentLocationRequest = CurrentLocationRequest.Builder()
                        .setDurationMillis(maxDuration)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build()
                    fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
                        .addOnSuccessListener { location ->
                            location?.let {
                                continuation.resume(Resource.Success(location))
                            } ?: continuation.resume(Resource.Failure(locationErrorMessage))
                        }.addOnFailureListener {
                            continuation.resume(Resource.Failure(locationErrorMessage))
                        }
                }
            }
        } ?: Resource.Failure("getCurrentLocationIfHasPermission timed out")
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(maxDuration: Long): Location? {
        return withTimeoutOrNull(maxDuration) {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    fusedLocationClient.lastLocation.addOnCompleteListener {
                        if (it.isSuccessful) {
                            continuation.resume(it.result)
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
            }
        }
    }

    suspend fun getAddressFromLocation(maxDuration: Long): Resource<String> {
        val locationResult = getCurrentLocation(maxDuration = maxDuration)
        return if (locationResult is Resource.Success) {
            val location =
                locationResult.data ?: return Resource.Failure(turnOnLocationErrorMessage)
            convertLocationToAddress(location, maxDuration)
        } else {
            Resource.Failure(locationResult.message.toString())
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun convertLocationToAddress(
        location: Location,
        maxDuration: Long,
    ): Resource<String> {
        return withTimeoutOrNull(maxDuration) {
            withContext(Dispatchers.IO) {
                val geocoder = Geocoder(context, context.resources.configuration.locales.get(0))
                try {
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    return@withContext addresses?.firstOrNull()?.getAddressLine(0)
                        ?.let { Resource.Success(it) } ?: Resource.Failure(locationErrorMessage)
                } catch (e: Exception) {
                    return@withContext Resource.Failure(locationErrorMessage)
                }
            }
        } ?: Resource.Failure(locationErrorMessage)
    }
}