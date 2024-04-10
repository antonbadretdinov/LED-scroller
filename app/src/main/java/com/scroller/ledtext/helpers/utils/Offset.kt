package com.scroller.ledtext.helpers.utils

import androidx.compose.ui.unit.Dp

fun calculateOffset(screenWidth: Dp, textWidth: Dp, scrollOffset: Float): Dp {
    val totalOffset = textWidth + screenWidth
    val offset = (totalOffset * scrollOffset)
    return if (offset > screenWidth) {
        val additionalOffset = offset - screenWidth
        (-additionalOffset)
    } else {
        screenWidth.minus(offset)
    }
}