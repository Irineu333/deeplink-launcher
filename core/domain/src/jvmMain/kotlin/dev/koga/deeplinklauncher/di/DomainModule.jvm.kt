package dev.koga.deeplinklauncher.di

import dev.koga.deeplinklauncher.datasource.TargetDataSource
import dev.koga.deeplinklauncher.manager.AdbManager
import dev.koga.deeplinklauncher.manager.AdbManagerImpl
import dev.koga.deeplinklauncher.platform.GetFileContent
import dev.koga.deeplinklauncher.platform.PlatformInfo
import dev.koga.deeplinklauncher.platform.SaveFile
import dev.koga.deeplinklauncher.platform.ShareFile
import dev.koga.deeplinklauncher.provider.UUIDProvider
import dev.koga.deeplinklauncher.usecase.GetDeepLinkMetadata
import dev.koga.deeplinklauncher.usecase.GetDeviceType
import dev.koga.deeplinklauncher.usecase.LaunchDeepLink
import dev.koga.deeplinklauncher.usecase.ShareDeepLink
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDomainModule: Module = module {
    singleOf(::LaunchDeepLink)
    singleOf(::ShareDeepLink)
    singleOf(::SaveFile)
    singleOf(::ShareFile)
    singleOf(::GetFileContent)
    singleOf(::GetDeepLinkMetadata)
    singleOf(::PlatformInfo)
    singleOf(::TargetDataSource)
    singleOf(::GetDeviceType)

    single { AdbManagerImpl.build() } bind AdbManager::class
    single { UUIDProvider }
}
