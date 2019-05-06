package info.aiavci.twitterfeedapp

import android.util.Log
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class TwitterManager() {

    private var service: OAuth10aService

    private var accessToken: OAuth1AccessToken

    private val baseUrl = "https://api.twitter.com/1.1/search/tweets.json"

    private val apiKey: String = info.aiavci.twitterfeedapp.BuildConfig.api_key
    private val apiSecret: String = info.aiavci.twitterfeedapp.BuildConfig.api_secret
    private val token: String = info.aiavci.twitterfeedapp.BuildConfig.token
    private val tokenSecret: String = info.aiavci.twitterfeedapp.BuildConfig.token_secret

    init {
        service = ServiceBuilder(apiKey)
            .apiSecret(apiSecret)
            .build(TwitterApi.instance())

        accessToken = OAuth1AccessToken(
            token,
            tokenSecret
        )
    }

    fun fetchTweets(searchText: String = ""): List<Tweet> {
        val encodedUrl = URLEncoder.encode(searchText, "utf-8")
        val searchUrl = "$baseUrl?q=$encodedUrl"

        return performFetch(searchUrl)
    }

    fun fetchTweets(latitude: Double, longtitude: Double, radius: Int): List<Tweet> {
        val geoCode = "$latitude,$longtitude,${radius}km"

        val searchUrl = "$baseUrl?geocode=$geoCode&count=25"

        return performFetch(searchUrl)
    }

    private fun performFetch(searchUrl: String): List<Tweet> {
        val request = OAuthRequest(Verb.GET, searchUrl)

        service.signRequest(accessToken, request) // the access token from step 4

        val response = service.execute(request)

        return getTweetsFromResponse(response)
    }

    private fun getTweetsFromResponse(response: Response): List<Tweet> {
        val responseObject = JSONObject(response.body)

        val statuses: JSONArray = responseObject.optJSONArray("statuses")

        Log.d("data", "Response data size is " + statuses.length())

        return statuses
            .let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
            .map {
                Tweet(it.toString())
            } // transforms each JSONObject of the array into Foo
    }
}