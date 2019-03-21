package eu.slicky.pomesljajcek.ui.activity.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.databinding.ActivityMainBinding
import eu.slicky.pomesljajcek.ui.activity.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val backgrounds = arrayOf(
        R.drawable.background_0,
        R.drawable.background_1,
        R.drawable.background_2,
        R.drawable.background_3,
        R.drawable.background_4
    )

    /* OVERRIDE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    /* BODY */

    fun changeBackground(index: Int? = null) {
        Picasso.get()
            .load(index?.let { backgrounds.getOrNull(it) } ?: backgrounds.random())
            .fit()
            .centerCrop()
            .into(binding.backgroundImage)
    }

}
