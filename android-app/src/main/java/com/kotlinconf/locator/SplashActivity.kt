package com.kotlinconf.locator

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.kotlinconf.library.feature.mainMap.MainMapDependencies
import com.kotlinconf.library.feature.mainMap.MapActivity

import com.kotlinconf.library.feature.mainMap.presentation.SplashViewModel
import com.kotlinconf.library.feature.mainMap.utils.alert
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import com.kotlinconf.locator.databinding.ActivitySplashBinding

class SplashActivity :
    MvvmEventsActivity<ActivitySplashBinding, SplashViewModel, SplashViewModel.EventsListener>(),
    SplashViewModel.EventsListener {
    override val layoutId: Int = R.layout.activity_splash
    override val viewModelClass: Class<SplashViewModel> = SplashViewModel::class.java
    override val viewModelVariableId: Int = BR.viewModel

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        MainMapDependencies.factory.createSplashViewModel(
            eventsDispatcher = eventsDispatcherOnMain()
        )
    }

    override fun showError(error: Throwable, retryingAction: (() -> Unit)?) {
        alert(
            message = error.message ?: getString(R.string.retry),
            cancelable = false,
            closable = false,
            positiveAction = retryingAction?.let { R.string.retry to retryingAction })
    }

    override fun routeToMainscreen() {
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }
}