package Screens

import Views.CollectWordView
import Views.CommonButton
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSCoder
import platform.Foundation.NSNumber
import platform.QuartzCore.CAShapeLayer
import platform.UIKit.*


class MainScreenViewController: UIViewController, UIScrollViewDelegateProtocol {
    private val scrollView: UIScrollView = UIScrollView()
    private val findTaskButton: CommonButton = CommonButton(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val controlWordContainerView: UIView = UIView()
    private val collectWordView: CollectWordView = CollectWordView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val shadowLayer: CAShapeLayer = CAShapeLayer()
    private val roundedCornersMaskLayer: CAShapeLayer = CAShapeLayer()
    private val shadowView: UIView = UIView()
    private val mapImageView: UIImageView = UIImageView(UIImage.imageNamed("mapImage"))

    @OverrideInit
    constructor() : super(nibName = null, bundle = null)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    override fun viewDidLoad() {
        super.viewDidLoad()

        with(view) {
            backgroundColor = UIColor.whiteColor

            addSubview(scrollView)
            addSubview(findTaskButton)
            addSubview(shadowView)
            addSubview(controlWordContainerView)
        }

        this.scrollView.addSubview(this.mapImageView)
        this.controlWordContainerView.addSubview(this.collectWordView)

        this.shadowView.backgroundColor = UIColor.whiteColor

        listOf(
            scrollView,
            findTaskButton,
            controlWordContainerView,
            shadowView,
            mapImageView,
            collectWordView
        ).forEach { it.translatesAutoresizingMaskIntoConstraints = false }

        this.collectWordView.topAnchor.constraintEqualToAnchor(this.shadowView.topAnchor, constant = 20.0).setActive(true)
        this.collectWordView.bottomAnchor.constraintEqualToAnchor(this.shadowView.bottomAnchor, constant = -30.0).setActive(true)
        this.collectWordView.centerXAnchor.constraintEqualToAnchor(this.shadowView.centerXAnchor).setActive(true)

        this.scrollView.leftAnchor.constraintEqualToAnchor(this.view.leftAnchor).setActive(true)
        this.scrollView.topAnchor.constraintEqualToAnchor(this.view.topAnchor).setActive(true)
        this.scrollView.rightAnchor.constraintEqualToAnchor(this.view.rightAnchor).setActive(true)
        this.scrollView.bottomAnchor.constraintEqualToAnchor(this.shadowView.topAnchor).setActive(true)
        this.scrollView.backgroundColor = UIColor.whiteColor

        this.shadowView.leftAnchor.constraintEqualToAnchor(this.view.leftAnchor).setActive(true)
        this.shadowView.rightAnchor.constraintEqualToAnchor(this.view.rightAnchor).setActive(true)
        this.shadowView.bottomAnchor.constraintEqualToAnchor(this.view.bottomAnchor).setActive(true)

        this.findTaskButton.heightAnchor.constraintEqualToConstant(50.0).setActive(true)
        this.findTaskButton.leftAnchor.constraintEqualToAnchor(this.view.leftAnchor, constant = 16.0).setActive(true)
        this.findTaskButton.rightAnchor.constraintEqualToAnchor(this.view.rightAnchor, constant = -16.0).setActive(true)
        this.findTaskButton.bottomAnchor.constraintEqualToAnchor(this.shadowView.topAnchor, constant = -20.0).setActive(true)

        this.findTaskButton.setStyle(CommonButton.Style.ORANGE)
        this.findTaskButton.setTitle("Find a task", forState = 0u)

        this.controlWordContainerView.fillContainer(this.shadowView)

        this.mapImageView.fillSuperview()

        this.collectWordView.backgroundColor = UIColor.whiteColor

        with(controlWordContainerView) {
            with(layer) {
                mask = this@MainScreenViewController.roundedCornersMaskLayer
                masksToBounds = false
            }
            backgroundColor = UIColor.whiteColor
        }

        with(shadowView.layer) {
            insertSublayer(layer = this@MainScreenViewController.shadowLayer, atIndex = 0u)
            masksToBounds = false
        }

        with(shadowLayer) {
            shadowOpacity = NSNumber(0.45).floatValue
            masksToBounds = false
            shadowRadius = 4.0
            shadowColor = UIColor.blackColor.CGColor
            shadowOffset = CGSizeMake(width = 0.0, height = 0.0)
        }

        with(scrollView) {
            setShowsHorizontalScrollIndicator(false)
            setShowsVerticalScrollIndicator(false)

            minimumZoomScale = 0.1
            maximumZoomScale = 1.0
            zoomScale = minimumZoomScale
            delegate = this@MainScreenViewController
        }
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        this.shadowLayer.frame = this.controlWordContainerView.bounds

        val path: UIBezierPath = UIBezierPath.bezierPathWithRoundedRect(
            rect = this.controlWordContainerView.bounds,
            byRoundingCorners = UIRectCornerTopLeft.or(UIRectCornerTopRight),
            cornerRadii = CGSizeMake(16.0, 16.0))

        this.shadowLayer.shadowPath = path.CGPath

        this.roundedCornersMaskLayer.path = path.CGPath
    }

    override fun viewForZoomingInScrollView(scrollView: UIScrollView): UIView {
        return this.mapImageView
    }

    private fun UIView.fillSuperview(spacings: UIEdgeInsets = UIEdgeInsetsZero) {
        assert(this.superview != null)

        this.translatesAutoresizingMaskIntoConstraints = false

        this.leftAnchor.constraintEqualToAnchor(this.superview!!.leftAnchor, constant = spacings.left).setActive(true)
        this.topAnchor.constraintEqualToAnchor(this.superview!!.topAnchor, constant = spacings.top).setActive(true)
        this.rightAnchor.constraintEqualToAnchor(this.superview!!.rightAnchor, constant = -spacings.right).setActive(true)
        this.bottomAnchor.constraintEqualToAnchor(this.superview!!.bottomAnchor, constant = -spacings.bottom).setActive(true)
    }

    private fun UIView.fillContainer(container: UIView, spacings: UIEdgeInsets = UIEdgeInsetsZero) {
        this.translatesAutoresizingMaskIntoConstraints = false

        this.leftAnchor.constraintEqualToAnchor(container.leftAnchor, constant = spacings.left).setActive(true)
        this.topAnchor.constraintEqualToAnchor(container.topAnchor, constant = spacings.top).setActive(true)
        this.rightAnchor.constraintEqualToAnchor(container.rightAnchor, constant = -spacings.right).setActive(true)
        this.bottomAnchor.constraintEqualToAnchor(container.bottomAnchor, constant = -spacings.bottom).setActive(true)
    }
}
