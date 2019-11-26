package common

import platform.CoreGraphics.CGFloat
import platform.Foundation.NSTimer
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.posix.round


class FeedbackGenerator {
    private var timer: NSTimer? = null
    private val generator: UIImpactFeedbackGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    private val vibrationsInterval: Float = 0.2f
    private val vibrationTimes: List<Int> = listOf(
        1,
        2,
        3,
        4
    )

    init {
        this.generator.prepare()
    }

    fun feedback(intensity: CGFloat) {
        if (intensity <= 0.0)
            return

        var idx: Int = round(intensity * this.vibrationTimes.count()).toInt()

        if (idx >= this.vibrationTimes.count())
            idx = this.vibrationTimes.count() - 1

        this.vibrate(this.vibrationTimes[idx])
    }

    private fun vibrate(times: Int) {
        var vibratedTimes: Int = 0

        this.timer?.invalidate()

        this.timer = NSTimer.scheduledTimerWithTimeInterval(
            interval = this.vibrationsInterval.toDouble(),
            repeats = true,
            block = { timer: NSTimer? ->
                vibratedTimes++

                this.generator.impactOccurred()

                if (vibratedTimes == times)
                    timer?.invalidate()
            }
        )
    }
}