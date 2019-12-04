package screens

import com.kotlinconf.library.feature.mainMap.presentation.SplashViewModel
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
import platform.UIKit.centerYAnchor
import platform.UIKit.heightAnchor
import platform.UIKit.leftAnchor
import platform.UIKit.navigationController
import platform.UIKit.rightAnchor
import platform.UIKit.topAnchor
import platform.UIKit.translatesAutoresizingMaskIntoConstraints
import platform.UIKit.UIButton
import platform.UIKit.UIButtonType


class SplashViewController: UIViewController {
    private lateinit var viewModel: SplashViewModel

    @OverrideInit
    constructor() : super(nibName = null, bundle = null)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    override fun viewDidLoad() {
        super.viewDidLoad()

        this.view.backgroundColor = UIColor.whiteColor

        val imageView: UIImageView = UIImageView(UIImage.imageNamed("kotlin6"))
        imageView.translatesAutoresizingMaskIntoConstraints = false

        this.view.addSubview(imageView)

        imageView.centerXAnchor.constraintEqualToAnchor(this.view.centerXAnchor).setActive(true)
        imageView.centerYAnchor.constraintEqualToAnchor(this.view.centerYAnchor).setActive(true)
    }

    fun bindViewModel(viewModel: SplashViewModel) {
        this.viewModel = viewModel
    }
}