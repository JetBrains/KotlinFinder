package Views

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.SpriteKit.*


class SpotDistanceView: SKView {

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame) {


    }

    fun setDistance(distance: Float) {
        /* distance in [0.0, 1.0] */

    }
}