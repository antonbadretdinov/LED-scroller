package com.scroller.ledtext.ui.screens.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.scroller.ledtext.R
import com.scroller.ledtext.data.Colors
import com.scroller.ledtext.data.dataStore
import com.scroller.ledtext.data.speedList
import com.scroller.ledtext.helpers.BACKGROUND_COLOR_DATASTORE_NAME
import com.scroller.ledtext.helpers.LedFont
import com.scroller.ledtext.helpers.SCROLLER_TEXT_DATASTORE_NAME
import com.scroller.ledtext.helpers.SPEED_TEXT_DATASTORE_NAME
import com.scroller.ledtext.helpers.TEXT_COLOR_DATASTORE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    onStartClicked: (textLength: String, speed: String) -> Unit,
) {
    val context = LocalContext.current
    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    val coroutineScope = rememberCoroutineScope()

    val usedTextColorKey = longPreferencesKey(TEXT_COLOR_DATASTORE_NAME)
    val usedBackgroundColorKey = longPreferencesKey(BACKGROUND_COLOR_DATASTORE_NAME)
    val scrollerTextKey = stringPreferencesKey(SCROLLER_TEXT_DATASTORE_NAME)
    val usedSpeedKey = floatPreferencesKey(SPEED_TEXT_DATASTORE_NAME)

    var usedTextColorState by rememberSaveable {
        mutableLongStateOf(Colors.WHITE.color.value.toLong())
    }

    var usedBackgroundColorState by rememberSaveable {
        mutableLongStateOf(Colors.BLACK.color.value.toLong())
    }

    var usedSpeed by rememberSaveable {
        mutableFloatStateOf(1f)
    }

    val textMeasurer = rememberTextMeasurer()

    var textInputState by rememberSaveable {
        mutableStateOf("Example text")
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            context.dataStore.data.collectLatest { prefs ->
                textInputState = prefs[scrollerTextKey] ?: "Example text"
                usedSpeed = prefs[usedSpeedKey] ?: 1f
                usedTextColorState = prefs[usedTextColorKey] ?: Colors.WHITE.color.value.toLong()
                usedBackgroundColorState =
                    prefs[usedBackgroundColorKey] ?: Colors.BLACK.color.value.toLong()
            }
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp

    var textWidthState by remember {
        mutableStateOf(Dp(0f))
    }

    var measureTextWidth by remember {
        mutableStateOf(false)
    }

    if (measureTextWidth) {
        textWidthState = with(LocalDensity.current) {
            textMeasurer.measure(
                textInputState,
                style = TextStyle(fontSize = 50.sp, fontFamily = LedFont),
            ).size.width.toDp()
        }

        measureTextWidth = false
    }


    LaunchedEffect(textInputState) {
        measureTextWidth = true
        coroutineScope.launch(Dispatchers.IO) {
            context.dataStore.edit { prefs ->
                prefs[scrollerTextKey] = textInputState
            }
        }
    }

    val systemUiController = rememberSystemUiController()
    with(systemUiController) {
        setStatusBarColor(MaterialTheme.colorScheme.primary)
        setNavigationBarColor(MaterialTheme.colorScheme.background)
        statusBarDarkContentEnabled = true
        navigationBarDarkContentEnabled = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(usedBackgroundColorState.toULong()))
                    .horizontalScroll(
                        rememberScrollState(),
                        enabled = false
                    )
            ) {

                key(textInputState, usedSpeed) {
                    MovingText(
                        text = textInputState,
                        usedTextColor = usedTextColorState,
                        screenWidth = screenWidth,
                        textWidth = textWidthState.value.toInt(),
                        speed = usedSpeed
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                value = textInputState,
                onValueChange = { newValue ->
                    textInputState = newValue

                },
                maxLines = 1,
                singleLine = true,
                shape = CircleShape,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.speed),
                    color = MaterialTheme.colorScheme.onBackground
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(4) { index ->
                        val speed = speedList[index]
                        RadioButtonItem(value = speed, isSelected = usedSpeed == speed) {
                            usedSpeed = speed
                            coroutineScope.launch(Dispatchers.IO) {
                                context.dataStore.edit { prefs ->
                                    prefs[usedSpeedKey] = speed
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.text_color),
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow {

                    item {
                        val color = Colors.entries.first().color
                        ColorItem(
                            isFirst = true, color = color,
                            isSelected = usedTextColorState == color.value.toLong()
                        ) {
                            coroutineScope.launch(Dispatchers.IO) {
                                context.dataStore.edit { prefs ->
                                    prefs[usedTextColorKey] = color.value.toLong()
                                }
                            }
                            usedTextColorState = color.value.toLong()
                        }
                    }

                    items(Colors.entries.size - 1) { index ->
                        val color = Colors.entries[index + 1].color
                        ColorItem(
                            color = color,
                            isSelected = usedTextColorState == color.value.toLong()
                        ) {
                            coroutineScope.launch(Dispatchers.IO) {
                                context.dataStore.edit { prefs ->
                                    prefs[usedTextColorKey] = color.value.toLong()
                                }
                            }
                            usedTextColorState = color.value.toLong()
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.background_color),
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow {

                    item {
                        val color = Colors.entries.first().color
                        ColorItem(
                            isFirst = true, color = color,
                            isSelected = usedBackgroundColorState == color.value.toLong()
                        ) {
                            coroutineScope.launch(Dispatchers.IO) {
                                context.dataStore.edit { prefs ->
                                    prefs[usedBackgroundColorKey] = color.value.toLong()
                                }
                            }
                            usedBackgroundColorState = color.value.toLong()
                        }
                    }

                    items(Colors.entries.size - 1) { index ->
                        val color = Colors.entries[index + 1].color
                        ColorItem(
                            color = color,
                            isSelected = usedBackgroundColorState == color.value.toLong()
                        ) {
                            coroutineScope.launch(Dispatchers.IO) {
                                context.dataStore.edit { prefs ->
                                    prefs[usedBackgroundColorKey] = color.value.toLong()
                                }
                            }
                            usedBackgroundColorState = color.value.toLong()
                        }
                    }

                }
            }

            ExtendedFloatingActionButton(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    if (textInputState.isNotEmpty() && textInputState.isNotBlank()) {
                        onStartClicked(textInputState.length.toString(), usedSpeed.toString())
                    } else {
                        Toast.makeText(
                            context,
                            context.getText(R.string.empty_text_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.start),
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            }
        }
    }
}