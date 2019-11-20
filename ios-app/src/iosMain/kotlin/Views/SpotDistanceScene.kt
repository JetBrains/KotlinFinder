package views

import common.Colors
import kotlinx.cinterop.CValue
import platform.CoreGraphics.*
import platform.CoreImage.CIFilter
import platform.CoreImage.filterWithName
import platform.Foundation.NSCoder
import platform.Foundation.NSMutableArray
import platform.Foundation.NSTimeInterval
import platform.SpriteKit.*
import platform.UIKit.UIColor
import kotlin.random.Random


class SpotDistanceScene : SKScene {
    data class Bar(
        val topPart: SKShapeNode,
        val middlePart: SKShapeNode,
        val bottomPart: SKShapeNode,
        val glow: SKEffectNode
    )

    private val bars: NSMutableArray = NSMutableArray()
    private val initialBarHeight: CGFloat = 10.0
    private val maxScale: Float = 8.0f

    var distance: Float = 0.0f

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
        val barHeight: CGFloat = this.initialBarHeight
        val barWidth: CGFloat = width / barsCount / 2.0
        val barsSpacing: CGFloat = barWidth

        for (i in 0..barsCount) {
            val bar: Bar = this.createBar(width = barWidth, height = barHeight)
            val xPos: CGFloat = i * (barWidth + barsSpacing) + lrSpacings / 2.0 - barWidth / 2.0

            bar.topPart.position = CGPointMake(
                xPos,
                CGRectGetHeight(this.frame) / 2.0 + barHeight / 2.0 - barWidth / 2.0
            )

            bar.middlePart.position = CGPointMake(
                xPos,
                CGRectGetHeight(this.frame) / 2.0 - barHeight / 2.0
            )

            bar.bottomPart.position = CGPointMake(
                xPos,
                CGRectGetHeight(this.frame) / 2.0 - barHeight / 2.0 - barWidth / 2.0
            )

            bar.glow.position = CGPointMake(
                barWidth / 2.0,
                barHeight / 2.0
            )

            this.addChild(bar.middlePart)
            this.addChild(bar.topPart)
            this.addChild(bar.bottomPart)

            this.bars.addObject(bar)
        }
    }

    override fun update(currentTime: NSTimeInterval) {
        super.update(currentTime)

        for (i: Int in 0..(this.bars.count - 1u).toInt()) {
            val bar: Bar = (this.bars.objectAtIndex(i.toULong()) as? Bar) ?: return

            if (bar.middlePart.hasActions()) {
                continue
            }

            bar.middlePart.removeAllActions()
            bar.topPart.removeAllActions()
            bar.bottomPart.removeAllActions()

            val barWidth: CGFloat = CGRectGetWidth(bar.middlePart.frame)

            var scaleFactor: Float =
                Random.nextFloat() * 1.0f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat() +
                        this.distance * this.maxScale

            if (scaleFactor < 0.0 || this.distance == 0.0f)
                scaleFactor = 0.0f

            val newHeight: CGFloat = this.initialBarHeight * scaleFactor
            val newY: CGFloat = CGRectGetHeight(this.frame) / 2.0 - newHeight / 2.0
            var duration: NSTimeInterval =
                0.1 + Random.nextFloat() * (0.1f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat())

            if (duration < 0.1)
                duration = 0.2

            val scaleAnimation: SKAction =
                SKAction.scaleYTo(scale = scaleFactor.toDouble(), duration = duration)
            val moveAnimation: SKAction = SKAction.moveToY(y = newY, duration = duration)

            val unScaleAnimation: SKAction = scaleAnimation.reversedAction()
            val unMoveAnimation: SKAction = moveAnimation.reversedAction()

            val moveTopPartAnimation: SKAction =
                SKAction.moveToY(y = newY + newHeight - barWidth / 2.0, duration = duration)

            val moveBottomPartAnimation: SKAction =
                SKAction.moveToY(y = newY - barWidth / 2.0, duration = duration)

            val actionsSeq: SKAction = SKAction.sequence(
                listOf(
                    SKAction.group(listOf(scaleAnimation, moveAnimation)),
                    SKAction.group(listOf(unScaleAnimation, unMoveAnimation))
                )
            )

            bar.topPart.runAction(
                SKAction.sequence(
                    listOf(
                        moveTopPartAnimation,
                        moveTopPartAnimation.reversedAction()
                    )
                ), completion = {
                    bar.topPart.removeAllActions()
                })

            bar.middlePart.runAction(actionsSeq, completion = {
                bar.middlePart.removeAllActions()
            })

            bar.bottomPart.runAction(
                SKAction.sequence(
                    listOf(
                        moveBottomPartAnimation,
                        moveBottomPartAnimation.reversedAction()
                    )
                ), completion = {
                    bar.bottomPart.removeAllActions()
                })
        }
    }

    private fun createBar(width: CGFloat, height: CGFloat): Bar {
        val bar: SKShapeNode = SKShapeNode.shapeNodeWithRect(
            rect = CGRectMake(0.0, 0.0, width, height),
            cornerRadius = 0.0
        )
        bar.fillColor = Colors.orange
        bar.strokeColor = UIColor.clearColor
        bar.antialiased = true

        val circlePart: SKShapeNode = SKShapeNode.shapeNodeWithRect(
            rect = CGRectMake(0.0, 0.0, width, width),
            cornerRadius = width / 2.0
        )
        circlePart.fillColor = Colors.orange
        circlePart.strokeColor = UIColor.clearColor
        circlePart.antialiased = true

        val glow: SKEffectNode = SKEffectNode()
        glow.shouldRasterize = true
        glow.shouldEnableEffects = true
        bar.addChild(glow)

        val view: SKView = SKView()

        glow.addChild(SKSpriteNode(view.textureFromNode(bar)))
        glow.filter = CIFilter.filterWithName(
            name = "CIGaussianBlur",
            withInputParameters = mapOf("inputRadius" to 2.0)
        )
        glow.zPosition = -1.0

        return Bar(circlePart, bar, circlePart.copy() as SKShapeNode, glow)
    }
}
