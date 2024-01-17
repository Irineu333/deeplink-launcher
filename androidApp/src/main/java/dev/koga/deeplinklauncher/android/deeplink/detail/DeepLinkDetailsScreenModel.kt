package dev.koga.deeplinklauncher.android.deeplink.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.koga.deeplinklauncher.model.DeepLink
import dev.koga.deeplinklauncher.model.Folder
import dev.koga.deeplinklauncher.repository.DeepLinkRepository
import dev.koga.deeplinklauncher.repository.FolderRepository
import dev.koga.deeplinklauncher.usecase.LaunchDeepLink
import dev.koga.deeplinklauncher.usecase.ShareDeepLink
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class DeepLinkDetailScreenModel(
    deepLinkId: String,
    private val deepLinkRepository: DeepLinkRepository,
    private val folderRepository: FolderRepository,
    private val launchDeepLink: LaunchDeepLink,
    private val shareDeepLink: ShareDeepLink,
) : ScreenModel {

    private val deepLink = deepLinkRepository.getDeepLinkById(deepLinkId)

    val folders = folderRepository.getFolders().stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )


    val details = MutableStateFlow(
        DeepLinkDetails(
            id = deepLinkId,
            name = deepLink.name.orEmpty(),
            description = deepLink.description.orEmpty(),
            folder = deepLink.folder,
            link = deepLink.link,
            isFavorite = deepLink.isFavorite,
            deleted = false,
        )
    )

    init {
        details.onEach {
            deepLinkRepository.upsert(
                deepLink.copy(
                    name = it.name,
                    description = it.description,
                    folder = it.folder,
                    isFavorite = it.isFavorite,
                )
            )
        }.launchIn(screenModelScope)

    }

    fun updateDeepLinkName(s: String) {
        details.update {
            it.copy(name = s)
        }
    }

    fun updateDeepLinkDescription(s: String) {
        details.update {
            it.copy(description = s)
        }
    }

    fun favorite() {
        details.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun launch() {
        launchDeepLink.launch(deepLink.link)
    }

    fun delete() {
        screenModelScope.launch {
            details.update { it.copy(deleted = true) }

            deepLinkRepository.deleteDeeplink(deepLink)
        }
    }

    fun share() {
        shareDeepLink(deepLink)
    }

    fun insertFolder(name: String, description: String) {
        screenModelScope.launch {
            val folder = Folder(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
            )

            folderRepository.upsertFolder(folder)

            selectFolder(folder)
        }
    }

    fun selectFolder(folder: Folder) {
        details.update { it.copy(folder = folder) }
    }

    fun removeFolderFromDeepLink() {
        details.update { it.copy(folder = null) }
    }

}

data class DeepLinkDetails(
    val id: String,
    val name: String,
    val description: String,
    val folder: Folder?,
    val link: String,
    val isFavorite: Boolean,
    val deleted: Boolean,
)
