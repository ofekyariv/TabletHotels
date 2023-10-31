package ofek.yariv.tablethotels.view.activities.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.databinding.ActivityMainBinding
import ofek.yariv.tablethotels.utils.*
import ofek.yariv.tablethotels.utils.Constants.TERMS_OR_PRIVACY
import ofek.yariv.tablethotels.utils.ReportConstants.PRESSED_WITHOUT_SUBSCRIPTION
import ofek.yariv.tablethotels.utils.ReportConstants.SUBSCRIPTION
import ofek.yariv.tablethotels.utils.SharedPreferencesKeys.DO_NOT_SHOW_AGAIN_TERMS
import ofek.yariv.tablethotels.utils.managers.*
import ofek.yariv.tablethotels.view.activities.termsAndPrivacy.TermsAndPrivacy
import ofek.yariv.tablethotels.view.activities.termsAndPrivacy.TermsAndPrivacyActivity
import ofek.yariv.tablethotels.view.dialogs.BuySubscriptionDialog
import ofek.yariv.tablethotels.view.fragments.home.HomeFragment
import ofek.yariv.tablethotels.view.fragments.hotels.HotelsFragment
import ofek.yariv.tablethotels.view.fragments.care_plan.CarePlanFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModel()
    private val languageManager: LanguageManager by inject { parametersOf(this) }
    private val updateManager: UpdateManager by inject { parametersOf(this) }
    private val menuItemClickHelper: MenuItemClickHelper by inject { parametersOf(this) }
    private val buySubscriptionDialog: BuySubscriptionDialog by inject { parametersOf(this) }
    private val permissionsManager: PermissionsManager by inject { parametersOf(this) }
    private val themeManager: ThemeManager by inject { parametersOf(this) }
    private val locationManager: LocationManager by inject()
    private val analyticsManager: AnalyticsManager by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val billingManager: BillingManager by inject()
    private val editor = sharedPreferences.edit()

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.close()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            //updateManager.checkForUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var loading = true
        lifecycleScope.launch {
            if (permissionsManager.hasLocationPermission()) {
                locationManager.getCurrentLocation(maxDuration = 3_000L)
            }
            loading = false
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                loading
            }
        }
        val savedTheme = themeManager.getSavedTheme()
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        languageManager.setSavedLanguageOrDefault()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiateLayout()

        analyticsManager.report(
            "${ReportConstants.MAIN_ACTIVITY} ${ReportConstants.SHOWN}",
            ReportConstants.SHOWN
        )
    }

    private fun initiateLayout() {
        lifecycleScope.launch {
            billingManager.isSubscribed.collect { isSubscribed ->
                if (isSubscribed) {
                    //todo: the user is subscribed
                }
            }
        }

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.hotelFragment -> {
                    replaceFragment(HotelsFragment())
                    true
                }

                R.id.homeFragment -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.carePlanFragment -> {
                    if (!billingManager.isSubscribed.value) {
                        analyticsManager.report(
                            PRESSED_WITHOUT_SUBSCRIPTION,
                            SUBSCRIPTION
                        )
                        buySubscriptionDialog.show()
                        false
                    } else {
                        replaceFragment(CarePlanFragment())
                        true
                    }
                }

                else -> false
            }
        }
        binding.btnMenu.setOnClickListener { binding.drawerLayout.open() }
        binding.sideNavigationView.setNavigationItemSelectedListener { item: MenuItem ->
            menuItemClickHelper.menuItemClickById(item.itemId)
        }
        openTermsDialogIfNotYetAccepted()
    }

    private fun replaceFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainHostFragment)
        if (currentFragment?.javaClass == fragment.javaClass) {
            return
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            when (fragment) {
                is HotelsFragment -> R.anim.slide_in_left
                is HomeFragment -> {
                    when (currentFragment) {
                        is HotelsFragment -> R.anim.slide_in_right
                        is CarePlanFragment -> R.anim.slide_in_left
                        else -> R.anim.slide_in_right
                    }
                }

                is CarePlanFragment -> R.anim.slide_in_right
                else -> R.anim.slide_in_left
            },
            when (fragment) {
                is HotelsFragment -> R.anim.slide_out_right
                is HomeFragment -> {
                    when (currentFragment) {
                        is HotelsFragment -> R.anim.slide_out_left
                        is CarePlanFragment -> R.anim.slide_out_right
                        else -> R.anim.slide_out_left
                    }
                }

                is CarePlanFragment -> R.anim.slide_out_left
                else -> R.anim.slide_out_right
            }
        )
        fragmentTransaction.replace(R.id.mainHostFragment, fragment)
        if (currentFragment is HomeFragment) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    fun setBottomNavigationItemChecked(itemId: Int) {
        binding.bottomNavigationView.menu.findItem(itemId).isChecked = true
    }

    private fun openTermsDialogIfNotYetAccepted() {
        if (!sharedPreferences.getBoolean(DO_NOT_SHOW_AGAIN_TERMS, false)) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.terms_and_conditions)
                .setMessage(R.string.terms_and_condition_read_needed)
                .setNegativeButton(R.string.terms_and_conditions) { _, _ ->
                    this.startActivity(
                        Intent(
                            this,
                            TermsAndPrivacyActivity::class.java
                        ).putExtra(TERMS_OR_PRIVACY, TermsAndPrivacy.TERMS.value)
                    )
                }
                .setPositiveButton(R.string.i_have_read_and_accept_terms) { dialogInterface, _ ->
                    editor.putBoolean(DO_NOT_SHOW_AGAIN_TERMS, true)
                    editor.apply()
                    dialogInterface.dismiss()
                }.setCancelable(false).show()
        }
    }
}