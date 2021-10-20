package com.pubnub.components.chat.ui.component.input.renderer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.input.LocalTypingIndicatorTheme
import com.pubnub.framework.data.Typing
import kotlinx.coroutines.delay

/**
 * Typing Indicator Custom Renderer with animated icon
 */
object AnimatedTypingIndicatorRenderer : TypingIndicatorRenderer {

    private const val initialAlpha = 0.5f
    private const val targetAlpha = 0.86f

    private const val duration = 200
    private const val delay = duration * 2

    private val easing = FastOutSlowInEasing

    @Composable
    override fun TypingIndicator(data: List<Typing>) {
        val context = LocalContext.current
        val theme = LocalTypingIndicatorTheme.current

        val lastData = data.filter { it.isTyping }.maxByOrNull { it.timestamp }

        Row(
            modifier = theme.modifier.semantics {
                contentDescription = context.getString(R.string.typing_indicator)
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (lastData?.isTyping != null) {
                // Draw icon
                TypingAnimation(
                    modifier = Modifier.width(48.dp),
                    color = theme.icon.tint,
                )

                // Draw text
                Text(
                    text = context.getString(R.string.is_typing_animated, lastData.userId),
                    fontWeight = theme.text.fontWeight,
                    fontSize = theme.text.fontSize,
                    color = theme.text.color,
                    overflow = theme.text.overflow,
                    maxLines = theme.text.maxLines,
                    modifier = theme.text.modifier,
                )
            }
        }
    }

    @Composable
    fun TypingAnimation(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colors.primary,
        size: Dp = 5.dp,
    ) {
        // Animation Settings
        val alpha1 = remember { Animatable(initialAlpha) }
        val alpha2 = remember { Animatable(initialAlpha) }
        val alpha3 = remember { Animatable(initialAlpha) }

        // Set animation depends on start delay
        alpha1.setAnimation(startDelay = 0)
        alpha2.setAnimation(startDelay = duration.toLong())
        alpha3.setAnimation(startDelay = 2 * duration.toLong())

        // Draw animated dots
        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
            AnimatedDot(color = color.copy(alpha = alpha1.value), size = size)
            AnimatedDot(color = color.copy(alpha = alpha2.value), size = size)
            AnimatedDot(color = color.copy(alpha = alpha3.value), size = size)
        }
    }

    @Composable
    private fun AnimatedDot(color: Color, size: Dp) {
        Box(
            Modifier
                .background(color, CircleShape)
                .size(size)
        )
    }

    @Composable
    private fun <V : AnimationVector> Animatable<Float, V>.setAnimation(startDelay: Long) {
        LaunchedEffect(this) {
            delay(startDelay)
            this@setAnimation.animateTo(
                targetValue = targetAlpha,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = duration,
                        delayMillis = delay,
                        easing = easing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
            )
        }

    }
}


@Preview
@Composable
private fun TypingAnimationPreview() {
    AnimatedTypingIndicatorRenderer.TypingAnimation(Modifier.width(25.dp))
}