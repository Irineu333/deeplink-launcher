package dev.koga.deeplinklauncher.usecase.deeplink

import dev.koga.deeplinklauncher.util.constant.defaultDeepLink
import dev.koga.deeplinklauncher.datasource.DeepLinkDataSource

class GetDeepLinksPlainTextPreview(
    private val dataSource: DeepLinkDataSource,
) {
    operator fun invoke(): String {
        val deepLinks = dataSource.getDeepLinks().filter {
            it.id != defaultDeepLink.id
        }

        return deepLinks.joinToString(separator = "\n") { deepLink ->
            deepLink.link
        }
    }
}
