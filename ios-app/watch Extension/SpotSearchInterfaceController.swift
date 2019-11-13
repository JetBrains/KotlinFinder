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


class SpotSearchInterfaceController: WKInterfaceController {
    @IBOutlet private var findTaskButton: WKInterfaceButton!
    @IBOutlet private var searchScene: WKInterfaceSKScene!

    override func awake(withContext context: Any?) {
        super.awake(withContext: context)

        self.setFindTask()
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }

    private func setSpotSearch() {
        // TODO: self.animate(withDuration: 0.2) {}

        self.findTaskButton.setHidden(true)
        self.searchScene.setHidden(false)
        self.searchScene.isPaused = false

        // TODO: configure animated bars scene
    }

    private func setSpotFound() {
        // TODO: self.animate(withDuration: 0.2) {}

        self.findTaskButton.setBackgroundImage(UIImage(named: "spotFound"))
        self.findTaskButton.setHidden(false)
        self.searchScene.setHidden(true)
        self.searchScene.isPaused = true
    }

    private func setFindTask() {
        self.findTaskButton.setHidden(false)
        self.searchScene.setHidden(true)
    }
}
