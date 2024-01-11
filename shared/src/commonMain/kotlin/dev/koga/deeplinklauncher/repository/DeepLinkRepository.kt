package dev.koga.deeplinklauncher.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.koga.deeplinklauncher.database.DeepLinkLauncherDatabase
import dev.koga.deeplinklauncher.model.DeepLink
import dev.koga.deeplinklauncher.model.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeepLinkRepository(
    private val database: DeepLinkLauncherDatabase
) {

    fun getAllDeepLinks(): Flow<List<DeepLink>> {
        return database.deepLinkLauncherDatabaseQueries
            .selectDeeplinks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { data ->
                    DeepLink(
                        id = data.id,
                        link = data.link,
                        name = data.name,
                        description = data.description,
                        createdAt = data.createdAt,
                        updatedAt = data.updatedAt,
                        isFavorite = data.isFavorite == 1L,
                        folder = data.folderId?.let {  folderId ->
                            Folder(
                                id = folderId,
                                name = data.name_.orEmpty(),
                                description = data.description_,
                                color = data.color
                            )
                        }
                    )
                }
            }
    }

    fun getDeepLinkByLink(link: String): Flow<DeepLink?> {
        return database.deepLinkLauncherDatabaseQueries
            .getDeepLinkByLink(link)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.firstOrNull()?.let { data ->
                    DeepLink(
                        id = data.id,
                        link = data.link,
                        name = data.name,
                        description = data.description,
                        createdAt = data.createdAt,
                        updatedAt = data.updatedAt,
                        isFavorite = data.isFavorite == 1L,
                        folder = data.folderId?.let {  folderId ->
                            Folder(
                                id = folderId,
                                name = data.name_.orEmpty(),
                                description = data.description_,
                                color = data.color
                            )
                        }
                    )
                }
            }
    }

    fun insertDeeplink(deepLink: DeepLink) {
        database.transaction {
            deepLink.folder?.let {
                database.deepLinkLauncherDatabaseQueries.upsertFolder(
                    id = deepLink.folder.id,
                    name = deepLink.folder.name,
                    description = deepLink.folder.description,
                    color = deepLink.folder.color
                )
            }

            database.deepLinkLauncherDatabaseQueries.upsertDeeplink(
                id = deepLink.id,
                link = deepLink.link,
                name = deepLink.name,
                description = deepLink.description,
                createdAt = deepLink.createdAt,
                updatedAt = deepLink.updatedAt,
                isFavorite = if (deepLink.isFavorite) 1L else 0L,
                folderId = deepLink.folder?.id
            )
        }
    }

    fun deleteDeeplink(deepLink: DeepLink) {
        database.transaction {
            database.deepLinkLauncherDatabaseQueries.deleteDeeplinkById(deepLink.id)
        }
    }
}