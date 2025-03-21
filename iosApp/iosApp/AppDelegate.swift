//
//  AppDelegate.swift
//  iosApp
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import UIKit
import shared


class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Initialize Koin
        shared.KoinHelper.initialize()
        return true
    }
}
