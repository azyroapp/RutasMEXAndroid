package com.azyroapp.rutasmex.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.core.services.LegalDocumentCacheService
import com.azyroapp.rutasmex.core.services.OnboardingService

/**
 * Modal de Configuración
 * Paridad 100% con iOS SettingsView
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    onDismiss: () -> Unit,
    onShowOnboarding: () -> Unit,
    onShowWebView: (LegalDocumentCacheService.LegalDocument, String) -> Unit,
    onShowAbout: () -> Unit,
    onShowHelp: () -> Unit,
    onShowContact: () -> Unit,
    onboardingService: OnboardingService,
    legalDocumentService: LegalDocumentCacheService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showClearCacheDialog by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Configuración",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }
            
            // 👤 General
            item {
                SettingsSectionHeader("General")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.PlayCircle,
                    title = "Ver Bienvenida",
                    onClick = {
                        onboardingService.resetOnboarding()
                        onDismiss()
                        onShowOnboarding()
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Book,
                    title = "Ver Tutorial",
                    onClick = {
                        onShowWebView(
                            LegalDocumentCacheService.LegalDocument.TUTORIAL,
                            "Tutorial"
                        )
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Limpiar Caché",
                    onClick = { showClearCacheDialog = true }
                )
            }
            
            // ℹ️ Información
            item {
                SettingsSectionHeader("Información")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    onClick = onShowAbout
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Ayuda",
                    onClick = onShowHelp
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Contacto",
                    onClick = onShowContact
                )
            }
            
            // 📜 Legal
            item {
                SettingsSectionHeader("Legal")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Política de Privacidad",
                    onClick = {
                        onShowWebView(
                            LegalDocumentCacheService.LegalDocument.PRIVACY_POLICY,
                            "Política de Privacidad"
                        )
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Términos de Servicio",
                    onClick = {
                        onShowWebView(
                            LegalDocumentCacheService.LegalDocument.TERMS_OF_SERVICE,
                            "Términos de Servicio"
                        )
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Gavel,
                    title = "EULA",
                    onClick = {
                        onShowWebView(
                            LegalDocumentCacheService.LegalDocument.EULA,
                            "EULA"
                        )
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Política de Reembolso",
                    onClick = {
                        onShowWebView(
                            LegalDocumentCacheService.LegalDocument.REFUND_POLICY,
                            "Política de Reembolso"
                        )
                    }
                )
            }
            
            // 📱 Información de la App
            item {
                SettingsSectionHeader("Información de la App")
            }
            
            item {
                SettingsInfoRow(
                    label = "Versión",
                    value = "1.0" // BuildConfig.VERSION_NAME
                )
            }
            
            item {
                SettingsInfoRow(
                    label = "Build",
                    value = "1" // BuildConfig.VERSION_CODE.toString()
                )
            }
        }
    }
    
    // Diálogo de confirmación para limpiar caché
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            title = {
                Text("Limpiar Caché")
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar todos los documentos en caché? Se volverán a descargar cuando los necesites.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        legalDocumentService.clearCache()
                        showClearCacheDialog = false
                    }
                ) {
                    Text("Limpiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCacheDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Header de sección en Settings
 */
@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

/**
 * Item clickeable en Settings
 */
@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Fila de información (no clickeable)
 */
@Composable
private fun SettingsInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
