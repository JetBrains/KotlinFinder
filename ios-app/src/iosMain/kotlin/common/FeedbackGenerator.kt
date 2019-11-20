package common

import platform.CoreGraphics.CGFloat
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.posix.round


class FeedbackGenerator {
    private val generators: List<UIImpactFeedbackGenerator> = listOf(
        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight),
        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium),
        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    )

    init {
        for (g: UIImpactFeedbackGenerator in this.generators)
            g.prepare()
    }

    fun feedback(intensity: CGFloat) {
        if (intensity <= 0.0)
            return

        var idx: Int = round(intensity * this.generators.count()).toInt()

        if (idx >= this.generators.count())
            idx = this.generators.count() - 1

        this.generators[idx].impactOccurred()
    }
}