package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.City

/**
 * Selector de ciudad para el TopAppBar
 * Réplica del CitySelector.swift de iOS
 */
@Composable
fun CitySelector(
    currentCity: City?,
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        // Botón del selector
        TextButton(
            onClick = { expanded = true }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentCity?.name ?: "Selecciona Ciudad",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar ciudad",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Menú desplegable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(city.name)
                            
                            if (currentCity?.id == city.id) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Ciudad seleccionada",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}
