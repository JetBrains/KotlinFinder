package common

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import com.icerockdev.jetfinder.feature.mainMap.presentation.SplashViewModel
import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import org.example.library.Factory
import platform.UIKit.UIImage
import platform.UIKit.UINavigationController
import platform.UIKit.UIWindow
import platform.UIKit.tintColor
import screens.MainScreenViewController
import screens.SpotSearchViewController
import screens.SplashViewController
import org.example.library.domain.entity.GameConfig


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
        val vm: MapViewModel = this.factory.mapFactory.createMapViewModel(EventsDispatcher(this))

        vc.bindViewModel(vm)

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
}