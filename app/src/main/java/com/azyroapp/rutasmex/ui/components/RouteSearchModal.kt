package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.Route

/**
 * Modal para buscar y seleccionar rutas
 * Replica el comportamiento de iOS RouteSearchModal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSearchModal(
    routes: List<Route>,
    selectedRoutes: Set<Route>,
    onRouteToggle: (Route, Boolean) -> Unit,
    onClearAll: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Obtener categorías únicas (routeType)
    val routeCategories = remember(routes) {
        val categories = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        
        // Primero agregar "Urbana" si existe
        if (routes.any { it.routeType == "Urbana" }) {
            categories.add("Urbana")
            seen.add("Urbana")
        }
        
        // Luego agregar las demás en el orden que aparecen
        routes.forEach { route ->
            if (!seen.contains(route.routeType)) {
                categories.add(route.routeType)
                seen.add(route.routeType)
            }
        }
        
        categories
    }
    
    // Categoría seleccionada
    var selectedCategory by remember { 
        mutableStateOf(routeCategories.firstOrNull() ?: "") 
    }
    
    // Filtrar rutas según búsqueda y categoría
    val filteredRoutes = remember(routes, searchQuery, selectedCategory) {
        var filtered = routes
        
        // Filtrar por búsqueda
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { route ->
                route.name.contains(searchQuery, ignoreCase = true) ||
                route.id.contains(searchQuery, ignoreCase = true)
            }
        }
        
        // Filtrar por categoría
        if (selectedCategory.isNotEmpty()) {
            filtered = filtered.filter { it.routeType == selectedCategory }
        }
        
        // Ordenar alfabéticamente
        filtered.sortedBy { it.name }
    }
    
    // Contar rutas por categoría (considerando búsqueda)
    fun routeCount(category: String): Int {
        var filtered = routes.filter { it.routeType == category }
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { route ->
                route.name.contains(searchQuery, ignoreCase = true) ||
                route.id.contains(searchQuery, ignoreCase = true)
            }
        }
        return filtered.count()
    }
    
    // Total de rutas disponibles según filtros
    val totalAvailableRoutes = remember(routes, searchQuery) {
        if (searchQuery.isEmpty()) {
            routes
        } else {
            routes.filter { route ->
                route.name.contains(searchQuery, ignoreCase = true) ||
                route.id.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    // Rutas seleccionadas de las disponibles
    val selectedAvailableCount = remember(selectedRoutes, totalAvailableRoutes) {
        selectedRoutes.count { route ->
            totalAvailableRoutes.any { it.id == route.id }
        }
    }
    
    // Verificar si todas las disponibles están seleccionadas
    val allAvailableSelected = selectedAvailableCount == totalAvailableRoutes.size && totalAvailableRoutes.isNotEmpty()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Header con botón de cerrar y contador
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón X para cerrar
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Botón de seleccionar/deseleccionar todas con contador
                TextButton(
                    onClick = {
                        if (allAvailableSelected) {
                            // Deseleccionar solo las rutas filtradas disponibles
                            totalAvailableRoutes.forEach { route ->
                                if (selectedRoutes.any { it.id == route.id }) {
                                    onRouteToggle(route, false)
                                }
                            }
                        } else {
                            // Primero deseleccionar todas
                            routes.forEach { route ->
                                if (selectedRoutes.any { it.id == route.id }) {
                                    onRouteToggle(route, false)
                                }
                            }
                            // Luego seleccionar solo las filtradas disponibles
                            totalAvailableRoutes.forEach { route ->
                                if (!selectedRoutes.any { it.id == route.id }) {
                                    onRouteToggle(route, true)
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (allAvailableSelected) {
                            Icons.Default.CheckBox
                        } else {
                            Icons.Default.CheckBoxOutlineBlank
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$selectedAvailableCount/${totalAvailableRoutes.size} rutas",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Barra de búsqueda con teclado numérico
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Buscar ruta...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, // Teclado numérico por defecto
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                ),
                singleLine = true
            )
            
            // Picker de categorías (si hay más de una)
            if (routeCategories.size > 1) {
                ScrollableTabRow(
                    selectedTabIndex = routeCategories.indexOf(selectedCategory).coerceAtLeast(0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    edgePadding = 0.dp
                ) {
                    routeCategories.forEach { category ->
                        Tab(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            text = {
                                Text(
                                    text = "(${routeCount(category)}) $category",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                }
            }
            
            // Lista de rutas
            if (filteredRoutes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No se encontraron rutas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredRoutes) { route ->
                        RouteCheckboxItem(
                            route = route,
                            isSelected = selectedRoutes.any { it.id == route.id },
                            onToggle = { isSelected ->
                                onRouteToggle(route, isSelected)
                            }
                        )
                    }
                }
            }
            
            // Botón Aplicar (siempre visible)
            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = selectedRoutes.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedRoutes.isEmpty()) {
                        "Selecciona al menos una ruta"
                    } else {
                        "Aplicar ${selectedRoutes.size} ruta${if (selectedRoutes.size != 1) "s" else ""}"
                    }
                )
            }
        }
    }
}

/**
 * Item de ruta en búsqueda (estilo iOS)
 */
@Composable
private fun RouteCheckboxItem(
    route: Route,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onToggle(!isSelected) },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = onToggle
            )
            
            // Nombre de ruta
            Text(
                text = route.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            
            // Indicador de selección (checkmark)
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
