package info.aiavci.twitterfeedapp

import android.os.Bundle
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