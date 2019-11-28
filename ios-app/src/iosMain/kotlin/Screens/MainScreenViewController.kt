package screens

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import common.FeedbackGenerator
import common.fillContainer
import common.fillSuperview
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.*
import platform.Foundation.NSCoder
import platform.Foundation.NSNumber
import platform.QuartzCore.CAShapeLayer
import platform.UIKit.*
import views.CollectWordView
import views.SpotDistanceView
import kotlin.math.min


class MainScreenViewController : UIViewController, UIScrollViewDelegateProtocol {
    private val scrollView: UIScrollView = UIScrollView()
    private val controlWordContainerView: UIView = UIView()
    private val collectWordView: CollectWordView =
        CollectWordView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val shadowLayer: CAShapeLayer = CAShapeLayer()
    private val roundedCornersMaskLayer: CAShapeLayer = CAShapeLayer()
    private val shadowView: UIView = UIView()
    private val mapImageView: UIImageView = UIImageView(UIImage.imageNamed("mapImage"))
    private val hintButton: UIButton = UIButton.buttonWithType(3)
    private val strengthLabel: UILabel = UILabel()
    private val spotDistanceView: SpotDistanceView = SpotDistanceView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val feedbackGenerator: FeedbackGenerator = FeedbackGenerator()
    private lateinit var spotDistanceViewWidthConstraint: NSLayoutConstraint

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
            addSubview(shadowView)
            addSubview(controlWordContainerView)
            addSubview(strengthLabel)
            addSubview(spotDistanceView)
        }

        this.scrollView.addSubview(this.mapImageView)
        this.controlWordContainerView.addSubview(this.collectWordView)
        this.controlWordContainerView.addSubview(this.hintButton)

        this.shadowView.backgroundColor = UIColor.whiteColor

        listOf(
            scrollView,
            controlWordContainerView,
            shadowView,
            mapImageView,
            collectWordView,
            this.hintButton,
            this.strengthLabel,
            this.spotDistanceView
        ).forEach { it.translatesAutoresizingMaskIntoConstraints = false }

        this.spotDistanceView.centerXAnchor.constraintEqualToAnchor(this.view.rightAnchor, constant = -70.0).setActive(true)
        this.spotDistanceView.centerYAnchor.constraintEqualToAnchor(this.view.topAnchor, constant = 80.0).setActive(true)
        this.spotDistanceViewWidthConstraint = this.spotDistanceView.widthAnchor.constraintEqualToConstant(80.0)
        this.spotDistanceView.heightAnchor.constraintEqualToAnchor(this.spotDistanceView.widthAnchor).setActive(true)

        this.spotDistanceViewWidthConstraint.setActive(true)

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

        this.strengthLabel.leftAnchor.constraintEqualToAnchor(this.scrollView.leftAnchor).setActive(true)
        this.strengthLabel.topAnchor.constraintEqualToAnchor(this.scrollView.topAnchor).setActive(true)

        this.strengthLabel.textColor = UIColor.redColor
        this.strengthLabel.font = UIFont.boldSystemFontOfSize(30.0)

        this.strengthLabel.setHidden(true)

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

        this.hintButton.addTarget(
            target = this,
            action = platform.darwin.sel_registerName("hintButtonTapped"),
            forControlEvents = UIControlEventTouchUpInside
        )

        this.collectWordView.didLongTapImageViewBlock = {
            this.viewModel.resetCookiesButtonTapped()
        }
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

    override fun viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        this.scrollView.minimumZoomScale = min(
            CGRectGetWidth(this.scrollView.frame) / CGRectGetWidth(this.mapImageView.frame),
            CGRectGetHeight(this.scrollView.frame) / CGRectGetHeight(this.mapImageView.frame))
        this.scrollView.zoomScale = 1.0
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

        viewModel.hintButtonEnabled.addObserver { enabled: Boolean ->
            this.hintButton.setEnabled(enabled)
        }
        
        viewModel.signalStrength.addObserver { strength: Float? ->
            this.strengthLabel.text = "$strength"

            this.feedbackGenerator.feedback(strength ?: 0.0f)
        }

        viewModel.searchViewState.addObserver { state: MapViewModel.SearchViewState ->
            println("STATE: $state")

            if (state is MapViewModel.SearchViewState.noTask)
                this.spotDistanceViewWidthConstraint.setConstant(80.0)
            else
                this.spotDistanceViewWidthConstraint.setConstant(100.0)

            UIView.animateWithDuration(0.22) {
                this.view.layoutIfNeeded()
            }

            this.spotDistanceView.setState(state)
        }
        
        viewModel.requestPermissions()
    }

    @ObjCAction
    private fun hintButtonTapped() {
        this.viewModel.hintButtonTapped()
    }

    override fun viewForZoomingInScrollView(scrollView: UIScrollView): UIView {
        return this.mapImageView
    }
}
