package com.pubnub.components.chat.ui.component.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode

class FlowRowTheme(
    modifier: Modifier = Modifier,
    mainAxisSize: SizeMode = SizeMode.Wrap,
    mainAxisAlignment: FlowMainAxisAlignment = FlowMainAxisAlignment.Start,
    mainAxisSpacing: Dp = 0.dp,
    crossAxisAlignment: FlowCrossAxisAlignment = FlowCrossAxisAlignment.Start,
    crossAxisSpacing: Dp = 0.dp,
    lastLineMainAxisAlignment: FlowMainAxisAlignment = mainAxisAlignment,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var mainAxisSize by mutableStateOf(mainAxisSize, structuralEqualityPolicy())
        internal set

    var mainAxisAlignment by mutableStateOf(mainAxisAlignment, structuralEqualityPolicy())
        internal set

    var mainAxisSpacing by mutableStateOf(mainAxisSpacing, structuralEqualityPolicy())
        internal set

    var crossAxisAlignment by mutableStateOf(crossAxisAlignment, structuralEqualityPolicy())
        internal set

    var crossAxisSpacing by mutableStateOf(crossAxisSpacing, structuralEqualityPolicy())
        internal set

    var lastLineMainAxisAlignment by mutableStateOf(
        lastLineMainAxisAlignment,
        structuralEqualityPolicy()
    )
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FlowRowTheme

        if (modifier != other.modifier) return false
        if (mainAxisSize != other.mainAxisSize) return false
        if (mainAxisAlignment != other.mainAxisAlignment) return false
        if (mainAxisSpacing != other.mainAxisSpacing) return false
        if (crossAxisAlignment != other.crossAxisAlignment) return false
        if (crossAxisSpacing != other.crossAxisSpacing) return false
        if (lastLineMainAxisAlignment != other.lastLineMainAxisAlignment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modifier.hashCode()
        result = 31 * result + mainAxisSize.hashCode()
        result = 31 * result + mainAxisAlignment.hashCode()
        result = 31 * result + mainAxisSpacing.hashCode()
        result = 31 * result + crossAxisAlignment.hashCode()
        result = 31 * result + crossAxisSpacing.hashCode()
        result = 31 * result + lastLineMainAxisAlignment.hashCode()
        return result
    }
}
