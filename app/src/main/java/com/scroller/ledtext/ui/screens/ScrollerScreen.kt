package com.scroller.ledtext.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.scroller.ledtext.data.Colors
import com.scroller.ledtext.data.dataStore
import com.scroller.ledtext.helpers.BACKGROUND_COLOR_DATASTORE_NAME
import com.scroller.ledtext.helpers.LedFont
import com.scroller.ledtext.helpers.SCROLLER_TEXT_DATASTORE_NAME
import com.scroller.ledtext.helpers.TEXT_COLOR_DATASTORE_NAME
import com.scroller.ledtext.helpers.utils.calculateOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Composable
fun ScrollerScreen(
    textLength: String,
    speed: String
) {

    val context = LocalContext.current
    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    val usedTextColorKey = longPreferencesKey(TEXT_COLOR_DATASTORE_NAME)
    val usedBackgroundColorKey = longPreferencesKey(BACKGROUND_COLOR_DATASTORE_NAME)
    val scrollerTextKey = stringPreferencesKey(SCROLLER_TEXT_DATASTORE_NAME)

    var usedTextColorState by remember {
        mutableLongStateOf(Colors.WHITE.color.value.toLong())
    }

    var usedBackgroundColorState by remember {
        mutableLongStateOf(Colors.BLACK.color.value.toLong())
    }

    var constraintsState by remember {
        mutableStateOf(Constraints(value = 0))
    }

    var textHeightState by remember {
        mutableStateOf(Dp(0f))
    }

    var isBackButtonVisible by remember {
        mutableStateOf(false)
    }


    val systemUiController = rememberSystemUiController()

    var scrollerText by remember {
        mutableStateOf("")
    }

    var fontSp by remember {
        mutableIntStateOf(600)
    }

    val textMeasurer = rememberTextMeasurer()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(Unit) {

        with(systemUiController) {
            isSystemBarsVisible = false
            setStatusBarColor(Color(usedBackgroundColorState.toULong()))
            setNavigationBarColor(Color(usedBackgroundColorState.toULong()))
        }

        withContext(Dispatchers.IO) {
            context.dataStore.data.collectLatest { prefs ->
                usedTextColorState = prefs[usedTextColorKey] ?: Colors.WHITE.color.value.toLong()
                usedBackgroundColorState =
                    prefs[usedBackgroundColorKey] ?: Colors.BLACK.color.value.toLong()
                scrollerText = prefs[scrollerTextKey] ?: "Example text"
            }
        }
    }


    var measureTextHeight by remember {
        mutableStateOf(false)
    }

    if (measureTextHeight) {

        textHeightState = with(LocalDensity.current) {
            textMeasurer.measure(
                constraints = constraintsState,
                text = scrollerText,
                style = TextStyle(fontSize = fontSp.sp, fontFamily = LedFont),
                maxLines = 1
            ).size.height.toDp()
        }

        while (textHeightState > screenHeight) {
            fontSp -= 2
            textHeightState = with(LocalDensity.current) {
                textMeasurer.measure(
                    constraints = constraintsState,
                    text = scrollerText,
                    style = TextStyle(fontSize = fontSp.sp, fontFamily = LedFont),
                    maxLines = 1
                ).size.height.toDp()
            }
        }

        measureTextHeight = false
    }


    LaunchedEffect(constraintsState) {
        measureTextHeight = true
    }


    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (((textLength.toInt() * 500) + 5000) * 1 / speed.toFloat()).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    BoxWithConstraints(
        modifier = Modifier
            .clickable {
                isBackButtonVisible = !isBackButtonVisible
                systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(usedBackgroundColorState.toULong()))
                .horizontalScroll(
                    rememberScrollState(),
                    enabled = false
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            constraintsState = this@BoxWithConstraints.constraints

            Text(
                text = scrollerText,
                color = Color(usedTextColorState.toULong()),
                maxLines = 1,
                fontSize = fontSp.sp,
                modifier = Modifier
                    .offset(
                        x = calculateOffset(
                            screenWidth = screenWidth,
                            textWidth = with(LocalDensity.current) {
                                textMeasurer.measure(
                                    scrollerText,
                                    style = TextStyle(
                                        fontSize = fontSp.sp,
                                        fontFamily = LedFont
                                    ),
                                    maxLines = 1
                                ).size.width.toDp()
                            },
                            scrollOffset = scrollOffset
                        )
                    ),
                fontFamily = LedFont
            )
        }
    }
}