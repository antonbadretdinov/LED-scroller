package com.scroller.ledtext.ui.screens.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorItem(
    isFirst: Boolean = false,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(
                if (!isFirst)
                    PaddingValues(horizontal = 4.dp)
                else
                    PaddingValues(start = 16.dp, end = 4.dp)
            )
            .size(48.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(shape = CircleShape)
                .size(40.dp)
                .background(color = color)
                .clickable {
                    onClick()
                }
        )
    }
}