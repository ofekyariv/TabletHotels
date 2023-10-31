package ofek.yariv.tablethotels.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleCoroutineScope
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.managers.AutoCompleteManager
import kotlinx.coroutines.*

class SuggestionAdapter(
    activity: AppCompatActivity,
    private val autoCompleteManager: AutoCompleteManager,
) :
    ArrayAdapter<String>(activity, R.layout.item_autocomplete, ArrayList<String>()), Filterable {

    private val inflater = LayoutInflater.from(context)
    lateinit var lifecycleScope: LifecycleCoroutineScope

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_autocomplete, parent, false)
        val textView: AppCompatTextView = view.findViewById(R.id.tvAddressItem)
        textView.text = getItem(position)
        return view
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null && constraint.length > 1) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val suggestions =
                            autoCompleteManager.getAutoCompleteSuggestions(constraint.toString())
                        results.values = suggestions
                        results.count = suggestions.size
                        withContext(Dispatchers.Main) {
                            publishResults(constraint, results)
                        }
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    clear()
                    addAll(results.values as List<String>)
                    notifyDataSetChanged()
                }
            }
        }
    }
}
