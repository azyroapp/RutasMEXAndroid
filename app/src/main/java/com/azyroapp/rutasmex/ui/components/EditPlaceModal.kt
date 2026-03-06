package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.PlaceCategory
import com.azyroapp.rutasmex.data.model.SavedPlace

/**
 * Modal para agregar o editar un lugar guardado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaceModal(
    place: SavedPlace? = null, // null = agregar nuevo, no-null = editar existente
    onSave: (name: String, latitude: Double, longitude: Double, category: PlaceCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(place?.name ?: "") }
    var latitude by remember { mutableStateOf(place?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(place?.longitude?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(place?.category ?: PlaceCategory.OTHER) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    
    val isEditing = place != null
    val isValid = name.isNotBlank() && 
                  latitude.toDoubleOrNull() != null && 
                  longitude.toDoubleOrNull() != null
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditing) "Editar Lugar" else "Agregar Lugar")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del lugar") },
                    placeholder = { Text("Ej: Mi casa, Trabajo, Escuela") },
                    leadingIcon = {
                        Icon(Icons.Default.Place, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Categoría
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = getCategoryDisplayName(selectedCategory),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu)
                        },
                        leadingIcon = {
                            Icon(getCategoryIcon(selectedCategory), contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        PlaceCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(getCategoryDisplayName(category)) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryMenu = false
                                },
                                leadingIcon = {
                                    Icon(getCategoryIcon(category), contentDescription = null)
                                }
                            )
                        }
                    }
                }
                
                // Latitud
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitud") },
                    placeholder = { Text("16.7504034") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = latitude.isNotBlank() && latitude.toDoubleOrNull() == null
                )
                
                // Longitud
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitud") },
                    placeholder = { Text("-93.12392021") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = longitude.isNotBlank() && longitude.toDoubleOrNull() == null
                )
                
                if (!isValid && name.isNotBlank()) {
                    Text(
                        text = "Por favor verifica las coordenadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSave(
                            name,
                            latitude.toDouble(),
                            longitude.toDouble(),
                            selectedCategory
                        )
                        onDismiss()
                    }
                },
                enabled = isValid
            ) {
                Text(if (isEditing) "Guardar" else "Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Obtiene el nombre para mostrar de una categoría
 */
private fun getCategoryDisplayName(category: PlaceCategory): String {
    return when (category) {
        PlaceCategory.HOME -> "Casa"
        PlaceCategory.WORK -> "Trabajo"
        PlaceCategory.SCHOOL -> "Escuela"
        PlaceCategory.FAVORITE -> "Favorito"
        PlaceCategory.OTHER -> "Otro"
    }
}

/**
 * Obtiene el icono de una categoría
 */
private fun getCategoryIcon(category: PlaceCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        PlaceCategory.HOME -> Icons.Default.Home
        PlaceCategory.WORK -> Icons.Default.Work
        PlaceCategory.SCHOOL -> Icons.Default.School
        PlaceCategory.FAVORITE -> Icons.Default.Star
        PlaceCategory.OTHER -> Icons.Default.Place
    }
}
