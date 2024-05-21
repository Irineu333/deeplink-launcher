package dev.koga.deeplinklauncher.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.koga.deeplinklauncher.datasource.PreferencesDataSource
import dev.koga.deeplinklauncher.model.AppTheme
import dev.koga.deeplinklauncher.model.Preferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import androidx.datastore.preferences.core.Preferences as DataStorePreferences

class PreferencesDataStore(
    private val dataStore: DataStore<DataStorePreferences>,
) : PreferencesDataSource {

    private val themeKey = stringPreferencesKey("theme")
    private val shouldShowOnboarding = booleanPreferencesKey("should_show_onboarding")
    private val shouldDisableDeepLinkSuggestions =
        booleanPreferencesKey("should_disable_deep_link_suggestions")

    override val preferencesStream = dataStore.data.map {
        Preferences(
            shouldShowOnboarding = it[shouldShowOnboarding] ?: true,
            appTheme = AppTheme.get(it[themeKey]),
            shouldDisableDeepLinkSuggestions = it[shouldDisableDeepLinkSuggestions] ?: false,
        )
    }

    override val preferences = runBlocking { preferencesStream.firstOrNull() ?: Preferences() }

    override suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit {
            it[themeKey] = theme.name
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        dataStore.edit {
            it[shouldShowOnboarding] = !shouldHideOnboarding
        }
    }

    override suspend fun setShouldDisableDeepLinkSuggestions(
        shouldDisableDeepLinkSuggestions: Boolean,
    ) {
        dataStore.edit {
            it[this.shouldDisableDeepLinkSuggestions] = shouldDisableDeepLinkSuggestions
        }
    }
}
