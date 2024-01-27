package dev.koga.deeplinklauncher.usecase

import dev.koga.deeplinklauncher.model.DeepLink
import dev.koga.deeplinklauncher.model.Folder
import dev.koga.deeplinklauncher.provider.UUIDProvider
import kotlinx.datetime.Clock

expect class ImportDeepLinks {
    fun invoke(filePath: String, fileType: FileType): ImportDeepLinksOutput
}

sealed interface ImportDeepLinksOutput {
    data object Success : ImportDeepLinksOutput

    sealed interface Error : ImportDeepLinksOutput {
        data class InvalidDeepLinksFound(val invalidDeepLinks: List<String>) : Error
        data object Unknown : Error
    }
}

fun String.toDeepLink(): DeepLink {
    return DeepLink(
        id = UUIDProvider.provide(),
        createdAt = Clock.System.now(),
        link = this,
        name = null,
        description = null,
        isFavorite = false,
        folder = null,
    )
}

fun ImportDeepLinkDto.toDeepLink() = DeepLink(
    id = this.id ?: UUIDProvider.provide(),
    createdAt = Clock.System.now(),
    link = this.link,
    name = this.name,
    description = this.description,
    isFavorite = this.isFavorite ?: false,
    folder = this.folder?.let { folder ->
        Folder(
            id = folder.id ?: UUIDProvider.provide(),
            name = folder.name,
            description = folder.description,
            deepLinkCount = 0
        )
    }
)

fun ImportFolderDto.toFolder() = Folder(
    id = id ?: UUIDProvider.provide(),
    name =name,
    description = description,
)