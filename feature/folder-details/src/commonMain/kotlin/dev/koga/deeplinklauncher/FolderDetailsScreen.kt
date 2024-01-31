package dev.koga.deeplinklauncher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.koga.deeplinklauncher.component.DeleteFolderBottomSheet
import dev.koga.deeplinklauncher.deeplink.DeepLinkItem
import dev.koga.deeplinklauncher.folder.EditableText
import dev.koga.deeplinklauncher.model.DeepLink
import dev.koga.deeplinklauncher.provider.DeepLinkClipboardProvider
import dev.koga.deeplinklauncher.theme.LocalDimensions
import dev.koga.deeplinklauncher.usecase.deeplink.LaunchDeepLink
import dev.koga.navigation.SharedScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

class FolderDetailsScreen(private val folderId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = getScreenModel<FolderDetailsScreenModel>(
            parameters = { parametersOf(folderId) },
        )

        val launchDeepLink = koinInject<LaunchDeepLink>()
        val deepLinkClipboardProvider = koinInject<DeepLinkClipboardProvider>()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val state by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.deletedEvent.collectLatest { navigator.pop() }
        }

        var showDeleteDialog by remember { mutableStateOf(false) }
        if (showDeleteDialog) {
            DeleteFolderBottomSheet(
                onDismissRequest = { showDeleteDialog = false },
                onDelete = {
                    showDeleteDialog = false
                    screenModel.delete()
                },
            )
        }

        Scaffold(
            topBar = {
                DLLTopBar(onBack = navigator::pop, actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.Red.copy(alpha = .2f),
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                })
            },
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                FolderDetailsScreenContent(
                    form = state,
                    onEditName = screenModel::updateName,
                    onEditDescription = screenModel::updateDescription,
                    onDeepLinkClick = { deepLink ->
                        val screen = ScreenRegistry.get(SharedScreen.DeepLinkDetails(deepLink.id))
                        navigator.push(screen)
                    },
                    onDeepLinkLaunch = launchDeepLink::launch,
                    onDeepLinkCopy = {
                        scope.launch {
                            deepLinkClipboardProvider.copy(it.link)
                            snackbarHostState.showSnackbar(
                                message = "Copied to clipboard",
                                actionLabel = "Dismiss",
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun FolderDetailsScreenContent(
    form: FolderDetails,
    onEditName: (String) -> Unit,
    onEditDescription: (String) -> Unit,
    onDeepLinkClick: (DeepLink) -> Unit,
    onDeepLinkLaunch: (DeepLink) -> Unit,
    onDeepLinkCopy: (DeepLink) -> Unit,
) {
    val dimensions = LocalDimensions.current

    Column(modifier = Modifier.padding(horizontal = dimensions.extraLarge)) {
        Spacer(modifier = Modifier.height(dimensions.extraLarge))

        Text(
            text = "Name",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Light,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableText(
            value = form.name,
            onSave = onEditName,
            inputLabel = "Enter a name",
            editButtonEnabled = form.name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = form.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        Spacer(modifier = Modifier.height(dimensions.extraLarge))

        Text(
            text = "Description",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Light,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableText(
            value = form.description,
            onSave = onEditDescription,
            inputLabel = "Enter a description",
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = form.description.ifEmpty { "--" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                )
            )
        }

        Divider(modifier = Modifier.padding(vertical = dimensions.extraLarge))

        FolderDeepLinks(
            deepLinks = form.deepLinks,
            onClick = onDeepLinkClick,
            onLaunch = onDeepLinkLaunch,
            onCopy = onDeepLinkCopy,
        )
    }
}

@Composable
private fun FolderDeepLinks(
    deepLinks: ImmutableList<DeepLink>,
    onClick: (DeepLink) -> Unit,
    onLaunch: (DeepLink) -> Unit,
    onCopy: (DeepLink) -> Unit,
) {
    val dimensions = LocalDimensions.current

    if (deepLinks.isEmpty()) {
        Text(
            text = "No deeplinks vinculated to this folder",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
            ),
        )
    } else {
        Text(
            text = "Deeplinks",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(dimensions.extraLarge))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensions.extraLarge),
        ) {
            items(deepLinks) { deepLink ->
                DeepLinkItem(
                    deepLink = deepLink,
                    onClick = onClick,
                    onLaunch = onLaunch,
                    onCopy = onCopy,
                )
            }

            item {
                Spacer(modifier = Modifier.height(dimensions.extraLarge))
            }
        }
    }
}
