package ofek.yariv.tablethotels.utils.managers

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ofek.yariv.tablethotels.R
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpeechToTextManager(private val activity: Activity) {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    suspend fun startSpeechRecognition(): String = suspendCoroutine { continuation ->
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, activity.getString(R.string.speak_now))

        try {
            activityResultLauncher =
                (activity as ComponentActivity).activityResultRegistry.register(
                    "key",
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data = result.data
                        val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        val spokenText = results?.getOrNull(0)
                        continuation.resume(spokenText ?: "")
                    } else {
                        continuation.resume("")
                    }
                }

            activityResultLauncher.launch(intent)
        } catch (e: Exception) {
            continuation.resume("")
        }
    }

    fun unregisterLauncher() {
        if (::activityResultLauncher.isInitialized) {
            activityResultLauncher.unregister()
        }
    }
}



