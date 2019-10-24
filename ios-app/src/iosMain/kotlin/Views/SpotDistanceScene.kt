package Views

import common.Colors
import dev.icerock.moko.core.Timer
import kotlinx.cinterop.CValue
import platform.CoreGraphics.*
import platform.CoreImage.CIFilter
import platform.CoreImage.filterWithName
import platform.Foundation.NSCoder
import platform.Foundation.NSMutableArray
import platform.Foundation.NSTimeInterval
import platform.Foundation.removeAllObjects
import platform.SpriteKit.*
import platform.UIKit.UIColor
import platform.UIKit.UIView
import kotlin.random.Random


class SpotDistanceScene: SKScene {
    private var barNodes: MutableList<SKShapeNode> = mutableListOf()
    private var glowNodes: NSMutableArray = NSMutableArray()

    private var distance: CGFloat = 1.0

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(size: CValue<CGSize>) : super(size)

    @OverrideInit
    constructor() : super()

    override fun didMoveToView(view: SKView) {
        super.didMoveToView(view)

        this.backgroundColor = UIColor.clearColor

    }

    override fun didChangeSize(oldSize: CValue<CGSize>) {
        super.didChangeSize(oldSize)

        for (node: Any? in this.children) {
            (node as? SKNode)?.removeFromParent()
        }

        val lrSpacings: CGFloat = 80.0
        val width: CGFloat = CGRectGetWidth(this.frame) - lrSpacings

        val barsCount: Int = 20
        val barHeight: CGFloat = 20.0
        val barWidth: CGFloat = width / barsCount / 2.0
        val barsSpacing: CGFloat = barWidth

        println("width: ${CGRectGetWidth(this.frame)}\n")
        println("bar width: $barWidth\n")
        println("bars count: $barsCount\n")

        for (i in 0..barsCount) {
            val nodes: Pair<SKShapeNode, SKEffectNode> = this.createBar(width = barWidth, height = barHeight)
            val bar: SKShapeNode = nodes.first
            val glow: SKEffectNode = nodes.second

            bar.position = CGPointMake(
                i * (barWidth + barsSpacing) + lrSpacings / 2.0 ,
                CGRectGetHeight(this.frame) / 2.0 - barHeight / 2.0)

            glow.position = CGPointMake(
                0.0 ,
                barHeight / 2.0)

            this.addChild(bar)

            println(this.barNodes)
            println(bar)

            this.barNodes.add(bar)
            this.glowNodes.addObject(glow)
        }

        //this.setDistance(1.0f)


    }

   /* override fun update(currentTime: NSTimeInterval) {
        super.update(currentTime)

        for (i: Int in 0..(this.barNodes.count - 1u).toInt()) {
            val barNode: SKShapeNode =
                (this.barNodes.objectAtIndex(i.toULong()) as? SKShapeNode) ?: return


        }
    }*/

    fun setDistance(distance: Float) {
        this.startAnimation(distance)
    }

    private fun startAnimation(distance: Float) {
        for (i: Int in 0..(this.barNodes.count() - 1).toInt()) {
            val barNode: SKShapeNode = this.barNodes[i] //(this.barNodes.objectAtIndex(i.toULong()) as? SKShapeNode) ?: return

            barNode.removeAllActions()

            val scaleFactor: Float = (0.5f + Random.nextFloat()) * distance
            val newHeight: CGFloat = CGRectGetHeight(barNode.frame) * scaleFactor
            val newY: CGFloat = CGRectGetHeight(this.frame) / 2.0 - newHeight / 2.0
            val duration: NSTimeInterval = 1.0 + Random.nextFloat() * (-1.0f * Random.nextInt() % 2)

            val scaleAnimation: SKAction = SKAction.scaleYTo(scale = scaleFactor.toDouble(), duration = duration)
            val moveAnimation: SKAction = SKAction.moveToY(y = newY, duration = duration)

            val unScaleAnimation: SKAction = SKAction.scaleYTo(scale = 1.0 / scaleFactor, duration = duration)
            val unMoveAnimation: SKAction = SKAction.moveToY(y = CGRectGetHeight(this.frame) / 2.0 - (newHeight / scaleFactor) / 2.0, duration = duration)

            //barNode.runAction(scaleAnimation)
            //barNode.runAction(moveAnimation)

            //barNode.runAction(SKAction.group(listOf(scaleAnimation, moveAnimation)))
            //SKAction.sequence(listOf(SKAction.group(listOf(scaleAnimation, moveAnimation)),
           //     SKAction.group(listOf(unScaleAnimation, unMoveAnimation))))

            /*barNode.runAction(SKAction.group(listOf(scaleAnimation, moveAnimation)), completion = {

            })*/

            barNode.runAction(SKAction.repeatActionForever(
                SKAction.sequence(listOf(SKAction.group(listOf(scaleAnimation, moveAnimation)),
                    SKAction.group(listOf(unScaleAnimation, unMoveAnimation))))))
        }
    }

    private fun createBar(width: CGFloat, height: CGFloat): Pair<SKShapeNode, SKEffectNode> {
        val bar: SKShapeNode = SKShapeNode.shapeNodeWithRect(
            rect = CGRectMake(0.0, 0.0, width, height),
            cornerRadius = width / 2.0)
        bar.fillColor = Colors.orange
        bar.strokeColor = UIColor.clearColor
        bar.antialiased = true

        val glow: SKEffectNode = SKEffectNode()
        glow.shouldRasterize = true
        bar.addChild(glow)

        val view: SKView = SKView()

        glow.addChild(SKSpriteNode(view.textureFromNode(bar)))
        glow.filter = CIFilter.filterWithName(
            name ="CIGaussianBlur",
            withInputParameters = mapOf("inputRadius" to 10.0))

        return Pair(bar, glow)
    }

    private fun clearScene() {
        /*for (i: Int in 0..(this.barNodes.count - 1u).toInt()) {
            (this.barNodes.objectAtIndex(i.toULong()) as? SKShapeNode)?.removeFromParent()
        }

        this.barNodes.removeAllObjects()*/
    }
}