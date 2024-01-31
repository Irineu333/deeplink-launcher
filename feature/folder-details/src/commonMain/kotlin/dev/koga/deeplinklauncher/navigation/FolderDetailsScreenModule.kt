package dev.koga.deeplinklauncher.navigation

import cafe.adriel.voyager.core.registry.screenModule
import dev.koga.deeplinklauncher.FolderDetailsScreen
import dev.koga.deeplinklauncher.SharedScreen

val folderDetailsScreenModule = screenModule {
    register<SharedScreen.FolderDetails> { provider -> FolderDetailsScreen(provider.id) }
}
