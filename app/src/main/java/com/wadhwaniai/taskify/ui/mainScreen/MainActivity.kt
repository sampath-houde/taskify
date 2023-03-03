package com.wadhwaniai.taskify.ui.mainScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.databinding.ActivityMainBinding
import com.wadhwaniai.taskify.databinding.DrawerMenuBinding
import com.wadhwaniai.taskify.service.TimerService
import com.wadhwaniai.taskify.ui.ErrorDialogFragment
import com.wadhwaniai.taskify.ui.auth.AuthActivity
import com.wadhwaniai.taskify.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostController = supportFragmentManager.findFragmentById(R.id.fragment2) as NavHostFragment
        navController = navHostController.navController

        viewModel.fetchAllTasks()

        initializeDrawerLayout()
        setSupportActionBar(binding.activityMainContent.toolbar)

        binding.activityMainContent.menuIcon.setOnClickListener {
            binding.drawer.openDrawer()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Timber.d("New dest ${destination.id}")
            binding.drawer.closeDrawer()
            setNavButtons()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.runningTask.collect {
                Timber.d("Running task $it")
                if (it.isNotEmpty()) {
                    startService(it[0])
                } else {
                    stopService()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.shouldLogout.collect {
                if (it)
                    goBackToAuth()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.taskFetchState.collect {
                binding.root.isEnabled = it !is Resource.Loading
                when (it) {
                    is Resource.Empty -> Unit
                    is Resource.Error -> {
                        it.errorType?.let { error ->
                            showErrorDialog(error)
                        } ?: showToast(it.message)
                    }
                    is Resource.Loading -> Unit
                    is Resource.Success -> Unit
                }
            }
        }
    }

    private fun setNavButtons() {
        val openDrawerView = DrawerMenuBinding.bind(binding.drawer.menuView)
        val currentFragment = navController.currentDestination?.id ?: 0
        Timber.d("Current $currentFragment")
        openDrawerView.apply {
            homeItem.setUsable(currentFragment, TopLevelScreens.HOME)
            profileItem.setUsable(currentFragment, TopLevelScreens.PROFILE)
            aboutItem.setUsable(currentFragment, TopLevelScreens.ABOUt)
            statsItem.setUsable(currentFragment, TopLevelScreens.STATS)
        }
    }

    private fun startService(task: TaskEntity) {
        Timber.d("StartService ${viewModel.isServiceRunning()}")
        if (!viewModel.isServiceRunning()) {
            Timber.d("Starting service")
            Intent(this, TimerService::class.java).also {
                it.putExtra(TASK, task)
                it.putExtra(DURATION, task.timeLeft)
                startService(it)
            }
            viewModel.saveServiceStarted()
        }
    }

    private fun stopService() {
        Timber.d("StopService ${viewModel.isServiceRunning()}")
        if (viewModel.isServiceRunning()) {
            Intent(this, TimerService::class.java).also {
                stopService(it)
            }
            viewModel.saveServiceStopped()
        }
    }

    private fun handleNavigation(screen: TopLevelScreens) {
        if (navController.currentDestination?.id == screen.fragmentId)
            return
        val navigationId = when (screen) {
            TopLevelScreens.HOME -> R.id.homeFragment
            TopLevelScreens.PROFILE -> R.id.profileFragment
            TopLevelScreens.STATS -> R.id.statsFragment
            TopLevelScreens.ABOUt -> R.id.aboutFragment
            TopLevelScreens.TIMER -> R.id.timerFragment
        }
        Timber.d(navigationId.toString())
        if (screen != TopLevelScreens.TIMER)
            navController.popBackStack()
        navController.navigate(navigationId)
    }

    fun showTimer(stopWatchFor: StopWatchFor, task: TaskEntity) {
        viewModel.task = task
        viewModel.stopWatchFor = stopWatchFor
        handleNavigation(TopLevelScreens.TIMER)
    }

    private fun goBackToAuth() {
        Intent(this, AuthActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    fun showErrorDialog(errorTYpe: ErrorTYpe) {
        ErrorDialogFragment(errorTYpe).show(supportFragmentManager, SHOW_ERROR_DIALOG)
    }

    private fun initializeDrawerLayout() {

        val drawerBinding = DrawerMenuBinding.bind(binding.drawer.menuView)
        drawerBinding.apply {
            //userNameText.text = viewModel.user?.username
            userEmailText.text = viewModel.user?.email
            //avatarImage.setProfileImage(viewModel.user?.profileImage ?: "")
            homeItem.setOnClickListener {
                handleNavigation(TopLevelScreens.HOME)
            }
            profileItem.setOnClickListener {
                handleNavigation(TopLevelScreens.PROFILE)
            }
            aboutItem.setOnClickListener {
                handleNavigation(TopLevelScreens.ABOUt)
            }
            statsItem.setOnClickListener {
                handleNavigation(TopLevelScreens.STATS)
            }
            logoutBtn.setOnClickListener {
                viewModel.onLogoutPressed()
            }
        }
    }
}