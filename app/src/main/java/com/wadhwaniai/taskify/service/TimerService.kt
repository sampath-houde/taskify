package com.wadhwaniai.taskify.service

import android.app.PendingIntent
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.data.repo.PreferencesRepo
import com.wadhwaniai.taskify.data.repo.TaskRepo
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : LifecycleService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    lateinit var timer: CountDownTimer

    @Inject
    lateinit var taskRepo: TaskRepo

    @Inject
    lateinit var preferencesRepo: PreferencesRepo

    lateinit var task: TaskEntity

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Timber.d("Service started ${this.hashCode()}")
        val duration = intent!!.getLongExtra(DURATION, 0L)
        task = intent.serializable(TASK)!!
        val pendingIntent = getPendingIntent(task)
        ServiceUtil.timerState.postValue(TimerState.START)
        timer = object : CountDownTimer(duration, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.d(millisUntilFinished.toString())
                val timerText = millisUntilFinished.formatDuration("%d:%02d:%02d left")
                ServiceUtil.timeLeft.postValue(millisUntilFinished)
                notificationHelper.showSilentNotification(timerText, task.task_title, pendingIntent)
            }

            override fun onFinish() {
                notificationHelper.showCompletedNotification(
                    "Completed task",
                    task.task_title,
                    pendingIntent
                )
                finishTask(task)
                ServiceUtil.timerState.postValue(TimerState.STOP)
                ServiceUtil.timeLeft.postValue(duration)
            }
        }
        timer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }


    private fun finishTask(task: TaskEntity) = lifecycleScope.launchWhenStarted {
        task.timeLeft = 0
        task.state = TaskState.COMPLETED
        taskRepo.updateTask(task)
        preferencesRepo.setServiceRunning(false)
        stopSelf()
    }

    private fun getPendingIntent(task: TaskEntity): PendingIntent {
        val intent = Intent(this@TimerService, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(GO_TO_TIMER, true)
        intent.putExtra(TASK, task)
        return PendingIntent.getActivity(applicationContext, FROM_NOTIFICATION, intent, 0)
    }
}