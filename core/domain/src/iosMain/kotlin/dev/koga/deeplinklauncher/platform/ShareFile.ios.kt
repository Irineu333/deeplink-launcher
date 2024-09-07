package dev.koga.deeplinklauncher.platform

import dev.koga.deeplinklauncher.model.FileType
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController

actual class ShareFile {
    @OptIn(ExperimentalForeignApi::class)
    actual operator fun invoke(filePath: String, fileType: FileType) {
        val fileUrl = NSURL.fileURLWithPath(filePath)
        val controller = UIDocumentInteractionController.interactionControllerWithURL(fileUrl)

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.let {
            controller.presentOptionsMenuFromRect(it.view.bounds, it.view, true)
        } ?: run {
            println("Unable to find root view controller")
        }
    }
}
