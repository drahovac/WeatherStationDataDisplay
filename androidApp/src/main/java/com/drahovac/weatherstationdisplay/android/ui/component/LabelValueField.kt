package com.drahovac.weatherstationdisplay.android.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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
        }
    }
}
