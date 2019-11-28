package com.icerockdev.jetfinder.feature.spotSearch

import androidx.lifecycle.ViewModelProvider
import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.github.aakira.napier.Napier
import com.icerockdev.jetfinder.feature.spotSearch.databinding.ActivitySpotSearchBinding
import android.os.Vibrator


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
            val maxDistance = 100
            val distance = (distance ?: 0) / maxDistance.toFloat()
            binding.spotSearch.distance = distance

            val vibrator = getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(100L,100L,100L)
            vibrator.vibrate(pattern, -1)
        }

        viewModel.isSearchMode.addObserver { searchMode: Boolean ->
            if (searchMode) {
                binding.statusLabel.setText(R.string.searching)
                binding.searchSuccess.visibility = View.GONE
                binding.spotSearch.visibility = View.VISIBLE
            } else {
                binding.statusLabel.setText(R.string.spot_found)
                binding.searchSuccess.visibility = View.VISIBLE
                binding.spotSearch.visibility = View.GONE
            }
        }

        viewModel.hintText.addObserver { text: String ->
            binding.hint.text = text
        }
    }
}