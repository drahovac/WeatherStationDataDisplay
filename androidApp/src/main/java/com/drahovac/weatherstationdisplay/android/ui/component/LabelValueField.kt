package com.drahovac.weatherstationdisplay.android.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LabelValueField(
    label: String,
    value: String
) {
    LabelField(label)
    Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
fun LabelValueFieldWithUnits(
    label: String,
    value: String,
    units: String,
) {
    LabelField(label)
    Row {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .alignByBaseline(),
            text = units,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LabelField(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Preview
@Composable
fun LabelValueFieldPreview() {
    MaterialTheme {
        Column {
            LabelValueField(label = "Label", value = "Value")
            LabelValueFieldWithUnits(label = "Label", value = "Value", units = "Unit")
        }
    }
}
