package info.aiavci.twitterfeedapp

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tweet_item.view.*
import java.util.*
import android.support.customtabs.CustomTabsIntent
import android.util.Log


class TweetsAdapter(val activity: Activity): BaseAdapter() {
    var tweetsList: List<Tweet>? = listOf()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val menuItemView: View = LinearLayout(activity).apply {
//            addView(TextView(this@TweetsAdapter.activity))
//        }

        val menuItemView: View = CardView(activity).apply {
            addView(activity.layoutInflater.inflate(R.layout.tweet_item, null).apply {
                val tweet = tweetsList?.get(position)

                this.name.text = tweet?.name ?: ""
                this.tweetText.text = tweet?.text ?: ""

                Picasso.get().load(tweet?.profileUrl).into(this.profileImage)

                setOnClickListener {
//                    val url = tweet.url
                    val url = "https://twitter.com/statuses/" + tweet?.id

                    Log.d("data", "check url" + url)

                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(activity, Uri.parse(url))
                }
            })
        }

        return menuItemView
    }

    fun setTweets(tweets: List<Tweet>?) {
        tweetsList = tweets
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    override fun getCount(): Int = tweetsList?.size ?: 0
}