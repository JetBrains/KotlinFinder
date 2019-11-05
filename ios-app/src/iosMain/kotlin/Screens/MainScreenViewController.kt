package screens

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import common.fillContainer
import common.fillSuperview
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSCoder
import platform.Foundation.NSNumber
import platform.QuartzCore.CAShapeLayer
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIRectCornerTopLeft
import platform.UIKit.UIRectCornerTopRight
import platform.UIKit.UIScrollView
import platform.UIKit.UIScrollViewDelegateProtocol
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.addSubview
import platform.UIKit.backgroundColor
import platform.UIKit.bottomAnchor
import platform.UIKit.centerXAnchor
import platform.UIKit.heightAnchor
import platform.UIKit.leftAnchor
import platform.UIKit.navigationController
import platform.UIKit.rightAnchor
import platform.UIKit.topAnchor
import platform.UIKit.translatesAutoresizingMaskIntoConstraints
import platform.UIKit.UIButton
import platform.UIKit.UIButtonType
import views.CollectWordView
import views.CommonButton


class MainScreenViewController : UIViewController, UIScrollViewDelegateProtocol {
    private val scrollView: UIScrollView = UIScrollView()
    private val findTaskButton: CommonButton = CommonButton(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val controlWordContainerView: UIView = UIView()
    private val collectWordView: CollectWordView =
        CollectWordView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val shadowLayer: CAShapeLayer = CAShapeLayer()
    private val roundedCornersMaskLayer: CAShapeLayer = CAShapeLayer()
    private val shadowView: UIView = UIView()
    private val mapImageView: UIImageView = UIImageView(UIImage.imageNamed("mapImage"))
    private val hintButton: UIButton = UIButton.buttonWithType(3)

    private lateinit var viewModel: MapViewModel

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
        this.controlWordContainerView.addSubview(this.hintButton)

        this.shadowView.backgroundColor = UIColor.whiteColor

        listOf(
            scrollView,
            findTaskButton,
            controlWordContainerView,
            shadowView,
            mapImageView,
            collectWordView,
            this.hintButton
        ).forEach { it.translatesAutoresizingMaskIntoConstraints = false }

        this.collectWordView.topAnchor.constraintEqualToAnchor(
            this.shadowView.topAnchor,
            constant = 20.0
        ).setActive(true)

        this.collectWordView.bottomAnchor.constraintEqualToAnchor(
            this.shadowView.bottomAnchor,
            constant = -30.0
        ).setActive(true)

        this.collectWordView.centerXAnchor.constraintEqualToAnchor(this.shadowView.centerXAnchor)
            .setActive(true)

        this.hintButton.topAnchor.constraintEqualToAnchor(
            this.controlWordContainerView.topAnchor,
            constant = 20.0
        ).setActive(true)

        this.hintButton.rightAnchor.constraintEqualToAnchor(
            this.controlWordContainerView.rightAnchor,
            constant = -20.0
        ).setActive(true)

        this.scrollView.leftAnchor.constraintEqualToAnchor(this.view.leftAnchor).setActive(true)
        this.scrollView.topAnchor.constraintEqualToAnchor(this.view.topAnchor).setActive(true)
        this.scrollView.rightAnchor.constraintEqualToAnchor(this.view.rightAnchor).setActive(true)
        this.scrollView.bottomAnchor.constraintEqualToAnchor(this.shadowView.topAnchor)
            .setActive(true)
        this.scrollView.backgroundColor = UIColor.whiteColor

        this.shadowView.leftAnchor.constraintEqualToAnchor(this.view.leftAnchor).setActive(true)
        this.shadowView.rightAnchor.constraintEqualToAnchor(this.view.rightAnchor).setActive(true)
        this.shadowView.bottomAnchor.constraintEqualToAnchor(this.view.bottomAnchor).setActive(true)

        this.findTaskButton.heightAnchor.constraintEqualToConstant(50.0).setActive(true)
        this.findTaskButton.leftAnchor.constraintEqualToAnchor(
            this.view.leftAnchor,
            constant = 16.0
        ).setActive(true)
        this.findTaskButton.rightAnchor.constraintEqualToAnchor(
            this.view.rightAnchor,
            constant = -16.0
        ).setActive(true)
        this.findTaskButton.bottomAnchor.constraintEqualToAnchor(
            this.shadowView.topAnchor,
            constant = -20.0
        ).setActive(true)

        this.controlWordContainerView.fillContainer(this.shadowView)

        this.mapImageView.fillSuperview()

        this.collectWordView.backgroundColor = UIColor.whiteColor

        this.collectWordView.setText("KOTLIN")
        this.collectWordView.setCollectedLettersCount(0)

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

        this.findTaskButton.addTarget(
            target = this,
            action = platform.darwin.sel_registerName("findTaskButtonTapped"),
            forControlEvents = UIControlEventTouchUpInside
        )

        this.hintButton.addTarget(
            target = this,
            action = platform.darwin.sel_registerName("hintButtonTapped"),
            forControlEvents = UIControlEventTouchUpInside
        )
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)

        this.navigationController?.setNavigationBarHidden(true, animated = false)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)

        if (this.isMovingFromParentViewController()) {
            viewModel.onCleared()
        }
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        this.shadowLayer.frame = this.controlWordContainerView.bounds

        val path: UIBezierPath = UIBezierPath.bezierPathWithRoundedRect(
            rect = this.controlWordContainerView.bounds,
            byRoundingCorners = UIRectCornerTopLeft.or(UIRectCornerTopRight),
            cornerRadii = CGSizeMake(16.0, 16.0)
        )

        this.shadowLayer.shadowPath = path.CGPath

        this.roundedCornersMaskLayer.path = path.CGPath
    }

    fun bindViewModel(viewModel: MapViewModel) {
        this.loadViewIfNeeded()

        this.viewModel = viewModel

        viewModel.currentStep.addObserver { step: Int ->
            this.collectWordView.setCollectedLettersCount(step)
        }

        viewModel.findTaskButtonState.addObserver { state: MapViewModel.FindTaskButtonState ->
            when (state) {
                MapViewModel.FindTaskButtonState.ACTIVE -> {
                    this.findTaskButton.enabled = true
                    this.findTaskButton.setStyle(CommonButton.Style.ORANGE)
                    this.findTaskButton.setTitle("Find a task", forState = 0u)
                }

                MapViewModel.FindTaskButtonState.TOO_FAR -> {
                    this.findTaskButton.enabled = false
                    this.findTaskButton.setStyle(CommonButton.Style.GRAY)
                    this.findTaskButton.setTitle(
                        "You are too far from the task point",
                        forState = 0u
                    )
                }

                MapViewModel.FindTaskButtonState.COMPLETED -> {
                    this.findTaskButton.enabled = false
                    this.findTaskButton.setStyle(CommonButton.Style.ORANGE)
                    this.findTaskButton.setTitle("Completed", forState = 0u)
                }
            }
        }
    }

    @ObjCAction
    private fun findTaskButtonTapped() {
        this.viewModel.findTaskButtonTapped()
    }

    @ObjCAction
    private fun hintButtonTapped() {

    }

    override fun viewForZoomingInScrollView(scrollView: UIScrollView): UIView {
        return this.mapImageView
    }
}