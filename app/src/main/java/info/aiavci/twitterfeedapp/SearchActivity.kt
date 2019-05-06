package info.aiavci.twitterfeedapp

import android.app.Dialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity() {

    private val twitterManager: TwitterManager = TwitterManager()

    private val tweetsAdapter: TweetsAdapter = TweetsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchButton.setOnClickListener {
            if (searchText.text.toString().isBlank()) {

                WarningDialog().show(supportFragmentManager, "criteria_missing")

                return@setOnClickListener
            }

            performSearch()
        }

        tweetsListView.adapter = tweetsAdapter
    }

    /**
     * Fetch tweets and update list
     */
    private fun performSearch() {
        GlobalScope.launch {
            val tweets = twitterManager.fetchTweets(searchText.text.toString())

            runOnUiThread {
                (tweetsListView.adapter as TweetsAdapter).setTweets(tweets)
            }
        }
    }
}

class WarningDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Please enter search criteria")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}