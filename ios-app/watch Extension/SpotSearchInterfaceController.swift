//
//  SpotSearchInterfaceController.swift
//  watch Extension
//
//  Created by Anton Shelar on 13/11/2019.
//  Copyright © 2019 IceRock Development. All rights reserved.
//

import WatchKit
import Foundation
import SpriteKit
import MultiPlatformLibrary


class SpotSearchInterfaceController: WKInterfaceController, SpotSearchViewModelEventsListener {
    @IBOutlet private var spotFoundInterfaceImage: WKInterfaceImage!
    @IBOutlet private var searchScene: WKInterfaceSKScene!

    private var searchSceneProxy: SpotDistanceSceneProxy = SpotDistanceSceneProxy()
    private weak var viewModel: SpotSearchViewModel?

    override func awake(withContext context: Any?) {
        super.awake(withContext: context)

        self.viewModel = Factory.Companion.init().shared.createSpotSearchViewModel(eventsListener: self)

        self.searchSceneProxy.presentOnScene(superScene: self.searchScene)

        self.setSpotSearch()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()

        self.viewModel?.clear()
    }

    private func setSpotSearch() {
        // TODO: self.animate(withDuration: 0.2) {}

        self.spotFoundInterfaceImage.setHidden(true)
        self.searchScene.setHidden(false)
        self.searchScene.isPaused = false

        // TODO: configure animated bars scene
    }

    private func setSpotFound() {
        // TODO: self.animate(withDuration: 0.2) {}

        self.spotFoundInterfaceImage.setHidden(false)
        self.searchScene.setHidden(true)
        self.searchScene.isPaused = true
    }

    func didChangeDistance(distance: KotlinInt?) {
        print("change dist: \(String(describing: distance?.intValue))")
        DispatchQueue.main.async {
            self.searchSceneProxy.setDistance(distance: Float(distance?.intValue ?? 0) / 100.0)
        }
    }

    func didFoundSpot() {
        WKInterfaceDevice.current().play(.success)

        self.setSpotFound()
    }
}
