package views

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.UIKit.*


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
        this.wordStackView.arrangedSubviews().map { (it as? UIView)?.removeFromSuperview() }

        for (i in 0..(text.count() - 1)) {
            val label: UILabel = UILabel()
            label.text = "${text[i]}"
            label.textColor = UIColor.colorNamed("blackInactiveTextColor")!!
            label.font = UIFont.boldSystemFontOfSize(42.0)

            this.wordStackView.addArrangedSubview(label)
        }
    }

    fun setCollectedLettersCount(count: Int) {
        if (count == 0) {
            for (i in 0..(this.wordStackView.arrangedSubviews.count() - 1)) {
                (this.wordStackView.arrangedSubviews()[i] as? UILabel)?.textColor =
                    UIColor.colorNamed("blackInactiveTextColor")!!
            }
        } else {
            for (i in 0..(count - 1)) {
                (this.wordStackView.arrangedSubviews()[i] as? UILabel)?.textColor =
                    UIColor.colorNamed("blackTextColor")!!
            }
        }

        this.imageView.image = UIImage.imageNamed("kotlin$count")
    }

    @ObjCAction
    private fun didLongTapImageView() {
        this.didLongTapImageViewBlock?.invoke()
    }
}
