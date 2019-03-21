package eu.slicky.pomesljajcek.ui.activity

import androidx.appcompat.app.AppCompatActivity
import eu.slicky.pomesljajcek.App
import eu.slicky.pomesljajcek.data.DataHandler

abstract class BaseActivity : AppCompatActivity() {

    val dataHandler: DataHandler get() = (application as App).dataHandler

}
