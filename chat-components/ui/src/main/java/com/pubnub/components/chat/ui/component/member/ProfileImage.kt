package com.pubnub.components.chat.ui.component.member

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.pubnub.components.chat.ui.component.member.renderer.DefaultMemberRenderer.OnlineIndicator
import com.pubnub.components.chat.ui.component.message.LocalProfileImageTheme
import com.pubnub.components.chat.util.CenterInside

@Composable
fun ProfileImage(
    imageUrl: String?,
    isOnline: Boolean?,
    modifier: Modifier = Modifier,
) {

    with(LocalProfileImageTheme.current) {

        Box(modifier = modifier) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(false)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = null,
                contentScale = CenterInside,
                modifier = Modifier.fillMaxSize(),
            )
            if (isOnline != null)
                with(indicatorTheme) {
                    OnlineIndicator(
                        modifier = indicatorTheme.modifier.align(indicatorTheme.align),
                        isOnline = isOnline,
                        activeColor = activeColor,
                        inactiveColor = inactiveColor,
                        borderStroke = borderStroke,
                    )
                }
        }
    }
}