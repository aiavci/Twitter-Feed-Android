package info.aiavci.twitterfeedapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Base activity extended by other activities to allow for generic features
 */
open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}