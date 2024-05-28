package dev.koga.deeplinklauncher.preferences.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun dataStorePreferences(
    context: Any?
): DataStore<Preferences> = createDataStore(
    path = {
        File((context as Context).filesDir, "datastore/$dataStoreFileName").path
    },
)