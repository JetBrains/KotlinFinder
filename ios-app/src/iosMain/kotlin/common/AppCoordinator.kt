package common

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import com.icerockdev.jetfinder.feature.mainMap.presentation.SplashViewModel
import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import org.example.library.Factory
import screens.MainScreenViewController
import screens.SpotSearchViewController
import screens.SplashViewController
import org.example.library.domain.entity.GameConfig
import platform.UIKit.*


open class BasicCoordinator(
    protected val window: UIWindow,
    protected val factory: Factory
) {
    protected val navigationController: UINavigationController = UINavigationController()

    open fun start() {}

    fun popBack() {
        this.navigationController.popToRootViewControllerAnimated(true)
    }
}


class AppCoordinator(
    window: UIWindow,
    factory: Factory
) : BasicCoordinator(window, factory), MapViewModel.EventsListener, SplashViewModel.EventsListener {
    private val mapViewModel: MapViewModel = this.factory.mapFactory.createMapViewModel(EventsDispatcher(this))

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

    private fun createSpotSearchScreen(): SpotSearchViewController {
        val vc: SpotSearchViewController = SpotSearchViewController()
        val vm: SpotSearchViewModel = this.factory.spotSearchFactory.createSpotSearchViewModel()

        vc.bindViewModel(vm)

        return vc
    }

    private fun createSplashScreen(): SplashViewController {
        val vc: SplashViewController = SplashViewController()
        val vm: SplashViewModel = this.factory.mapFactory.createSplashViewModel(EventsDispatcher(this))

        vc.bindViewModel(vm)
        vm.loadData()

        return vc
    }

    override fun showSpotSearchScreen() {
        this.navigationController.pushViewController(this.createSpotSearchScreen(), animated = true)
    }

    override fun gameConfigLoaded(config: GameConfig?) {
        if (config == null) {
            // TODO: handle error
        } else {
            this.navigationController.setNavigationBarHidden(false)
            this.navigationController.setViewControllers(
                listOf(this.createMainScreen()),
                animated = true
            )
        }
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

                    this.mapViewModel.sendWinnerName(name = text, completion = { message: String? ->
                        if (message == null)
                            return@sendWinnerName

                        this.showAlert(message)
                    })
                }
            ))

        this.navigationController.topViewController?.presentViewController(alert, animated = true, completion = null)
    }

    private fun showAlert(text: String) {
        val alert: UIAlertController = UIAlertController.alertControllerWithTitle(
            title = null,
            message = text,
            preferredStyle = UIAlertControllerStyleAlert
        )

        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = "Ok",
                style = UIAlertActionStyleCancel,
                handler = {
                    alert.dismissViewControllerAnimated(true, completion = null)
                }
            ))

        this.navigationController.topViewController?.presentViewController(alert, animated = true, completion = null)
    }
}