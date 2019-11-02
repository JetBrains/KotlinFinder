package screens

import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import common.centerInSuperview
import common.fillSuperview
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSCoder
import platform.SpriteKit.SKSceneScaleMode
import platform.SpriteKit.SKView
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIBarButtonItem
import platform.UIKit.UIBarButtonItemStylePlain
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UILabel
import platform.UIKit.UILayoutConstraintAxisVertical
import platform.UIKit.UIStackView
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.addSubview
import platform.UIKit.backgroundColor
import platform.UIKit.bottomAnchor
import platform.UIKit.centerXAnchor
import platform.UIKit.centerYAnchor
import platform.UIKit.colorNamed
import platform.UIKit.navigationController
import platform.UIKit.navigationItem
import platform.UIKit.setHidden
import platform.UIKit.topAnchor
import platform.UIKit.translatesAutoresizingMaskIntoConstraints
import platform.UIKit.widthAnchor
import views.SpotDistanceScene


class SpotSearchViewController : UIViewController {
    private val spotSearchStatusContainerView: UIView = UIView()
    private val statusLabel: UILabel = UILabel()
    private val instructionLabel: UILabel = UILabel()
    private val successImageView: UIImageView = UIImageView(UIImage.imageNamed("spotFound"))
    private val spotSearchViewContainer: SKView = SKView()
    private val spotSearchScene: SpotDistanceScene = SpotDistanceScene()

    private lateinit var viewModel: SpotSearchViewModel

    @OverrideInit
    constructor() : super(nibName = null, bundle = null)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    override fun viewDidLoad() {
        super.viewDidLoad()

        this.title = "Find a task"

        this.navigationItem.leftBarButtonItem = UIBarButtonItem(
            image = UIImage.imageNamed("back"),
            style = UIBarButtonItemStylePlain,
            target = this,
            action = platform.darwin.sel_registerName("backButtonTapped")
        )

        this.view.backgroundColor = UIColor.whiteColor()

        this.view.addSubview(this.spotSearchStatusContainerView)
        this.spotSearchStatusContainerView.translatesAutoresizingMaskIntoConstraints = false
        this.spotSearchStatusContainerView.centerXAnchor.constraintEqualToAnchor(this.view.centerXAnchor)
            .setActive(true)
        this.spotSearchStatusContainerView.centerYAnchor.constraintEqualToAnchor(this.view.centerYAnchor)
            .setActive(true)

        val spotBackgroundImageView: UIImageView =
            UIImageView(UIImage.imageNamed("spotSearchBackground"))
        this.spotSearchStatusContainerView.addSubview(spotBackgroundImageView)
        spotBackgroundImageView.fillSuperview()

        this.spotSearchStatusContainerView.addSubview(this.spotSearchViewContainer)
        this.spotSearchViewContainer.fillSuperview()
        this.spotSearchViewContainer.backgroundColor = UIColor.clearColor
        this.spotSearchViewContainer.showsFPS = true
        this.spotSearchViewContainer.showsNodeCount = true

        this.spotSearchStatusContainerView.addSubview(this.successImageView)
        this.successImageView.centerInSuperview()

        this.statusLabel.font = UIFont.boldSystemFontOfSize(20.0)
        this.statusLabel.textAlignment = NSTextAlignmentCenter
        this.statusLabel.textColor = UIColor.colorNamed("blackTextColor")!!

        this.instructionLabel.font = UIFont.systemFontOfSize(14.0)
        this.instructionLabel.textAlignment = NSTextAlignmentCenter
        this.instructionLabel.numberOfLines = 0
        this.instructionLabel.lineBreakMode = NSLineBreakByWordWrapping
        this.instructionLabel.textColor = UIColor.colorNamed("blackLightTextColor")!!

        val labelsStackView: UIStackView = UIStackView()
        labelsStackView.translatesAutoresizingMaskIntoConstraints = false
        labelsStackView.axis = UILayoutConstraintAxisVertical
        labelsStackView.spacing = 10.0
        labelsStackView.addArrangedSubview(this.statusLabel)
        labelsStackView.addArrangedSubview(this.instructionLabel)

        this.view.addSubview(labelsStackView)

        labelsStackView.topAnchor.constraintEqualToAnchor(
            this.spotSearchStatusContainerView.bottomAnchor,
            constant = 30.0
        ).setActive(true)
        labelsStackView.centerXAnchor.constraintEqualToAnchor(this.view.centerXAnchor)
            .setActive(true)
        labelsStackView.widthAnchor.constraintLessThanOrEqualToAnchor(
            this.view.widthAnchor,
            multiplier = 0.8,
            constant = 0.0
        ).setActive(true)

        this.spotSearchScene.distance = 5.0f
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        this.spotSearchScene.scaleMode = SKSceneScaleMode.SKSceneScaleModeResizeFill
        this.spotSearchViewContainer.presentScene(this.spotSearchScene)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)

        this.navigationController?.setNavigationBarHidden(false, animated = false)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)

        if (this.isMovingFromParentViewController()) {
            viewModel.onCleared()
        }
    }

    fun bindViewModel(viewModel: SpotSearchViewModel) {
        this.viewModel = viewModel

        viewModel.nearestBeaconDistance.addObserver { distance: Int? ->
            this.spotSearchScene.distance =
                this.spotSearchScene.maxDistance * (viewModel.minDistance - (distance
                    ?: viewModel.minDistance)) / viewModel.minDistance
        }

        viewModel.isSearchMode.addObserver { searchMode: Boolean ->
            if (searchMode) {
                this.statusLabel.text = "Searching..."
                this.successImageView.setHidden(true)
                this.spotSearchViewContainer.setHidden(false)
            } else {
                this.statusLabel.text = "Spot found"
                this.successImageView.setHidden(false)
                this.spotSearchViewContainer.setHidden(true)
            }
        }

        viewModel.hintText.addObserver { text: String ->
            this.instructionLabel.text = text
        }
    }

    @ObjCAction
    fun backButtonTapped() {
        this.navigationController?.popViewControllerAnimated(true)
    }
}
