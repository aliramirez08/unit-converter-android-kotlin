package com.example.unitconverter.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitconverter.model.UnitCategory
import com.example.unitconverter.viewmodel.ConverterViewModel

data class ConversionHistoryItem(
    val category: String,
    val fromUnit: String,
    val toUnit: String,
    val input: String,
    val result: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as? Activity
    val categories = remember { UnitCategory.all() }
    val history = remember { mutableStateListOf<ConversionHistoryItem>() }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                title = {
                    Text(
                        text = "Unit Converter",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close app"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        val units = UnitCategory.getByName(viewModel.category)?.units ?: emptyList()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropdownSelector(
                label = "Category",
                options = categories.map { it.name },
                selected = viewModel.category,
                onSelected = { viewModel.updateCategory(it) }
            )

            DropdownSelector(
                label = "From",
                options = units,
                selected = viewModel.inputUnit,
                onSelected = { viewModel.inputUnit = it }
            )

            DropdownSelector(
                label = "To",
                options = units,
                selected = viewModel.outputUnit,
                onSelected = { viewModel.outputUnit = it }
            )

            OutlinedTextField(
                value = viewModel.inputValue,
                onValueChange = { viewModel.inputValue = it },
                label = { Text("Enter Value") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.convert() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Convert")
                }

                Button(
                    onClick = { viewModel.swapUnits() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Swap")
                }
            }

            Button(
                onClick = {
                    viewModel.inputValue = ""
                    viewModel.result = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear")
            }

            Text(
                text = "Result: ${viewModel.result}",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    if (viewModel.inputValue.isNotBlank() && viewModel.result.isNotBlank()) {
                        history.add(
                            0,
                            ConversionHistoryItem(
                                category = viewModel.category,
                                fromUnit = viewModel.inputUnit,
                                toUnit = viewModel.outputUnit,
                                input = viewModel.inputValue,
                                result = viewModel.result
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save to History")
            }

            if (history.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${item.input} ${item.fromUnit} → ${item.result} ${item.toUnit}",
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.category,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            onSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    disabledTextColor = Color.Black,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White,
    focusedBorderColor = Color(0xFF66D1A8),
    unfocusedBorderColor = Color.Gray,
    disabledBorderColor = Color.Gray,
    cursorColor = Color.Black
)