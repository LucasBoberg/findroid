package dev.jdtech.jellyfin

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManager
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import dev.jdtech.jellyfin.api.JellyfinApi
import dev.jdtech.jellyfin.database.ServerDatabaseDao
import dev.jdtech.jellyfin.databinding.ActivityMainBinding
import dev.jdtech.jellyfin.viewmodels.MainViewModel
import dev.jdtech.jellyfin.work.SyncWorker
import javax.inject.Inject
import dev.jdtech.jellyfin.core.R as CoreR

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    private var castSession: CastSession? = null
    private lateinit var sessionManager: SessionManager
    private val sessionManagerListener: SessionManagerListener<CastSession> =
        SessionManagerListenerImpl(this)

    @Inject
    lateinit var database: ServerDatabaseDao

    @Inject
    lateinit var appPreferences: AppPreferences

    private lateinit var navController: NavController

    @Inject
    lateinit var jellyfinApi: JellyfinApi

    @OptIn(NavigationUiSaveStateControl::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleUserDataSync()
        applyTheme()
        setupActivity()
    }

    @OptIn(NavigationUiSaveStateControl::class)
    private fun setupActivity() {
        sessionManager = CastContext.getSharedInstance(this).sessionManager

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.app_navigation)

        checkServersEmpty(graph) {
            navController.setGraph(graph, intent.extras)
        }
        checkUser(graph) {
            navController.setGraph(graph, intent.extras)
        }

        val navView: NavigationBarView = binding.navView as NavigationBarView

        if (appPreferences.offlineMode) {
            navView.menu.clear()
            navView.inflateMenu(CoreR.menu.bottom_nav_menu_offline)
        }

        setSupportActionBar(binding.mainToolbar)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.mediaFragment,
                R.id.favoriteFragment,
                R.id.downloadsFragment,
            ),
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        // navView.setupWithNavController(navController)
        // Don't save the state of other main navigation items, only this experimental function allows turning off this behavior
        NavigationUI.setupWithNavController(navView, navController, false)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navView.visibility = when (destination.id) {
                R.id.twoPaneSettingsFragment, R.id.serverSelectFragment, R.id.addServerFragment, R.id.loginFragment, com.mikepenz.aboutlibraries.R.id.about_libraries_dest, R.id.usersFragment, R.id.serverAddressesFragment -> View.GONE
                else -> View.VISIBLE
            }
            if (destination.id == com.mikepenz.aboutlibraries.R.id.about_libraries_dest) {
                binding.mainToolbar.title =
                    getString(CoreR.string.app_info)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        castSession = sessionManager.currentCastSession
        sessionManager.addSessionManagerListener(sessionManagerListener, CastSession::class.java)
    }

    override fun onPause() {
        super.onPause()
        sessionManager.removeSessionManagerListener(sessionManagerListener, CastSession::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun checkServersEmpty(graph: NavGraph, onServersEmpty: () -> Unit = {}) {
        if (!viewModel.startDestinationChanged) {
            val numOfServers = database.getServersCount()
            if (numOfServers < 1) {
                graph.setStartDestination(R.id.addServerFragment)
                viewModel.startDestinationChanged = true
                onServersEmpty()
            }
        }
    }

    private fun checkUser(graph: NavGraph, onNoUser: () -> Unit = {}) {
        if (!viewModel.startDestinationChanged) {
            appPreferences.currentServer?.let {
                val currentUser = database.getServerCurrentUser(it)
                if (currentUser == null) {
                    graph.setStartDestination(R.id.serverSelectFragment)
                    viewModel.startDestinationChanged = true
                    onNoUser()
                }
            }
        }
    }
    companion object {
        private class SessionManagerListenerImpl(private val mainActivity: MainActivity) :
            SessionManagerListener<CastSession> {
            override fun onSessionStarted(session: CastSession, sessionId: String) {
                mainActivity.invalidateOptionsMenu()
//                val thing =
//                    "{\"options\":{},\"command\":\"Identify\",\"userId\":\"${mainActivity.jellyfinApi.userId}\",\"deviceId\":\"${mainActivity.jellyfinApi.api.deviceInfo.id}\",\"accessToken\":\"${mainActivity.jellyfinApi.api.accessToken}\",\"serverAddress\":\"${mainActivity.jellyfinApi.api.baseUrl}\",\"serverId\":\"\",\"serverVersion\":\"\",\"receiverName\":\"\"}"
//                session.sendMessage("urn:x-cast:com.connectsdk", thing)
//                session.setMessageReceivedCallbacks(
//                    "urn:x-cast:com.connectsdk"
//                ) { _, _, message -> Timber.i(message) }
            }

            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                mainActivity.invalidateOptionsMenu()
            }

            override fun onSessionEnded(session: CastSession, error: Int) {
                //            finish()
            }

            override fun onSessionEnding(p0: CastSession) {
            }

            override fun onSessionResumeFailed(p0: CastSession, p1: Int) {
            }

            override fun onSessionResuming(p0: CastSession, p1: String) {
            }

            override fun onSessionStartFailed(p0: CastSession, p1: Int) {
            }

            override fun onSessionStarting(p0: CastSession) {
            }

            override fun onSessionSuspended(p0: CastSession, p1: Int) {
            }
        }
    }

    private fun scheduleUserDataSync() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED,
                    )
                    .build(),
            )
            .build()

        val workManager = WorkManager.getInstance(applicationContext)

        workManager.beginUniqueWork("syncUserData", ExistingWorkPolicy.KEEP, syncWorkRequest)
            .enqueue()
    }

    private fun applyTheme() {
        if (appPreferences.amoledTheme) {
            setTheme(CoreR.style.Theme_FindroidAMOLED)
        }
    }
}
