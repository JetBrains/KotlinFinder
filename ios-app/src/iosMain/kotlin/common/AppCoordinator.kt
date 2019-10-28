package common

import screens.MainScreenViewController
import screens.SpotSearchViewController
import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import org.example.library.Factory
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
): BasicCoordinator(window, factory), MapViewModel.EventsListener {

    override fun start() {
        // TODO: set navbar tint color
        this.navigationController.setViewControllers(listOf(this.createMainScreen()), animated = false)
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

    override fun showSpotSearchScreen() {
        this.navigationController.pushViewController(this.createSpotSearchScreen(), animated = true)
    }
}