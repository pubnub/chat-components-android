package com.pubnub.components.chat.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                fontSize = 24.sp, // todo: Create style for it
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        modifier = Modifier.height(72.dp)
    )
}

@Composable
fun Header(
    title: String,
    description: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onTitleClick: (() -> Unit)? = null
) {

    val clickable = onTitleClick?.let { Modifier.clickable(onClick = it) } ?: Modifier
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = title,
                        maxLines = 1,
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, false),
                    )

                    onTitleClick?.let {
                        Icon(
                            imageVector = Icons.Filled.ArrowRight,
                            contentDescription = "Members",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.onSurface,
                        )

                    }
                }
                if (description.isNotEmpty())
                    Text(
                        text = description,
                        maxLines = 1,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
        modifier = Modifier
            .height(72.dp)
            .then(clickable),
    )
}