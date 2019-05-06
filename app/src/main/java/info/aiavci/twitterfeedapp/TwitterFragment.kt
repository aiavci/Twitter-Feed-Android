package info.aiavci.twitterfeedapp

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tweet_item.view.*

class TweetFragment : DialogFragment() {

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val nameContent = arguments?.getString("name")
        val tweetTextContent = arguments?.getString("tweet")
        val tweetId = arguments?.getString("id")
        val profileUrl = arguments?.getString("url")

        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.tweet_item, container, false).apply {
            name?.text = nameContent

            tweetText.text = tweetTextContent

            Picasso.get().load(profileUrl).into(profileImage)

            setOnClickListener {
                val url = "https://twitter.com/statuses/$tweetId"

                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(activity, Uri.parse(url))
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}