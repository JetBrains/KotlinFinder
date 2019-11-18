package common

import platform.CoreGraphics.CGFloat
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.posix.round
import kotlin.math.min


class FeedbackGenerator {
    private val generator: UIImpactFeedbackGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)

    init {
        this.generator.prepare()
    }

    fun feedback(intensity: CGFloat) {
        if (intensity < 0.0)
            return

        this.generator.impactOccurredWithIntensity(intensity * 1000.0)
    }
}