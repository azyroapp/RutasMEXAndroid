package com.azyroapp.rutasmex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.core.config.DeveloperConfigService

/**
 * 🛠️ Pantalla de configuración de desarrollador
 * Permite habilitar/deshabilitar flags de debug y visualización
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperSettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val devConfig = remember { DeveloperConfigService.getInstance(context) }
    
    // Observar todos los flags
    val showDebugPanel by devConfig.showDebugPanel.collectAsState()
    val showDetailedLogs by devConfig.showDetailedLogs.collectAsState()
    val enableTestMode by devConfig.enableTestMode.collectAsState()
    val showDeveloperSection by devConfig.showDeveloperSection.collectAsState()
    val showRouteSegments by devConfig.showRouteSegments.collectAsState()
    val showRoutePoints by devConfig.showRoutePoints.collectAsState()
    val showSegmentIda by devConfig.showSegmentIda.collectAsState()
    val showSegmentRegreso by devConfig.showSegmentRegreso.collectAsState()
    val showSegmentCompleto by devConfig.showSegmentCompleto.collectAsState()
    val showPointsIda by devConfig.showPointsIda.collectAsState()
    val showPointsRegreso by devConfig.showPointsRegreso.collectAsState()
    val showPointsCompleto by devConfig.showPointsCompleto.collectAsState()
    val showAnnotationTitles by devConfig.showAnnotationTitles.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛠️ Configuración de Desarrollador") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón para resetear todos los flags
                    IconButton(
                        onClick = {
                            devConfig.resetAllFlags()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Resetear todo"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sección: Debug General
            SectionHeader(
                icon = Icons.Default.BugReport,
                title = "Debug General"
            )
            
            SwitchItem(
                title = "Panel de Debug",
                description = "Mostrar panel de información de debug",
                checked = showDebugPanel,
                onCheckedChange = { devConfig.setShowDebugPanel(it) }
            )
            
            SwitchItem(
                title = "Logs Detallados",
                description = "Habilitar logs detallados en Logcat",
                checked = showDetailedLogs,
                onCheckedChange = { devConfig.setShowDetailedLogs(it) }
            )
            
            SwitchItem(
                title = "Modo de Prueba",
                description = "Habilitar funcionalidades de testing",
                checked = enableTestMode,
                onCheckedChange = { devConfig.setEnableTestMode(it) }
            )
            
            SwitchItem(
                title = "Sección de Desarrollador",
                description = "Mostrar sección de desarrollador en settings",
                checked = showDeveloperSection,
                onCheckedChange = { devConfig.setShowDeveloperSection(it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Sección: Visualización de Rutas
            SectionHeader(
                icon = Icons.Default.Map,
                title = "Visualización de Rutas"
            )
            
            SwitchItem(
                title = "Mostrar Segmentos",
                description = "Visualizar segmentos de ruta en el mapa",
                checked = showRouteSegments,
                onCheckedChange = { devConfig.setShowRouteSegments(it) }
            )
            
            SwitchItem(
                title = "Mostrar Puntos",
                description = "Visualizar puntos de ruta en el mapa",
                checked = showRoutePoints,
                onCheckedChange = { devConfig.setShowRoutePoints(it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Sección: Segmentos por Modo
            SectionHeader(
                icon = Icons.Default.Code,
                title = "Segmentos por Modo"
            )
            
            SwitchItem(
                title = "Segmento IDA",
                description = "Mostrar segmento de ida en el mapa",
                checked = showSegmentIda,
                onCheckedChange = { devConfig.setShowSegmentIda(it) }
            )
            
            SwitchItem(
                title = "Segmento REGRESO",
                description = "Mostrar segmento de regreso en el mapa",
                checked = showSegmentRegreso,
                onCheckedChange = { devConfig.setShowSegmentRegreso(it) }
            )
            
            SwitchItem(
                title = "Segmento COMPLETO",
                description = "Mostrar segmento completo en el mapa",
                checked = showSegmentCompleto,
                onCheckedChange = { devConfig.setShowSegmentCompleto(it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Sección: Puntos por Modo
            SectionHeader(
                icon = Icons.Default.Visibility,
                title = "Puntos por Modo"
            )
            
            SwitchItem(
                title = "Puntos IDA",
                description = "Mostrar puntos de ida en el mapa",
                checked = showPointsIda,
                onCheckedChange = { devConfig.setShowPointsIda(it) }
            )
            
            SwitchItem(
                title = "Puntos REGRESO",
                description = "Mostrar puntos de regreso en el mapa",
                checked = showPointsRegreso,
                onCheckedChange = { devConfig.setShowPointsRegreso(it) }
            )
            
            SwitchItem(
                title = "Puntos COMPLETO",
                description = "Mostrar puntos completos en el mapa",
                checked = showPointsCompleto,
                onCheckedChange = { devConfig.setShowPointsCompleto(it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Sección: Anotaciones
            SectionHeader(
                icon = Icons.Default.Visibility,
                title = "Anotaciones"
            )
            
            SwitchItem(
                title = "Títulos de Anotaciones",
                description = "Mostrar títulos en marcadores del mapa",
                checked = showAnnotationTitles,
                onCheckedChange = { devConfig.setShowAnnotationTitles(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ Información",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estas configuraciones son solo para desarrollo y debugging. " +
                                "Los cambios se guardan automáticamente y persisten entre sesiones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Header de sección con icono
 */
@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Item de switch con título y descripción
 */
@Composable
private fun SwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
