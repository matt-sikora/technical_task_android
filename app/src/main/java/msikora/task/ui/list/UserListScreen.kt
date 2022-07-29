package msikora.task.ui.list

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import msikora.task.domain.User
import msikora.task.core.CallState
import msikora.task.core.extractSingleMessage
import msikora.task.domain.Gender
import msikora.task.ui.Colors
import msikora.task.ui.Routes
import msikora.task.ui.common.ErrorView
import msikora.task.ui.common.ProgressView

@Composable
fun UserListScreen(navController: NavController, viewModel: UserListViewModel = hiltViewModel()) {
    var pendingUserToDelete by rememberSaveable { mutableStateOf<User?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.messages) {
        viewModel.messages.collectLatest { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    // no-op
                }
                SnackbarResult.Dismissed -> {
                    // no-op
                }
            }
        }
    }
    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = "+",
                        color = Color.White,
                    )
                },
                onClick = {
                    navController.navigate(Routes.create)
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
        ) {
            MaybeDeleteDialog(
                onDismissRequest = { pendingUserToDelete = null },
                onDeleteRequest = { viewModel.delete(it) },
                pendingUserSnapshot = pendingUserToDelete
            )
            ItemsList(viewModel) { pendingUserToDelete = it }
        }
    }
}

@Composable
fun MaybeDeleteDialog(
    onDismissRequest: () -> Unit,
    onDeleteRequest: (User) -> Unit,
    pendingUserSnapshot: User?
) {
    if (pendingUserSnapshot != null) {
        AlertDialog(
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = "Are you sure you want to remove this user?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteRequest(pendingUserSnapshot)
                        onDismissRequest()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismissRequest() }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun ItemsList(
    viewModel: UserListViewModel,
    onLongPressed: (User) -> Unit,
) {
    val items by viewModel.users.collectAsState(emptyList())
    when (val fetchingState = viewModel.fetchingState.collectAsState(CallState.Loading).value) {
        is CallState.Error -> ErrorView(
            viewModel::retryFetching,
            fetchingState.extractSingleMessage()
        )
        CallState.Loading -> ProgressView()
        is CallState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items) { userItem ->
                    UserItemCell(userItem, onLongPressed)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItemCell(item: User, onLongPressed: (User) -> Unit) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    Toast
                        .makeText(context, "Long press to delete", Toast.LENGTH_SHORT)
                        .show()
                },
                onLongClick = {
                    onLongPressed(item)
                },
            )
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (item.isActive) {
                        Colors.green
                    } else {
                        Colors.red
                    }
                )
                .size(48.dp)
        ) {
            Text(
                color = Color.White,
                text = when (item.gender) {
                    Gender.Male -> "M"
                    Gender.Female -> "F"
                }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Colors.grey, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                text = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.lightGrey)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Divider(color = Colors.grey)
            Text(
                text = item.email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}
