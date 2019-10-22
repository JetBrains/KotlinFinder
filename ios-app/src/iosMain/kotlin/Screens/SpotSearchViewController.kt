package Screens

import Views.SpotDistanceView
import common.centerInSuperview
import common.fillSuperview
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSCoder
import platform.UIKit.*


class SpotSearchViewController: UIViewController {
    private val spotSearchStatusContainerView: UIView = UIView()
    private val statusLabel: UILabel = UILabel()
    private val instructionLabel: UILabel = UILabel()
    private val successImageView: UIImageView = UIImageView(UIImage.imageNamed("spotFound"))
    private val spotSearchView: SpotDistanceView = SpotDistanceView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))

    @OverrideInit
    constructor() : super(nibName = null, bundle = null)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    override fun viewDidLoad() {
        super.viewDidLoad()

        this.title = "Find a task"

        this.view.backgroundColor = UIColor.whiteColor()

        this.view.addSubview(this.spotSearchStatusContainerView)
        this.spotSearchStatusContainerView.translatesAutoresizingMaskIntoConstraints = false
        this.spotSearchStatusContainerView.centerXAnchor.constraintEqualToAnchor(this.view.centerXAnchor).setActive(true)
        this.spotSearchStatusContainerView.centerYAnchor.constraintEqualToAnchor(this.view.centerYAnchor).setActive(true)

        val spotBackgroundImageView: UIImageView = UIImageView(UIImage.imageNamed("spotSearchBackground"))
        this.spotSearchStatusContainerView.addSubview(spotBackgroundImageView)
        spotBackgroundImageView.fillSuperview()

        this.spotSearchStatusContainerView.addSubview(this.spotSearchView)
        this.spotSearchView.fillSuperview()
        this.spotSearchView.backgroundColor = UIColor.clearColor

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

        labelsStackView.topAnchor.constraintEqualToAnchor(this.spotSearchStatusContainerView.bottomAnchor, constant = 30.0).setActive(true)
        labelsStackView.centerXAnchor.constraintEqualToAnchor(this.view.centerXAnchor).setActive(true)
        labelsStackView.widthAnchor.constraintLessThanOrEqualToAnchor(this.view.widthAnchor, multiplier = 0.8, constant = 0.0).setActive(true)

        this.setSearchMode(true)
    }

    private fun setSearchMode(searchMode: Boolean) {
        if (searchMode) {
            this.statusLabel.text = "Searching..."
            this.instructionLabel.text = "The more intense and stronger the vibration, the closer you are to the goal!"
            this.successImageView.setHidden(true)
            this.spotSearchView.setHidden(false)
        } else {
            this.statusLabel.text = "Spot found"
            this.instructionLabel.text = "You are well done! Another letter in the control word is open"
            this.successImageView.setHidden(false)
            this.spotSearchView.setHidden(true)
        }
    }
}