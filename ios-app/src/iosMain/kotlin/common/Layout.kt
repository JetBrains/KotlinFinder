package common

import platform.UIKit.*

fun UIView.fillSuperview(spacings: UIEdgeInsets = UIEdgeInsetsZero) {
    assert(this.superview != null)

    this.translatesAutoresizingMaskIntoConstraints = false

    this.leftAnchor.constraintEqualToAnchor(
        this.superview!!.leftAnchor,
        constant = spacings.left
    ).setActive(true)
    this.topAnchor.constraintEqualToAnchor(this.superview!!.topAnchor, constant = spacings.top)
        .setActive(true)
    this.rightAnchor.constraintEqualToAnchor(
        this.superview!!.rightAnchor,
        constant = -spacings.right
    ).setActive(true)
    this.bottomAnchor.constraintEqualToAnchor(
        this.superview!!.bottomAnchor,
        constant = -spacings.bottom
    ).setActive(true)
}

fun UIView.fillContainer(container: UIView, spacings: UIEdgeInsets = UIEdgeInsetsZero) {
    assert(this.superview != null)

    this.translatesAutoresizingMaskIntoConstraints = false

    this.leftAnchor.constraintEqualToAnchor(container.leftAnchor, constant = spacings.left)
        .setActive(true)
    this.topAnchor.constraintEqualToAnchor(container.topAnchor, constant = spacings.top)
        .setActive(true)
    this.rightAnchor.constraintEqualToAnchor(container.rightAnchor, constant = -spacings.right)
        .setActive(true)
    this.bottomAnchor.constraintEqualToAnchor(
        container.bottomAnchor,
        constant = -spacings.bottom
    ).setActive(true)
}

fun UIView.centerInContainer(container: UIView) {
    assert(this.superview != null)

    this.translatesAutoresizingMaskIntoConstraints = false

    this.centerXAnchor.constraintEqualToAnchor(container.centerXAnchor).setActive(true)
    this.centerYAnchor.constraintEqualToAnchor(container.centerYAnchor).setActive(true)
}

fun UIView.centerInSuperview() {
    assert(this.superview != null)

    this.centerInContainer(this.superview!!)
}