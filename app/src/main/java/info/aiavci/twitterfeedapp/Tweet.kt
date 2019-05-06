package info.aiavci.twitterfeedapp

import org.json.JSONObject

class Tweet(json: String) : JSONObject(json) {
    val name: String = this.optJSONObject("user").getString("name")
    val screenName: String = this.optJSONObject("user").getString("screen_name")
    val text: String = this.optString("text")
    val geo: String = this.optString("geo")
    val coordinates: String? = this.optString("coordinates")
    val placeName: String? = this.optJSONObject("place")?.optString("full_name")

    val id: String = this.optString("id")

    val profileUrl: String = this.optJSONObject("user").getString("profile_image_url")
}