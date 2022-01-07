package ru.ra66it.updaterforspotify.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.R

@Composable
fun LoadingScreen(title: String, subTitle: String) {
    val primaryDark = colorResource(id = R.color.colorPrimaryDark)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryDark),
    ) {
        VersionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            title = title,
            subTitle = subTitle
        )
        LoadingCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 0.dp, 32.dp, 32.dp),
            color = primaryDark
        )
    }
}

@Composable
fun LatestVersionScreen(title: String, subTitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark)),
    ) {
        VersionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            title = title,
            subTitle = subTitle
        )
    }
}

@Composable
fun NewVersionScreen(
    snackbarHostState: State<SnackbarHostState>,
    installedTitle: String,
    installedVersion: String,
    latestTitle: String,
    latestVersion: String,
    buttonTitle: String,
    buttonIcon: Int,
    clickCallback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark)),
    ) {
        VersionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            title = installedTitle,
            subTitle = installedVersion
        )
        VersionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 0.dp, 32.dp, 32.dp),
            title = latestTitle,
            subTitle = latestVersion
        )
        ExtendedFloatingActionButton(
            text = { Text(text = buttonTitle) },
            icon = {
                Icon(
                    painter = painterResource(id = buttonIcon),
                    ""
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { clickCallback.invoke() },
            backgroundColor = colorResource(id = R.color.colorAccent)
        )
        Snackbar(snackbarHostState)
    }
}

@Composable
fun ErrorScreen(
    snackbarHostState: MutableState<SnackbarHostState>,
    title: String,
    subTitle: String,
    errorMessage: String,
    cardClickCallback: () -> Unit,
    lifecycleOwner: LifecycleOwner
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark)),
    ) {
        VersionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            title = title,
            subTitle = subTitle
        )
        RetryCard(modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 0.dp, 32.dp, 32.dp)
            .clickable { cardClickCallback.invoke() })
        Snackbar(snackbarHostState)
        lifecycleOwner.lifecycleScope.launch {
            snackbarHostState.value.showSnackbar(errorMessage)
        }
    }

}

@Composable
private fun VersionCard(modifier: Modifier = Modifier, title: String, subTitle: String) {
    Card(
        modifier = modifier,
        shape = Shape(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subTitle,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
private fun RetryCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = Shape(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.ic_autorenew_black_24dp),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun LoadingCard(modifier: Modifier = Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = Shape(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = color
            )
        }
    }
}

@Composable
private fun Snackbar(snackbarHostState: State<SnackbarHostState>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackbarHostState.value
        )
    }
}

@Composable
private fun Shape() = RoundedCornerShape(8.dp)