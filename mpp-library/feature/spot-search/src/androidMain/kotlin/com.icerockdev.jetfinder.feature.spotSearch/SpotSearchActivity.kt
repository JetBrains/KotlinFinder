package com.icerockdev.jetfinder.feature.spotSearch

import androidx.lifecycle.ViewModelProvider
import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import android.Manifest
import android.os.Bundle
import android.os.PersistableBundle
import com.github.aakira.napier.Napier
import com.icerockdev.jetfinder.feature.spotSearch.databinding.ActivitySpotSearchBinding
import com.icerockdev.jetfinder.feature.spotSearch.BR.*

class SpotSearchActivity : MvvmActivity<ActivitySpotSearchBinding, SpotSearchViewModel>() {
    override val layoutId: Int = R.layout.activity_spot_search
    override val viewModelClass: Class<SpotSearchViewModel> = SpotSearchViewModel::class.java
    override val viewModelVariableId: Int = BR.viewModel

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        SpotSearchDependencies.factory.createSpotSearchViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.nearestBeaconDistance.addObserver { distance: Int? ->
            Napier.d("distance: $distance")
            val maxDistance: Int = 100

            binding.spotSearch.update((distance ?: 0) / maxDistance.toFloat())

            //this.feedbackGenerator.feedback(this.spotSearchScene.distance.toDouble())
        }
    }
}