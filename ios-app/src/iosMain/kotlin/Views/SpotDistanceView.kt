package views

import com.kotlinconf.library.feature.mainMap.presentation.MapViewModel
import common.fillSuperview
import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.SpriteKit.SKSceneScaleMode
import platform.SpriteKit.SKView
import platform.UIKit.*


class SpotDistanceView: UIView {
    private val backgroundImageView: UIImageView = UIImageView(null)
    private val animationView: SKView = SKView()
    private val animationScene: SpotDistanceScene = SpotDistanceScene()

    private val backgroundImage: UIImage = UIImage.imageNamed("spotSearchBackground")!!
    private val spotDiscoveredImage: UIImage = UIImage.imageNamed("spotDiscoveredBackground")!!
    private val noTasksImage: UIImage = UIImage.imageNamed("noTasksBackground")!!

    private var isSpotDiscovered: Boolean = false

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame) {
        this.addSubview(this.backgroundImageView)
        this.backgroundImageView.fillSuperview()

        this.backgroundImageView.setImage(this.backgroundImage)

        this.addSubview(this.animationView)
        this.animationView.fillSuperview()

        this.animationScene.scaleMode = SKSceneScaleMode.SKSceneScaleModeResizeFill
        this.animationView.presentScene(this.animationScene)
        this.animationView.setBackgroundColor(UIColor.clearColor)

        this.setDistance(null)
        this.setSpotDiscovered(false)
    }

    fun setState(state: MapViewModel.SearchViewState) {
        when (state) {
            is MapViewModel.SearchViewState.NoTask -> {
                this.setSpotDiscovered(false)
                this.setDistance(null)
            }

            is MapViewModel.SearchViewState.Distance -> {
                this.setSpotDiscovered(false)
                this.setDistance(state.distance)
            }

            is MapViewModel.SearchViewState.Discovered -> {
                this.setSpotDiscovered(true)
            }
        }
    }

    private fun setSpotDiscovered(discovered: Boolean) {
        this.isSpotDiscovered = discovered

        if (discovered) {
            this.backgroundImageView.setImage(this.spotDiscoveredImage)
            this.animationView.setPaused(true)
            this.animationView.setHidden(true)
        } else {
            this.backgroundImageView.setImage(this.backgroundImage)
            this.animationView.setPaused(false)
            this.animationView.setHidden(false)
        }
    }

    private fun setDistance(distance: Float?) {
        if (this.isSpotDiscovered)
            return

        this.animationScene.distance = distance ?: 0.0f

        if (distance == null) {
            this.animationView.setHidden(true)
            this.animationView.setPaused(true)
            this.backgroundImageView.setImage(this.noTasksImage)
        } else {
            this.animationView.setPaused(false)
            this.animationView.setHidden(false)
            this.backgroundImageView.setImage(this.backgroundImage)
        }
    }
}