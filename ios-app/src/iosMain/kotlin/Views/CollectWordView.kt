package views

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UILabel
import platform.UIKit.UILayoutConstraintAxisHorizontal
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UIStackView
import platform.UIKit.UIView
import platform.UIKit.addGestureRecognizer
import platform.UIKit.addSubview
import platform.UIKit.bottomAnchor
import platform.UIKit.colorNamed
import platform.UIKit.leadingAnchor
import platform.UIKit.removeFromSuperview
import platform.UIKit.topAnchor
import platform.UIKit.trailingAnchor
import platform.UIKit.translatesAutoresizingMaskIntoConstraints
import kotlin.math.max
import kotlin.math.min


class CollectWordView : UIView {
    private val imageView: UIImageView = UIImageView()
    private val wordStackView: UIStackView = UIStackView()
    private val titleLabel: UILabel = UILabel()

    var didLongTapImageViewBlock: (() -> Unit)? = null

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame) {
        this.addSubview(this.imageView)
        this.addSubview(this.wordStackView)
        this.addSubview(this.titleLabel)

        this.wordStackView.axis = UILayoutConstraintAxisHorizontal

        this.imageView.image = UIImage.imageNamed("kotlin0")

        this.titleLabel.text = "Collect word"
        this.titleLabel.font = UIFont.systemFontOfSize(12.0)
        this.titleLabel.textColor = UIColor.colorNamed("blackLightTextColor")!!

        this.imageView.translatesAutoresizingMaskIntoConstraints = false
        this.imageView.leadingAnchor.constraintEqualToAnchor(this.leadingAnchor).setActive(true)
        this.imageView.topAnchor.constraintEqualToAnchor(this.topAnchor).setActive(true)
        this.imageView.bottomAnchor.constraintEqualToAnchor(this.bottomAnchor).setActive(true)

        this.wordStackView.translatesAutoresizingMaskIntoConstraints = false
        this.wordStackView.leadingAnchor.constraintEqualToAnchor(
            this.imageView.trailingAnchor,
            constant = 20.0
        ).setActive(true)
        this.wordStackView.trailingAnchor.constraintEqualToAnchor(this.trailingAnchor)
            .setActive(true)
        this.wordStackView.bottomAnchor.constraintEqualToAnchor(this.bottomAnchor, constant = 0.0)
            .setActive(true)

        this.titleLabel.translatesAutoresizingMaskIntoConstraints = false
        this.titleLabel.leadingAnchor.constraintEqualToAnchor(this.wordStackView.leadingAnchor)
            .setActive(true)
        this.titleLabel.topAnchor.constraintEqualToAnchor(this.topAnchor, constant = 2.0)
            .setActive(true)

        val longTapRecoginzer: UILongPressGestureRecognizer = UILongPressGestureRecognizer(
            target = this,
            action = platform.darwin.sel_registerName("didLongTapImageView")
        )

        longTapRecoginzer.minimumPressDuration = 2.0

        this.imageView.addGestureRecognizer(longTapRecoginzer)
        this.imageView.setUserInteractionEnabled(true)
    }

    fun setText(text: String) {

        this.wordStackView.arrangedSubviews().forEach { (it as? UIView)?.removeFromSuperview() }

        val textColor = UIColor.colorNamed("blackInactiveTextColor")
        val textFont = UIFont.boldSystemFontOfSize(42.0)

        text.map { char ->
            UILabel().also {
                it.text = "${char}"
                it.textColor = textColor!!
                it.font = textFont
            }
        }.forEach { this.wordStackView.addArrangedSubview(it) }
    }

    fun setCollectedLettersCount(count: Int) {
        val subviews: List<UIView> = this.wordStackView.arrangedSubviews() as List<UIView>
        val count = min(count, subviews.size)

        val textColorName = if (count == 0) "blackInactiveTextColor" else "blackTextColor"

        for (i in 0 until subviews.count()) {
            (subviews[i] as? UILabel)?.textColor =
                UIColor.colorNamed(textColorName)!!
        }

        this.imageView.image = UIImage.imageNamed("kotlin$count")
    }

    @ObjCAction
    private fun didLongTapImageView() {
        this.didLongTapImageViewBlock?.invoke()
    }
}
