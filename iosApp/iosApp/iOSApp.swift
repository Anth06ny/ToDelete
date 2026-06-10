import SwiftUI
import Shared

@main
struct iOSApp: App {

    init(){
        //initKoin() dans le fichier koin.kt donne
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}