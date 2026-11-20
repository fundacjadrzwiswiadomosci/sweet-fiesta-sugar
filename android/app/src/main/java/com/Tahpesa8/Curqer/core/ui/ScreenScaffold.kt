package com.Tahpesa8.Curqer.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Tahpesa8.Curqer.R
import com.Tahpesa8.Curqer.core.theme.SurfaceTint
import com.Tahpesa8.Curqer.core.theme.TextPrimary

@Composable
fun ScreenScaffold(
    titleRes: Int,
    overlay: List<Color>,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
    headerExtra: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    AppBackground(overlay = overlay, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceTint, CircleShape)
                        .border(2.dp, Color(0x66FFFFFF), CircleShape)
                        .clickable(onClick = onMenu),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_home),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                if (headerExtra != null) headerExtra()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
            Spacer(modifier = Modifier.height(12.dp))
            MenuButton(
                onClick = onMenu,
                text = stringResource(R.string.menu),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
