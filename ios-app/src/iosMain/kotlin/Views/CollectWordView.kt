package Views

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.UIKit.*

class CollectWordView: UIView {
    private val imageView: UIImageView = UIImageView()
    private val wordLabel: UILabel = UILabel()
    private val titleLabel: UILabel = UILabel()

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame) {
        this.addSubview(this.imageView)
        this.addSubview(this.wordLabel)
        this.addSubview(this.titleLabel)

        this.imageView.image = UIImage.imageNamed("kotlin0")

        this.wordLabel.text = "KOTLIN"
        this.wordLabel.font = UIFont.systemFontOfSize(26.0)

        this.titleLabel.text = "Collect word"
        this.titleLabel.font = UIFont.systemFontOfSize(12.0)

        this.imageView.translatesAutoresizingMaskIntoConstraints = false
        this.imageView.leadingAnchor.constraintEqualToAnchor(this.leadingAnchor).setActive(true)
        this.imageView.topAnchor.constraintEqualToAnchor(this.topAnchor).setActive(true)
        this.imageView.bottomAnchor.constraintEqualToAnchor(this.bottomAnchor).setActive(true)

        this.wordLabel.translatesAutoresizingMaskIntoConstraints = false
        this.wordLabel.leadingAnchor.constraintEqualToAnchor(this.imageView.trailingAnchor, constant = 40.0).setActive(true)
        this.wordLabel.trailingAnchor.constraintEqualToAnchor(this.trailingAnchor).setActive(true)
        this.wordLabel.bottomAnchor.constraintEqualToAnchor(this.bottomAnchor, constant = -10.0).setActive(true)

        this.titleLabel.translatesAutoresizingMaskIntoConstraints = false
        this.titleLabel.leadingAnchor.constraintEqualToAnchor(this.wordLabel.leadingAnchor).setActive(true)
        this.titleLabel.topAnchor.constraintEqualToAnchor(this.topAnchor, constant = 2.0).setActive(true)
    }
}
