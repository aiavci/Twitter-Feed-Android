package info.aiavci.twitterfeedapp

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.SeekBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    private val twitterManager: TwitterManager = TwitterManager()

    private val tweetMap = mutableMapOf<Marker, Tweet>()

    private var radius = 5

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        radiusDisplay.text = "${radius}km"

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                map.addCircle(CircleOptions()
                    .center(LatLng(lastLocation.latitude, lastLocation.longitude))
                    .radius(radius.toDouble() * 1000)
                    .strokeWidth(0f)
                    .fillColor(0x550000FF))

                populateTweets()
            }

            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                radius = progress

                radiusDisplay.text = "${radius}km"

                map.clear()
            }
        })
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val tweet = tweetMap[marker] ?: return false

        val tweetFragment = TweetFragment()

        val bundle = Bundle().apply {
            putString("name", tweet.name)
            putString("tweet", tweet.text)
            putString("id", tweet.id)
            putString("url", tweet.profileUrl)
        }

        tweetFragment.arguments = bundle

        tweetFragment.show(supportFragmentManager, "tweet_details")

        return true
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // 1
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location == null) return@addOnSuccessListener

            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))

            populateTweets()

            map.addCircle(CircleOptions()
                .center(LatLng(lastLocation.latitude, lastLocation.longitude))
                .radius(radius.toDouble() * 1000)
                .strokeWidth(0f)
                .fillColor(0x550000FF))
        }
    }

    private fun populateTweets() {
        GlobalScope.launch {
            val tweets = twitterManager.fetchTweets(lastLocation.latitude, lastLocation.longitude, radius)

            tweets.forEach { tweet ->
                if (tweet.placeName !== null) {
                    val geoCoder = Geocoder(this@MapsActivity)

                    val address = geoCoder.getFromLocationName(tweet.placeName , 1).first()

                    val addressAsLocation = Location("Address"). apply {
                        latitude = address.latitude
                        longitude = address.longitude
                    }

                    if (lastLocation.distanceTo(addressAsLocation) < radius * 1000) {
                        // Set marker if place is within radius
                        drawTweetMarker(address.latitude, address.longitude, tweet)
                    }

                } else if (tweet.coordinates !== null && tweet.coordinates !== "null") {
                    val coordinates = tweet.coordinates.split(",")

                    drawTweetMarker(coordinates[0].toDouble(), coordinates[1].toDouble(), tweet)
                }
            }
        }
    }

    /**
     * Draw tweet and save to [tweetMap]
     */
    private fun drawTweetMarker(latitude: Double, Longtitude: Double, tweet: Tweet) {
        runOnUiThread {
            val newMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(latitude, Longtitude))
                    .title(tweet.name)
            )

            tweetMap[newMarker] = tweet
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener(this@MapsActivity)
        }

        setUpMap()
    }
}
