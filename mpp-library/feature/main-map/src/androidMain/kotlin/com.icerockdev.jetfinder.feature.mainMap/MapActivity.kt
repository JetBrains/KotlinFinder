package com.icerockdev.jetfinder.feature.mainMap

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.icerockdev.jetfinder.feature.mainMap.databinding.ActivityMapBinding
import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import com.icerockdev.shared.utils.alert
import com.icerockdev.shared.utils.alertInputText
import com.icerockdev.shared.utils.alertRetry
import com.icerockdev.shared.utils.alertYesOrNo
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import dev.icerock.moko.permissions.PermissionsController


class MapActivity :
    MvvmEventsActivity<ActivityMapBinding, MapViewModel, MapViewModel.EventsListener>(),
    MapViewModel.EventsListener {
    override val layoutId: Int = R.layout.activity_map
    override val viewModelClass: Class<MapViewModel> = MapViewModel::class.java
    override val viewModelVariableId: Int = BR.viewModel

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        MainMapDependencies.factory.createMapViewModel(
            eventsDispatcher = eventsDispatcherOnMain(),
            permissionsController = PermissionsController(applicationContext = applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.permissionsController.bind(lifecycle, supportFragmentManager)
        viewModel.requestPermissions()

        binding.statusView.setOnLongClickListener {
            this.alertYesOrNo(getString(R.string.reset)) { viewModel.resetCookiesButtonTapped() }
            false
        }

        viewModel.currentStep.ld().observe(this, Observer { step ->
            stages.getOrNull(step)?.let {
                binding.statusButton.setImageResource(it)
                binding.imageView.setImageResource(it)
                binding.labelText.setTwoColoredText(step)
            }
        })

        viewModel.findTaskButtonState.ld()
            .observe(this, Observer { state: MapViewModel.FindTaskButtonState ->
                // TODO move to common code - it duplicated between platforms
                when (state) {
                    MapViewModel.FindTaskButtonState.ACTIVE -> {
                        binding.findTaskButton.isEnabled = true
                        binding.findTaskButton.text = getString(R.string.find_task)
                    }

                    MapViewModel.FindTaskButtonState.TOO_FAR -> {
                        binding.findTaskButton.isEnabled = false
                        binding.findTaskButton.text = getString(R.string.farTaskPoint)
                    }

                    MapViewModel.FindTaskButtonState.COMPLETED -> {
                        binding.findTaskButton.isEnabled = false
                        binding.findTaskButton.text = getString(R.string.completed)
                    }
                }
            })
        val bottomSheet: View = findViewById(R.id.statusView)
        val statusView = BottomSheetBehavior.from(bottomSheet)
        statusView.state = BottomSheetBehavior.STATE_EXPANDED
        statusView.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.statusButton.apply {
                            visibility = View.VISIBLE
                            alpha = 0f
                            translationY = height.toFloat()
                            animate()
                                .translationY(0f)
                                .alpha(1f)
                                .duration = 200L
                        }
                    }
                    else -> {
                    }
                }
            }

        })


        binding.statusButton.setOnClickListener {
            statusView.state = BottomSheetBehavior.STATE_EXPANDED
            binding.statusButton.visibility = View.GONE
        }

        binding.map.setImageResource(R.drawable.map)
        binding.map.post { binding.map.scale = 3f }
        binding.map.setOnViewDragListener { dx, dy ->
            if (statusView.state == BottomSheetBehavior.STATE_EXPANDED) {
                statusView.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private val stages = mutableListOf(
        R.drawable.stage0,
        R.drawable.stage1,
        R.drawable.stage2,
        R.drawable.stage3,
        R.drawable.stage4,
        R.drawable.stage5,
        R.drawable.stage6
    )

    override fun routeToSpotSearchScreen() {
        MainMapDependencies.router.routeToSpotSearch(this)
    }

    override fun showResetCookiesAlert(resetAction: () -> Unit) {
        resetAction()
    }

    override fun showEnterNameAlert() {
        alertInputText(
            getString(R.string.enterName),
            action = { name: String -> viewModel.sendWinnerName(name) })
    }

    override fun showHint(hint: String) {
        alert(hint, {}, false)
    }

    override fun showRegistrationMessage(message: String) {
        alert(message, {}, false)
    }

    override fun showError(error: Throwable, retryingAction: (() -> Unit)?) {
        alertRetry(error.message ?: "UnknownError", action = retryingAction)
    }

    private fun TextView.setTwoColoredText(count: Int) {
        val text = this.text.toString()
        val firstText = SpannableString(text.take(count))
        val lastLength = text.length - count
        val lastText = SpannableString(text.takeLast(if (lastLength > 0) lastLength else 0))
        firstText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.textGrayDark)),
            0,
            firstText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        lastText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.textGrayLight)),
            0,
            lastText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setText(firstText)
        append(lastText)
    }
}

