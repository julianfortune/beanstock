package com.julianfortune.glacier.ui.page.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.AutocompleteSelectV2

@Composable
fun SandboxPage() {

    val fruitOptions = listOf(
        Option(1L, "Apple"),
        Option(2L, "Banana"),
        Option(3L, "Cherry"),
        Option(4L, "Durian"),
        Option(5L, "Elderberry"),
        Option(6L, "Fig"),
        Option(7L, "Grapefruit"),
        Option(8L, "Huckleberry"),
        Option(9L, "Indian Mango"),
        Option(10L, "Jujube"),
    )

    var selectedFruitOption by remember {
        mutableStateOf<Option<Long>?>(Option(2L, "Banana"))
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        AutocompleteSelectV2(
            selectedFruitOption,
            onValueChange = {
                println("selectedFruit => $it")
                selectedFruitOption = it
            },
            fruitOptions,
            onQueryChange = {
                // TODO
            },
            label = { Text("Fruit") }
        )

        AutocompleteSelectV2(
            null,
            onValueChange = {

            },
            fruitOptions,
            onQueryChange = {
                // TODO
            },
            label = { Text("Pre-filled Autocomplete") }
        )
    }
}
