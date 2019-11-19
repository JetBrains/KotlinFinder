package main

import main.Colors
import kotlinx.cinterop.CValue
import platform.CoreGraphics.*
import platform.Foundation.NSCoder
import platform.Foundation.NSMutableArray
import platform.Foundation.NSTimeInterval
import platform.SpriteKit.*
import platform.UIKit.UIColor
import platform.WatchKit.WKInterfaceSKScene
import kotlin.math.min
import kotlin.random.Random


class SpotDistanceSceneProxy {
    private val scene: SpotDistanceScene = SpotDistanceScene()

    fun presentOnScene(superScene: WKInterfaceSKScene) {
        this.scene.scaleMode = SKSceneScaleMode.SKSceneScaleModeResizeFill

        superScene.presentScene(this.scene)
    }

    fun setDistance(distance: Float) {
        this.scene.distance = distance
    }
}


class SpotDistanceScene : SKScene {
    data class Bar(
        val topPart: SKShapeNode,
        val middlePart: SKShapeNode,
        val bottomPart: SKShapeNode,
        val glow: SKEffectNode
    )

    private val bars: NSMutableArray = NSMutableArray()
    private val initialBarHeight: CGFloat = 10.0f
    private val maxScale: Float = 5.0f

    var distance: Float = 0.0f

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(size: CValue<CGSize>) : super(size)

    @OverrideInit
    constructor() : super()

    override fun didChangeSize(oldSize: CValue<CGSize>) {
        super.didChangeSize(oldSize)

        this.backgroundColor = UIColor.clearColor

        for (node: Any? in this.children) {
            (node as? SKNode)?.removeFromParent()
        }

        val backgroundSize: CGFloat = min(CGRectGetWidth(this.frame), CGRectGetHeight(this.frame))

        val backgroundNode: SKSpriteNode = SKSpriteNode.spriteNodeWithImageNamed("spotSearchBackground")
        backgroundNode.position = CGPointMake(CGRectGetMidX(this.frame), CGRectGetMidY(this.frame))
        backgroundNode.size = CGSizeMake(backgroundSize, backgroundSize)
        this.addChild(backgroundNode)

        val lrSpacings: CGFloat = 30.0f
        val width: CGFloat = CGRectGetWidth(this.frame) - lrSpacings

        val barsCount: Int = 10
        val barHeight: CGFloat = this.initialBarHeight
        val barWidth: CGFloat = width / barsCount / 2.0f
        val barsSpacing: CGFloat = barWidth

        for (i in 0..barsCount) {
            val bar: Bar = this.createBar(width = barWidth, height = barHeight)
            val xPos: CGFloat = i * (barWidth + barsSpacing) + lrSpacings / 2.0f - barWidth / 2.0f

            bar.topPart.position = CGPointMake(
                xPos,
                CGRectGetHeight(this.frame) / 2.0f + barHeight / 2.0f - barWidth / 2.0f
            )

            bar.middlePart.position = CGPointMake(
                xPos,
                CGRectGetHeight(this.frame) / 2.0f - barHeight / 2.0f
            )

            bar.bottomPart.position = CGPointMake(
                xPos,
                CGRectGetHeight(this.frame) / 2.0f - barHeight / 2.0f - barWidth / 2.0f
            )

            bar.glow.position = CGPointMake(
                barWidth / 2.0f,
                barHeight / 2.0f
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
            val bar: Bar = (this.bars.objectAtIndex(i.toUInt()) as? Bar) ?: return

            if (bar.middlePart.hasActions()) {
                continue
            }

            bar.middlePart.removeAllActions()
            bar.topPart.removeAllActions()
            bar.bottomPart.removeAllActions()

            val barWidth: CGFloat = CGRectGetWidth(bar.middlePart.frame)

            var scaleFactor: Float =
                Random.nextFloat() * 2.0f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat() +
                        this.distance * this.maxScale

            if (scaleFactor < 0.0 || this.distance == 0.0f)
                scaleFactor = 0.0f

            val newHeight: CGFloat = this.initialBarHeight * scaleFactor
            val newY: CGFloat = CGRectGetHeight(this.frame) / 2.0f - newHeight / 2.0f
            var duration: NSTimeInterval =
                0.1 + Random.nextFloat() * (0.1f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat())

            if (duration < 0.1)
                duration = 0.2

            val scaleAnimation: SKAction =
                SKAction.scaleYTo(scale = scaleFactor, duration = duration)
            val moveAnimation: SKAction = SKAction.moveToY(y = newY, duration = duration)

            val unScaleAnimation: SKAction = scaleAnimation.reversedAction()
            val unMoveAnimation: SKAction = moveAnimation.reversedAction()

            val moveTopPartAnimation: SKAction =
                SKAction.moveToY(y = newY + newHeight - barWidth / 2.0f, duration = duration)

            val moveBottomPartAnimation: SKAction =
                SKAction.moveToY(y = newY - barWidth / 2.0f, duration = duration)

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
            rect = CGRectMake(0.0f, 0.0f, width, height),
            cornerRadius = 0.0f
        )
        bar.fillColor = Colors.orange
        bar.strokeColor = UIColor.clearColor
        bar.antialiased = true

        val circlePart: SKShapeNode = SKShapeNode.shapeNodeWithRect(
            rect = CGRectMake(0.0f, 0.0f, width, width),
            cornerRadius = width / 2.0f
        )
        circlePart.fillColor = Colors.orange
        circlePart.strokeColor = UIColor.clearColor
        circlePart.antialiased = true

        val glow: SKEffectNode = SKEffectNode()
        glow.shouldRasterize = true
        glow.shouldEnableEffects = true
        bar.addChild(glow)


        return Bar(circlePart, bar, circlePart.copy() as SKShapeNode, glow)
    }
}
