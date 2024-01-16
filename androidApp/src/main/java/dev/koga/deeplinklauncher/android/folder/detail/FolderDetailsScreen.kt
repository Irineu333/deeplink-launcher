package dev.koga.deeplinklauncher.android.folder.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.koga.deeplinklauncher.android.core.designsystem.DLLTopBar
import dev.koga.deeplinklauncher.model.DeepLink
import dev.koga.deeplinklauncher.model.Folder
import org.koin.core.parameter.parametersOf

class FolderDetailsScreen(private val folderId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<FolderDetailsScreenModel>(
            parameters = { parametersOf(folderId) }
        )

        val state by screenModel.state.collectAsState()

        if ((state as? FolderDetailsScreenState.Loaded)?.deleted == true) {
            navigator.pop()
        }

        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showDeleteDialog) {

            ModalBottomSheet(
                onDismissRequest = { showDeleteDialog = false },
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
            ) {
                Column {
                    Text(
                        text = "Delete folder",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(24.dp)
                    )

                    HorizontalDivider()

                    Text(
                        text = "Are you sure you want to delete this deep link?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(24.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {


                        TextButton(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(text = "Cancel", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        FilledTonalButton(
                            onClick = {
                                showDeleteDialog = false
                                screenModel.deleteFolder()
                            },
                            modifier = Modifier.padding(end = 12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Delete",
                                fontWeight = FontWeight.Bold
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

        }

        Scaffold(
            topBar = {
                DLLTopBar(onBack = navigator::pop, actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.Red.copy(alpha = .2f)
                        )
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
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                when (state) {
                    is FolderDetailsScreenState.Loading -> Unit
                    is FolderDetailsScreenState.Loaded -> FolderDetailsScreenContent(
                        folder = (state as FolderDetailsScreenState.Loaded).folder,
                        deepLinks = (state as FolderDetailsScreenState.Loaded).deepLinks,
                    )
                }
            }

        }
    }
}


@Composable
fun FolderDetailsScreenContent(
    folder: Folder,
    deepLinks: List<DeepLink>,
) {

    Column(modifier = Modifier.padding(24.dp)) {

        Text(
            text = folder.name, style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        folder.description?.let { description ->
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (deepLinks.isEmpty()) {
            Text(
                text = "No deeplinks vinculated to this folder",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        } else {

            Text(
                text = "Deeplinks",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}