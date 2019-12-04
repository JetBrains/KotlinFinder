package common

import com.kotlinconf.library.feature.mainMap.presentation.MapViewModel
import com.kotlinconf.library.feature.mainMap.presentation.SplashViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.permissions.PermissionsController
import com.kotlinconf.library.Factory
import screens.MainScreenViewController
import screens.SplashViewController
import platform.UIKit.*


open class BasicCoordinator(
    protected val window: UIWindow
) {
    protected val navigationController: UINavigationController = UINavigationController()

    open fun start() {
        this.window.rootViewController = this.navigationController
        this.window.makeKeyAndVisible()
    }

    fun popBack() {
        this.navigationController.popToRootViewControllerAnimated(true)
    }
}


class AppCoordinator(
    window: UIWindow,
    factory: Factory
) : BasicCoordinator(window, factory), MapViewModel.EventsListener, SplashViewModel.EventsListener {
    private val mapViewModel: MapViewModel = this.factory.mapFactory.createMapViewModel(EventsDispatcher(this), PermissionsController())

    override fun start() {
        this.window.tintColor = Colors.orange

        this.navigationController.setViewControllers(
            listOf(this.createSplashScreen()),
            animated = false
        )
        this.navigationController.navigationBar.shadowImage = UIImage()
        this.navigationController.setNavigationBarHidden(true)

        this.window.rootViewController = this.navigationController
        this.window.makeKeyAndVisible()
    }

    private fun createMainScreen(): MainScreenViewController {
        val vc: MainScreenViewController = MainScreenViewController()
        vc.bindViewModel(this.mapViewModel)

        return vc
    }

    private fun createSplashScreen(): SplashViewController {
        val vc: SplashViewController = SplashViewController()
        val vm: SplashViewModel = this.factory.mapFactory.createSplashViewModel(EventsDispatcher(this))

        vc.bindViewModel(vm)

        return vc
    }

    override fun routeToMainscreen() {
        this.navigationController.setNavigationBarHidden(false)
        this.navigationController.setViewControllers(
            listOf(this.createMainScreen()),
            animated = true
        )
    }

    override fun showError(error: Throwable, retryingAction: (() -> Unit)?) {
        this.showRetryAlert(error.toString(), action = retryingAction)
    }

    override fun showHint(hint: String) {
        val alert: UIAlertController = UIAlertController.alertControllerWithTitle(
            title = hint,
            message = null,
            preferredStyle = UIAlertControllerStyleAlert
        )

        alert.addAction(UIAlertAction.actionWithTitle(
            title = "Ok",
            style = UIAlertActionStyleCancel,
            handler = {
                alert.dismissViewControllerAnimated(true, completion = null)
            }
        ))

        this.navigationController.topViewController?.presentViewController(alert, animated = true, completion = null)
    }

    override fun showEnterNameAlert() {
        val alert: UIAlertController = UIAlertController.alertControllerWithTitle(
            title = "Enter your name",
            message = null,
            preferredStyle = UIAlertControllerStyleAlert
        )

        alert.addTextFieldWithConfigurationHandler(null)

        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = "Ok",
                style = UIAlertActionStyleDefault,
                handler = {
                    val text: String = (alert.textFields?.first() as? UITextField)?.text ?: return@actionWithTitle
                    alert.dismissViewControllerAnimated(true, completion = null)

                    this.mapViewModel.sendWinnerName(name = text)
                }
            ))

        this.navigationController.topViewController?.presentViewController(alert, animated = true, completion = null)
    }

    override fun showRegistrationMessage(message: String) {
        this.showAlert(message, action = null)
    }

    override fun showResetCookiesAlert(resetAction: () -> Unit) {
        this.showAlert(
            text = "Reset cookies?",
            buttonTitle = "Reset",
            addCancelButton = true,
            action = resetAction
        )
    }

    override fun showFact(fact: String, closeAction: () -> Unit) {
        this.showAlert(
            text = fact,
            action = closeAction
        )
    }

    private fun showAlert(text: String,
                          buttonTitle: String = "Ok",
                          addCancelButton: Boolean = false,
                          action: (() -> Unit)?) {
        val alert: UIAlertController = UIAlertController.alertControllerWithTitle(
            title = null,
            message = text,
            preferredStyle = UIAlertControllerStyleAlert
        )

        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = buttonTitle,
                style = UIAlertActionStyleDefault,
                handler = {
                    action?.invoke()
                    alert.dismissViewControllerAnimated(true, completion = null)
                }
            ))

        if (addCancelButton) {
            alert.addAction(
                UIAlertAction.actionWithTitle(
                    title = "Cancel",
                    style = UIAlertActionStyleCancel,
                    handler = {
                        alert.dismissViewControllerAnimated(true, completion = null)
                    }
                ))
        }

        this.navigationController.topViewController?.presentViewController(alert, animated = true, completion = null)
    }

    private fun showRetryAlert(text: String, action: (() -> Unit)?) {
        this.showAlert(text = text, buttonTitle = "Retry", action = action)
    }
}