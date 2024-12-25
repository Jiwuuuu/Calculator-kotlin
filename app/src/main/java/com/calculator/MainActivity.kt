package com.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculator.ui.theme.CalculatorTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    // States to manage the display and expression
    val displayValue = remember { mutableStateOf("0") }
    val expression = remember { mutableStateOf("") }
    val operator = remember { mutableStateOf<String?>(null) }
    val previousValue = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Show the full expression and the current value
        ExpressionText(value = expression.value)
        DisplayText(value = displayValue.value)

        Spacer(modifier = Modifier.height(16.dp))

        CalculatorButtons { buttonLabel ->
            onButtonClicked(buttonLabel, displayValue, expression, operator, previousValue)
        }
    }
}

@Composable
fun ExpressionText(value: String) {
    Text(
        text = value,
        fontSize = 24.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.End,
        color = MaterialTheme.colorScheme.onBackground
    )
}



@Composable
fun DisplayText(value: String) {
    Text(
        text = value,
        fontSize = 48.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.End,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun CalculatorButtons(onButtonClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ButtonRow(listOf("7", "8", "9", "/"), onButtonClick)
        ButtonRow(listOf("4", "5", "6", "*"), onButtonClick)
        ButtonRow(listOf("1", "2", "3", "-"), onButtonClick)
        ButtonRow(listOf("0", "C", "=", "+"), onButtonClick)
    }
}


@Composable
fun ButtonRow(buttons: List<String>, onButtonClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { label ->
            CalculatorButton(
                label = label,
                onClick = { onButtonClick(label) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
        }
    }
}


@Composable
fun CalculatorButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier // Layout modifiers will be passed by the parent composable
    ) {
        Text(
            text = label,
            fontSize = 24.sp
        )
    }
}


fun onButtonClicked(
    label: String,
    displayValue: MutableState<String>,
    expression: MutableState<String>,
    operator: MutableState<String?>,
    previousValue: MutableState<String?>
) {
    when (label) {
        "C" -> {
            // Reset all states
            displayValue.value = "0"
            expression.value = ""
            operator.value = null
            previousValue.value = null
        }
        "=" -> {
            val current = displayValue.value.toDoubleOrNull()
            val previous = previousValue.value?.toDoubleOrNull()

            if (current != null && previous != null && operator.value != null) {
                val result = when (operator.value) {
                    "+" -> previous + current
                    "-" -> previous - current
                    "*" -> previous * current
                    "/" -> if (current != 0.0) previous / current else "Error"
                    else -> current
                }
                displayValue.value = result.toString()
                expression.value += " = $result"
            }

            // Reset operator and previous value
            operator.value = null
            previousValue.value = null
        }
        "+", "-", "*", "/" -> {
            if (operator.value == null) {
                // Append the operator to the expression
                expression.value += " ${displayValue.value} $label"
                previousValue.value = displayValue.value
                operator.value = label
                displayValue.value = "0" // Reset for the next input
            }
        }
        else -> {
            // Append numbers to the display
            displayValue.value = if (displayValue.value == "0") {
                label
            } else {
                displayValue.value + label
            }
        }
    }
}
