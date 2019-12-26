package tk.kvakva.beeoptions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import tk.kvakva.beeoptions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private val puvm by viewModels<PassUserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        binding.pu=puvm
        //setContentView(R.layout.activity_main)
        binding.lifecycleOwner=this
        val adaptr = OptionRecyViewAdap()
        binding.RecyViOp.adapter=adaptr

        puvm.data.observe(this, Observer {
            it?.let {
                adaptr.data=it
                title=getString(R.string.app_name) + " " + it.size.toString()
            }
        })
    }

    fun clka(view: View) {
        Log.d("M_MainActivity", "${view.tag}")
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        Log.d("M_MainActivity", "!!!!!!!!!!!!!!!!!!!!! $masterKeyAlias !!!!!!!!!!!!!!!")

        val sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs", masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val sp = sharedPreferences.all
        sp.keys.forEach {
            Log.d("M_MainActivity", "$it -> ${sp[it]}")
        }


    }

    fun enterpassword(item: MenuItem) {
        Log.d("M_MainActivity","un enterpassword(item: MenuItem) $item : ${item.itemId} ${R.menu.bomenu}")
        when(item.itemId){
            R.id.passwrd -> {
                Log.d("M_MainActivity"," R.menu.bomenu -> {")
                puvm.setShowPassUserLayout()
            }
            R.id.getOptFromBeeline -> {
                puvm.onClinkGetBT()
            }
        }
    }


    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [.onPrepareOptionsMenu].
     *
     *
     * The default implementation populates the menu with standard system
     * menu items.  These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     *
     * When you add items to the menu, you can implement the Activity's
     * [.onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
                menuInflater.inflate(R.menu.bomenu,menu)
        return super.onCreateOptionsMenu(menu)

    }
}
