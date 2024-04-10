package com.scroller.ledtext.ui.screens.edit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scroller.ledtext.helpers.LedFont
import com.scroller.ledtext.helpers.utils.calculateOffset

@Composable
fun MovingText(text: String, usedTextColor: Long, screenWidth: Int, textWidth: Int, speed: Float) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (((text.length * 300) + 4000) * 1 / speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Text(
        text = text,
        color = Color(usedTextColor.toULong()),
        maxLines = 1,
        fontSize = 50.sp,
        modifier = Modifier
            .padding(vertical = 24.dp)
            .offset(
                x = calculateOffset(
                    screenWidth = screenWidth.dp,
                    textWidth = textWidth.dp,
                    scrollOffset = scrollOffset
                )
            ),
        fontFamily = LedFont
    )


}