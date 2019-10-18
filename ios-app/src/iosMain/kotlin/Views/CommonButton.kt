package Views

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.Foundation.NSMutableAttributedString
import platform.UIKit.*

class CommonButton: UIButton {
    enum class Style {
        ORANGE,
        GRAY
    }

    private var style: Style = Style.ORANGE

    @OverrideInit
    constructor(coder: NSCoder): super(coder)

    @OverrideInit
    constructor(frame: CValue<CGRect>): super(frame) {
        this.layer.cornerRadius = 16.0
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        this.setStyle(this.style)
    }

    fun setStyle(style: Style) {
        this.style = style

        this.setBackgroundColor(this.backgroundColorForStyle(this.style, this.isEnabled()))
        this.setTitleColor(UIColor.whiteColor(), forState = 0u)
        this.titleLabel?.font = this.titleFontForStyle(style)
    }

    private fun backgroundColorForStyle(style: Style, enabled: Boolean): UIColor? {
        when (style) {
            Style.ORANGE ->
                if (enabled)
                    return UIColor.colorNamed("orangeColor")
                else
                    return UIColor.colorNamed("orangeInactiveColor")
            Style.GRAY ->
                return UIColor.colorNamed("grayColor")
        }
    }

    private fun titleFontForStyle(style: Style): UIFont {
        when (style) {
            Style.ORANGE -> return UIFont.boldSystemFontOfSize(20.0)
            Style.GRAY -> return UIFont.systemFontOfSize(14.0)
        }
    }
}