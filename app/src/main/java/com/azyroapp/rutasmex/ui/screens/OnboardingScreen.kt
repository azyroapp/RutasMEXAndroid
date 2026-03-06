package com.azyroapp.rutasmex.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.azyroapp.rutasmex.R
import com.azyroapp.rutasmex.core.services.OnboardingService
import com.azyroapp.rutasmex.data.model.OnboardingPages
import kotlinx.coroutines.launch

/**
 * Pantalla de onboarding (primera vez)
 * Paridad 100% con iOS OnboardingView
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onboardingService: OnboardingService,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = OnboardingPages.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    // Estado de permisos
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }
    
    // Launcher para permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                   permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    
    // Launcher para permisos de notificaciones (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionGranted = granted
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar con botón Skip
            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pages.size - 1)
                            }
                        }
                    ) {
                        Text("Saltar")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
            
            // Pager con páginas
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    isLocationPage = page == 4,
                    isNotificationPage = page == 5,
                    locationPermissionGranted = locationPermissionGranted,
                    notificationPermissionGranted = notificationPermissionGranted,
                    onRequestLocationPermission = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onRequestNotificationPermission = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            notificationPermissionGranted = true
                        }
                    }
                )
            }
            
            // Indicadores de página
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                }
                            )
                    )
                }
            }
            
            // Botones de navegación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Anterior (solo si no es la primera página)
                if (pagerState.currentPage > 0) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Anterior"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Anterior")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                
                // Botón Siguiente o Comenzar
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // Última página: completar onboarding
                            onboardingService.markOnboardingAsCompleted()
                            onComplete()
                        }
                    },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < pages.size - 1) {
                            "Siguiente"
                        } else {
                            "Comenzar"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (pagerState.currentPage < pages.size - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}


/**
 * Contenido de una página individual del onboarding
 */
@Composable
private fun OnboardingPageContent(
    page: com.azyroapp.rutasmex.data.model.OnboardingPage,
    isLocationPage: Boolean,
    isNotificationPage: Boolean,
    locationPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen SVG grande
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(getRawResourceForIcon(page.icon))
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = page.title,
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Título
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Descripción
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botones de permisos (solo en páginas específicas)
        when {
            isLocationPage -> {
                PermissionButton(
                    text = if (locationPermissionGranted) {
                        "✓ Permisos de Ubicación Otorgados"
                    } else {
                        "Otorgar Permisos de Ubicación"
                    },
                    granted = locationPermissionGranted,
                    onClick = onRequestLocationPermission
                )
            }
            isNotificationPage -> {
                PermissionButton(
                    text = if (notificationPermissionGranted) {
                        "✓ Permisos de Notificaciones Otorgados"
                    } else {
                        "Otorgar Permisos de Notificaciones"
                    },
                    granted = notificationPermissionGranted,
                    onClick = onRequestNotificationPermission
                )
            }
        }
    }
}

/**
 * Botón para solicitar permisos
 */
@Composable
private fun PermissionButton(
    text: String,
    granted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !granted,
        colors = if (granted) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.buttonColors()
        }
    ) {
        if (granted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * Mapea nombres de iconos a recursos raw
 */
private fun getRawResourceForIcon(iconName: String): Int {
    return when (iconName) {
        "onboarding_welcome" -> R.raw.onboarding_welcome
        "onboarding_search" -> R.raw.onboarding_search
        "onboarding_planner" -> R.raw.onboarding_planner
        "onboarding_favorites" -> R.raw.onboarding_favorites
        "onboarding_location" -> R.raw.onboarding_location
        "onboarding_notifications" -> R.raw.onboarding_notifications
        "onboarding_ready" -> R.raw.onboarding_ready
        else -> R.raw.onboarding_welcome
    }
}
