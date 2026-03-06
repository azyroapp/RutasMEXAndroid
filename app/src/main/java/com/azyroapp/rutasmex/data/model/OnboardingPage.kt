package com.azyroapp.rutasmex.data.model

import androidx.compose.ui.graphics.Color

/**
 * Modelo de página de onboarding
 * Paridad con iOS OnboardingPage
 */
data class OnboardingPage(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String, // Material icon name
    val color: Color
)

/**
 * Páginas del onboarding (paridad iOS)
 */
object OnboardingPages {
    val pages = listOf(
        OnboardingPage(
            id = 0,
            title = "Bienvenido a RutasMEX",
            description = "Visualiza rutas de transporte público. Estamos expandiéndonos a más ciudades",
            icon = "onboarding_welcome",
            color = Color(0xFF2196F3) // Blue
        ),
        OnboardingPage(
            id = 1,
            title = "Busca tu Ruta",
            description = "Encuentra rutas por nombre, categoría o ubicación. Selecciona múltiples rutas para visualizarlas en el mapa",
            icon = "onboarding_search",
            color = Color(0xFF4CAF50) // Green
        ),
        OnboardingPage(
            id = 2,
            title = "Planifica tu Viaje",
            description = "Selecciona origen y destino para encontrar las mejores rutas. Configura radios de búsqueda personalizados",
            icon = "onboarding_planner",
            color = Color(0xFF9C27B0) // Purple
        ),
        OnboardingPage(
            id = 3,
            title = "Guarda Favoritos",
            description = "Guarda tus búsquedas y rutas frecuentes para acceder rápidamente a ellas cuando las necesites",
            icon = "onboarding_favorites",
            color = Color(0xFFFFC107) // Yellow/Amber
        ),
        OnboardingPage(
            id = 4,
            title = "Permisos de Ubicación",
            description = "Tu ubicación nos permite detectar tu ciudad, mostrarte rutas cercanas y notificarte cuando te acerques a tu destino",
            icon = "onboarding_location",
            color = Color(0xFFFF9800) // Orange
        ),
        OnboardingPage(
            id = 5,
            title = "Alertas de Proximidad",
            description = "Recibe notificaciones cuando te acerques a tu destino. Funcionan incluso con la app cerrada",
            icon = "onboarding_notifications",
            color = Color(0xFFF44336) // Red
        ),
        OnboardingPage(
            id = 6,
            title = "¡Todo Listo!",
            description = "Comienza a explorar las rutas de transporte público en tu ciudad",
            icon = "onboarding_ready",
            color = Color(0xFF2196F3) // Blue
        )
    )
}
