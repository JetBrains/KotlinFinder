//
//  MainInterfaceController.swift
//  watch Extension
//
//  Created by Anton Shelar on 12/11/2019.
//  Copyright © 2019 IceRock Development. All rights reserved.
//

import WatchKit
import Foundation


class MainInterfaceController: WKInterfaceController {
    @IBOutlet private var progressImage: WKInterfaceImage!
    @IBOutlet private var progressLabel: WKInterfaceLabel!

    override func awake(withContext context: Any?) {
        super.awake(withContext: context)

        self.setStepsCount(count: 0)
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }

    private func setStepsCount(count: Int) {
        self.progressImage.setImageNamed("kotlin\(count)")

        self.progressLabel.setAttributedText(self.createCollectedString("KOTLIN", rangeEnd: count))
    }

    private func createCollectedString(_ str: String, rangeEnd: Int) -> NSAttributedString {
        let attributedString: NSMutableAttributedString = NSMutableAttributedString(
            string: str,
            attributes: [.font: UIFont.boldSystemFont(ofSize: 30.0), .foregroundColor: UIColor.white.withAlphaComponent(0.2)])

        attributedString.addAttributes([.foregroundColor: UIColor.white], range: NSMakeRange(0, rangeEnd))

        return attributedString
    }

    private func presentCompletionAlert() {
        self.presentAlert(withTitle: "All tasks were completed",
                          message: "Please go to the mobile application to enter your name",
                          preferredStyle: .alert,
                          actions: [WKAlertAction(title: "Ok", style: .cancel, handler: {})])
    }
}
