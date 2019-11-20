/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.app

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.icerockdev.jetfinder.feature.mainMap.MainMapDependencies
import com.icerockdev.jetfinder.feature.mainMap.MapActivity

import com.icerockdev.jetfinder.feature.mainMap.presentation.SplashViewModel
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import org.example.app.databinding.ActivitySplashBinding

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
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        //todo fix and delete
        routeToMainscreen()
    }

    override fun routeToMainscreen() {
        startActivity(Intent(this, MapActivity::class.java))
    }
}