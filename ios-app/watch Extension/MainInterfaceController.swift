//
//  MainInterfaceController.swift
//  watch Extension
//
//  Created by Anton Shelar on 12/11/2019.
//  Copyright © 2019 IceRock Development. All rights reserved.
//

import WatchKit
import Foundation


class MainInterfaceControllerA: WKInterfaceController {

    @IBOutlet var progressImage: WKInterfaceImage!
    @IBOutlet var progressLabel: WKInterfaceLabel!

    override func awake(withContext context: Any?) {
        super.awake(withContext: context)
        
        // Configure interface objects here.
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }

}
