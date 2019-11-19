//
//  MainInterfaceController.swift
//  watch Extension
//
//  Created by Anton Shelar on 12/11/2019.
//  Copyright © 2019 IceRock Development. All rights reserved.
//

import WatchKit
import Foundation
import MultiPlatformLibrary


class MainInterfaceController: WKInterfaceController, CollectWordViewModelEventsListener {
    @IBOutlet private var progressImage: WKInterfaceImage!
    @IBOutlet private var progressLabel: WKInterfaceLabel!
    @IBOutlet private var tasksCompletedGroup: WKInterfaceGroup!
    @IBOutlet private var collectWordGroup: WKInterfaceGroup!
    @IBOutlet private var findTaskButton: WKInterfaceButton!

    private weak var viewModel: CollectWordViewModel?

    override func awake(withContext context: Any?) {
        super.awake(withContext: context)

        self.viewModel = Factory.Companion.init().shared.createCollectWordViewModel(eventsListener: self)

        self.setStepsCount(count: Int(self.viewModel?.currentStep ?? 0))
        self.findTaskButton.setEnabled(false)
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        super.didDeactivate()

        self.viewModel?.clear()
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

    @IBAction private func findTaskButtonTapped() {
        self.pushController(withName: "search", context: nil)
    }

    func didChangeCurrentStep(newStep: Int32) {
        self.setStepsCount(count: Int(newStep))
    }

    func showCompletedGameAlert() {
        self.collectWordGroup.setHidden(true)
        self.tasksCompletedGroup.setHidden(false)
    }

    func setFindTaskButtonEnabled(enabled: Bool) {
        self.findTaskButton.setEnabled(enabled)
    }
}
