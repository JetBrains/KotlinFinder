package Screens

import Views.CollectWordView
import kotlinx.cinterop.CValue
import platform.CoreGraphics.*
import platform.Foundation.NSCoder
import platform.Foundation.NSNumber
import platform.QuartzCore.CAShapeLayer
import platform.UIKit.*


class MainScreenViewController: UIViewController, UIScrollViewDelegateProtocol {
    private val scrollView: UIScrollView = UIScrollView()
    private val findTaskButton: UIButton = UIButton()
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

        this.view.backgroundColor = UIColor.whiteColor

        this.view.addSubview(this.scrollView)
        this.view.addSubview(this.findTaskButton)
        this.view.addSubview(this.shadowView)
        this.view.addSubview(this.controlWordContainerView)
        this.scrollView.addSubview(this.mapImageView)
        this.controlWordContainerView.addSubview(this.collectWordView)

        this.shadowView.backgroundColor = UIColor.whiteColor

        this.scrollView.translatesAutoresizingMaskIntoConstraints = false
        this.findTaskButton.translatesAutoresizingMaskIntoConstraints = false
        this.controlWordContainerView.translatesAutoresizingMaskIntoConstraints = false
        this.shadowView.translatesAutoresizingMaskIntoConstraints = false
        this.mapImageView.translatesAutoresizingMaskIntoConstraints = false
        this.collectWordView.translatesAutoresizingMaskIntoConstraints = false

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

        this.controlWordContainerView.fillContainer(this.shadowView)

        this.mapImageView.fillSuperview()

        this.findTaskButton.layer.cornerRadius = 16.0
        this.findTaskButton.setBackgroundColor(UIColor.colorNamed("orangeColor"))

        this.controlWordContainerView.backgroundColor = UIColor.whiteColor
        this.collectWordView.backgroundColor = UIColor.whiteColor

        this.controlWordContainerView.layer.mask = this.roundedCornersMaskLayer
        this.controlWordContainerView.layer.masksToBounds = false

        this.shadowView.layer.insertSublayer(layer = this.shadowLayer, atIndex = 0)
        this.shadowView.layer.masksToBounds = false

        this.shadowLayer.shadowOpacity = NSNumber(0.45).floatValue
        this.shadowLayer.masksToBounds = false
        this.shadowLayer.shadowRadius = 4.0
        this.shadowLayer.shadowColor = UIColor.blackColor.CGColor
        this.shadowLayer.shadowOffset = CGSizeMake(width = 0.0, height = 0.0)

        this.scrollView.setShowsHorizontalScrollIndicator(false)
        this.scrollView.setShowsVerticalScrollIndicator(false)
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

    override fun viewForZoomingInScrollView(scrollView: platform.UIKit.UIScrollView): UIView {
        return this.mapImageView
    }

    fun UIView.fillSuperview(spacings: UIEdgeInsets = UIEdgeInsetsZero) {
        assert(this.superview != null)

        this.translatesAutoresizingMaskIntoConstraints = false

        this.leftAnchor.constraintEqualToAnchor(this.superview!!.leftAnchor, constant = spacings.left).setActive(true)
        this.topAnchor.constraintEqualToAnchor(this.superview!!.topAnchor, constant = spacings.top).setActive(true)
        this.rightAnchor.constraintEqualToAnchor(this.superview!!.rightAnchor, constant = -spacings.right).setActive(true)
        this.bottomAnchor.constraintEqualToAnchor(this.superview!!.bottomAnchor, constant = -spacings.bottom).setActive(true)
    }

    fun UIView.fillContainer(container: UIView, spacings: UIEdgeInsets = UIEdgeInsetsZero) {
        this.translatesAutoresizingMaskIntoConstraints = false

        this.leftAnchor.constraintEqualToAnchor(container.leftAnchor, constant = spacings.left).setActive(true)
        this.topAnchor.constraintEqualToAnchor(container.topAnchor, constant = spacings.top).setActive(true)
        this.rightAnchor.constraintEqualToAnchor(container.rightAnchor, constant = -spacings.right).setActive(true)
        this.bottomAnchor.constraintEqualToAnchor(container.bottomAnchor, constant = -spacings.bottom).setActive(true)
    }
}