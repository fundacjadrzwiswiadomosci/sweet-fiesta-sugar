package com.Tahpesa8.Curqer.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Tahpesa8.Curqer.core.theme.TextSecondary

data class StatCardSpec(
    val iconRes: Int,
    val value: String,
    val labelRes: Int,
    val valueColor: Color
)

@Composable
fun StatCard(spec: StatCardSpec, modifier: Modifier = Modifier) {
    ClayCard(
        modifier = modifier,
        accent = spec.valueColor,
        radius = 22.dp,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(spec.valueColor.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(spec.iconRes),
                    contentDescription = null,
                    tint = spec.valueColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = spec.value,
                style = MaterialTheme.typography.headlineSmall,
                color = spec.valueColor,
                maxLines = 1
            )
            Text(
                text = stringResource(spec.labelRes),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun StatRow(specs: List<StatCardSpec>, modifier: Modifier = Modifier) {
    val visible = specs.filter { it.value != "0" && it.value.isNotBlank() }
    if (visible.size < 2) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (spec in visible) {
            StatCard(spec = spec, modifier = Modifier.weight(1f))
        }
    }
}
