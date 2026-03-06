package com.azyroapp.rutasmex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Pantalla de Ayuda
 * FAQ y guías de uso
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Preguntas Frecuentes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // FAQ Items
            items(faqItems) { faq ->
                FAQCard(faq = faq)
            }
            
            // Contacto
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "¿Necesitas más ayuda?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Contáctanos en soporte@azyroapp.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card de FAQ expandible
 */
@Composable
private fun FAQCard(
    faq: FAQItem,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = faq.emoji,
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Text(
                        text = faq.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir"
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3f
                )
            }
        }
    }
}

/**
 * Modelo de FAQ
 */
private data class FAQItem(
    val emoji: String,
    val question: String,
    val answer: String
)

/**
 * Lista de FAQs
 */
private val faqItems = listOf(
    FAQItem(
        emoji = "🗺️",
        question = "¿Cómo busco una ruta?",
        answer = "Toca el botón 'Buscar Ruta' en la pantalla principal. Selecciona tu origen y destino, " +
                "luego elige las rutas que deseas visualizar en el mapa. Puedes seleccionar múltiples rutas " +
                "para compararlas."
    ),
    FAQItem(
        emoji = "📍",
        question = "¿Qué son las alertas de proximidad?",
        answer = "Las alertas de proximidad te notifican cuando te acercas a tu destino. Puedes configurar " +
                "tres niveles de alerta (Lejos, Medio, Cerca) con diferentes distancias y sonidos. " +
                "Esto te ayuda a no perderte tu parada."
    ),
    FAQItem(
        emoji = "⭐",
        question = "¿Cómo guardo lugares favoritos?",
        answer = "Toca el botón 'Lugares' en la pantalla principal. Puedes agregar lugares frecuentes " +
                "como tu casa, trabajo o escuela. Esto hace más rápido planificar tus viajes habituales."
    ),
    FAQItem(
        emoji = "🚌",
        question = "¿Cómo inicio un viaje?",
        answer = "Después de buscar rutas, selecciona una o más rutas y toca el botón de Play. " +
                "Si seleccionaste múltiples rutas, elige cuál tomarás. La app comenzará a rastrear " +
                "tu ubicación y te alertará cuando te acerques a tu destino."
    ),
    FAQItem(
        emoji = "🔔",
        question = "¿Por qué no recibo notificaciones?",
        answer = "Asegúrate de haber otorgado permisos de notificaciones a la app. También verifica " +
                "que las alertas de proximidad estén activadas en la configuración del viaje. " +
                "Revisa que tu dispositivo no esté en modo No Molestar."
    ),
    FAQItem(
        emoji = "📊",
        question = "¿Dónde veo mi historial?",
        answer = "Toca el botón 'Historial' en la pantalla principal. Ahí encontrarás todos tus viajes " +
                "anteriores con detalles como fecha, hora, ruta utilizada y duración."
    ),
    FAQItem(
        emoji = "🗺️",
        question = "¿Puedo usar la app sin internet?",
        answer = "Necesitas conexión a internet para buscar rutas y descargar mapas. Sin embargo, " +
                "una vez iniciado un viaje, la app puede funcionar con GPS sin conexión. " +
                "Los documentos legales se cachean para acceso offline."
    ),
    FAQItem(
        emoji = "🔋",
        question = "¿La app consume mucha batería?",
        answer = "La app usa GPS durante los viajes activos, lo cual consume batería. Para optimizar, " +
                "cierra el viaje cuando llegues a tu destino. También puedes ajustar la precisión " +
                "de ubicación en la configuración de tu dispositivo."
    )
)
