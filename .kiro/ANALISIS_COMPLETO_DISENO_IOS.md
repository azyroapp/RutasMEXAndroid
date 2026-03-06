# 📐 ANÁLISIS COMPLETO DEL DISEÑO iOS - RutasMEX

## 📅 Fecha: 5 de Marzo, 2026

---

## 🎯 Objetivo

Documentar la estructura completa del diseño iOS de RutasMEX para replicarla exactamente en Android, asegurando 100% de paridad visual y funcional.

---

## 📁 Estructura del Proyecto iOS

```
RutasMEX/
├── Assets.xcassets/          # Recursos visuales
│   ├── AppIcon.appiconset/
│   ├── Logos (Light, Tinted, Transparent)
│   ├── Ciudades (tuxtla, sancristobal, comitan, tapachula)
│   ├── Modos (ida, regreso, completo)
│   └── Onboarding (7 imágenes)
│
├── Core/                      # Sistema central
│   ├── Configuration/
│   │   └── AppConfiguration.swift
│   ├── DesignSystem/
│   │   └── DesignSystem.swift ⭐ CLAVE
│   ├── Extensions/
│   │   ├── MKPolyline+ClosestPoint.swift
│   │   └── View+ModalPersistentPadding.swift
│   ├── Factories/
│   │   └── ViewFactory.swift
│   └── Services/
│       ├── Data/
│       ├── LiveActivity/
│       ├── Location/
│       ├── Logging/
│       ├── Navigation/
│       ├── Notifications/
│       ├── Onboarding/
│       ├── Purchase/
│       ├── Route/
│       ├── Storage/
│       ├── Store/
│       ├── Toast/
│       ├── Trip/
│       └── TripHistory/
│
├── Features/                  # Módulos por funcionalidad
│   ├── ARMap/
│   │   ├── Components/
│   │   ├── ViewModels/
│   │   └── Views/
│   ├── Home/ ⭐ PRINCIPAL
│   │   ├── Components/
│   │   │   ├── LocationPermissionModal.swift
│   │   │   ├── MapAnnotationPin.swift
│   │   │   ├── ShareTripButton.swift
│   │   │   └── TripHistoryRow.swift
│   │   ├── ViewModels/
│   │   │   ├── HomeViewModel.swift ⭐
│   │   │   ├── HomeViewModel+Distance.swift
│   │   │   ├── HomeViewModel+DynamicIsland.swift
│   │   │   ├── HomeViewModel+Location.swift
│   │   │   ├── HomeViewModel+LockedTrip.swift
│   │   │   ├── HomeViewModel+Modals.swift
│   │   │   ├── HomeViewModel+Notifications.swift
│   │   │   ├── HomeViewModel+Persistence.swift
│   │   │   ├── HomeViewModel+TripControl.swift
│   │   │   └── HomeViewModel+Widget.swift
│   │   └── Views/
│   │       ├── HomeView.swift ⭐
│   │       ├── HomeMapView.swift
│   │       ├── PersistentBottomModal.swift ⭐
│   │       ├── TripBanner.swift ⭐
│   │       ├── ARModal.swift
│   │       ├── ArrivalModal.swift
│   │       ├── FavoritesModal.swift
│   │       ├── ProximityConfigModal.swift
│   │       ├── ProximityInfoModal.swift
│   │       ├── RadiusConfigModal.swift
│   │       ├── RouteInfoModal.swift
│   │       ├── RouteSearchModal.swift
│   │       ├── RouteSelectionForTripModal.swift
│   │       ├── SaveFavoriteModal.swift
│   │       ├── SavedPlaceEditorModal.swift
│   │       ├── SavedPlacesManagerModal.swift
│   │       ├── TripHistoryDetailModal.swift
│   │       └── TripHistoryModal.swift
│   ├── Onboarding/
│   ├── RoutePlanner/
│   ├── SavedPlaces/
│   ├── Settings/
│   └── Store/
│
├── Models/
│   ├── Domain/
│   │   ├── City.swift
│   │   ├── CityData.swift
│   │   ├── CitySubscription.swift
│   │   ├── DistanceCalculationMode.swift
│   │   ├── FavoriteSearch.swift
│   │   ├── GeoModels.swift
│   │   ├── Route.swift
│   │   └── TripHistory.swift
│   └── UI/
│       ├── LocationPoint.swift
│       ├── MapConfiguration.swift
│       └── SavedPlace.swift
│
├── Resources/
│   ├── Chiapas/
│   │   ├── comitan_mapkit.json
│   │   ├── sancristobal_mapkit.json
│   │   ├── tapachula_mapkit.json
│   │   └── tuxtla_mapkit.json
│   └── Oaxaca/
│
└── UI/
    └── Components/
        ├── Atoms/ ⭐ Componentes básicos
        │   ├── AppButton.swift
        │   ├── AppTextField.swift
        │   ├── BoundItem.swift
        │   ├── ClearableTextField.swift
        │   ├── CloseButton.swift
        │   ├── CurrentLocationButton.swift
        │   ├── InfoRow.swift
        │   ├── LocationInputButton.swift
        │   ├── LocationPermissionAlert.swift
        │   ├── LocationPermissionButton.swift
        │   ├── LocationRadiusCircle.swift
        │   ├── RadiusSlider.swift
        │   ├── RouteChip.swift ⭐
        │   ├── RoutePointAnnotation.swift
        │   ├── SearchField.swift
        │   ├── SelectionMessage.swift
        │   └── SwapButton.swift
        ├── Molecules/ ⭐ Componentes compuestos
        │   ├── AppOptionsMenu.swift
        │   ├── CitySelector.swift
        │   ├── ContentCard.swift
        │   ├── DynamicProjectionPoints.swift
        │   ├── DynamicRouteSegments.swift
        │   ├── EmptyStateView.swift ⭐
        │   ├── HorizontalDebugPanel.swift
        │   ├── LocationInputRow.swift ⭐
        │   ├── LocationSelectionMenu.swift
        │   ├── MapControlsBar.swift ⭐
        │   ├── ProximityCircles.swift
        │   ├── ProximityDebugPanel.swift
        │   ├── RouteAnnotations.swift
        │   ├── RouteCheckboxItem.swift
        │   ├── RouteHeader.swift
        │   └── RoutePolylines.swift
        └── Organisms/ ⭐ Componentes complejos
            ├── HomeOverlays.swift
            ├── HomeToolbar.swift
            ├── LocationPinsOverlay.swift
            ├── RouteMapOverlay.swift
            └── RouteMapView.swift
```

---

## 🎨 Design System (DesignSystem.swift)

### 🎨 Colores

```swift
struct Colors {
    // Colores de rutas (15 colores)
    static let routeColors: [Color] = [
        .red, .blue, .green, .orange, .purple,
        .pink, .yellow, .cyan, .mint, .indigo,
        .teal, .brown, .gray, .primary, .secondary
    ]
    
    // Colores principales
    static let accent = Color.accentColor
    static let primary = Color.primary
    static let secondary = Color.secondary
    static let background = Color(.systemBackground)
    static let secondaryBackground = Color(.systemGray6)
    static let error = Color.red
    
    // Colores de mapa
    static let originPin = Color.blue
    static let destinationPin = Color.red
    static let searchRadius = Color.green
    static let mapAnnotationBorder = Color.white
    
    // Colores de proximidad (adaptativo light/dark)
    static let proximityFar = Color(light: .blue, dark: .cyan)
    static let proximityMedium = Color(light: .blue, dark: .cyan)
    static let proximityNear = Color(light: .blue, dark: .cyan)
}
```

**Android Equivalente:**
- Usar Material 3 Color Scheme
- `routeColors` → Array de colores en `colors.xml`
- `accent` → `colorPrimary`
- `background` → `colorSurface`
- Proximidad → Colores adaptativos con `isSystemInDarkTheme()`

---

### 📏 Espaciado

```swift
struct Spacing {
    static let xs: CGFloat = 4
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let xl: CGFloat = 20
    static let xxl: CGFloat = 24
}
```

**Android Equivalente:**
```kotlin
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
}
```

---

### 🔲 Corner Radius

```swift
struct CornerRadius {
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let xl: CGFloat = 20
}
```

**Android Equivalente:**
```kotlin
object CornerRadius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
}
```

---

### 🔘 Tamaños de Botones

```swift
struct ButtonSizes {
    static let small: CGFloat = 40
    static let medium: CGFloat = 48
    static let large: CGFloat = 56
}
```

**Android Equivalente:**
```kotlin
object ButtonSizes {
    val small = 40.dp
    val medium = 48.dp
    val large = 56.dp
}
```

---

### 🎯 Tamaños de Iconos

```swift
struct IconSizes {
    static let small: CGFloat = 14
    static let medium: CGFloat = 18
    static let large: CGFloat = 24
}
```

**Android Equivalente:**
```kotlin
object IconSizes {
    val small = 14.dp
    val medium = 18.dp
    val large = 24.dp
}
```

---

### 🗺️ Estilos de Mapa

```swift
struct MapStyles {
    static let routeLineWidth: CGFloat = 2
    static let radiusCircleStroke: CGFloat = 2
    static let proximityStroke: CGFloat = 1.5
    static let searchRadiusStroke: CGFloat = 3
    static let annotationBorderWidth: CGFloat = 2
    
    static let primaryPointSize: CGFloat = 12
    static let secondaryPointSize: CGFloat = 8
    
    static let secondaryOpacity: Double = 0.6
    static let radiusCircleOpacity: Double = 0.2
    static let proximityOpacity: Double = 0.7
    static let searchRadiusOpacity: Double = 0.15
}
```

---

### 🌑 Sombras

```swift
struct Shadow {
    static let light = (
        color: Color.black.opacity(0.05),
        radius: CGFloat(2),
        x: CGFloat(0),
        y: CGFloat(1)
    )
    static let medium = (
        color: Color.black.opacity(0.1),
        radius: CGFloat(4),
        x: CGFloat(0),
        y: CGFloat(2)
    )
}
```

**Android Equivalente:**
```kotlin
object Shadow {
    val light = ShadowConfig(
        color = Color.Black.copy(alpha = 0.05f),
        elevation = 2.dp
    )
    val medium = ShadowConfig(
        color = Color.Black.copy(alpha = 0.1f),
        elevation = 4.dp
    )
}
```

---

## 🏗️ Arquitectura de Pantallas

### 📱 HomeView (Pantalla Principal)

**Estructura:**
```
ZStack {
    // 1. Mapa (fondo)
    HomeMapView()
    
    // 2. Overlays y controles
    HomeOverlays()
}
.sheet(isPresented: .constant(true)) {
    // 3. PersistentBottomModal (siempre visible)
    PersistentBottomModal()
        .presentationDetents([.height(78), .medium])
        .presentationBackgroundInteraction(.enabled)
        .interactiveDismissDisabled()
}
.toolbar {
    HomeToolbar()
}
```

**Componentes clave:**
1. **HomeMapView**: Mapa con rutas, pins, círculos de radio
2. **HomeOverlays**: MapControlsBar, TripBanner, botones flotantes
3. **PersistentBottomModal**: Modal inferior siempre visible
4. **HomeToolbar**: Barra superior con botones de acción

---

### 📐 PersistentBottomModal

**Estructura:**
```
NavigationStack {
    VStack {
        ScrollView {
            // Contenido dinámico:
            if sin origen ni destino {
                EmptyStateView("Selecciona origen o destino")
            } else if sin rutas {
                EmptyStateView("No hay rutas disponibles")
            } else {
                // Rutas con texto completo (adaptativo 1-3 columnas)
                LazyVGrid(columns: adaptive(150-300)) {
                    RouteChip(route, isSelected)
                }
                
                // Rutas numéricas (4 columnas fijas)
                LazyVGrid(columns: 4 flexible) {
                    RouteChip(route, isSelected)
                }
            }
        }
    }
    .toolbar {
        ToolbarItem(placement: .principal) {
            LocationInputRow()
        }
    }
}
```

**Características:**
- ✅ Siempre visible (`.interactiveDismissDisabled()`)
- ✅ Dos detents: `.height(78)` (colapsado) y `.medium` (expandido)
- ✅ Interacción con fondo habilitada (`.presentationBackgroundInteraction(.enabled)`)
- ✅ LocationInputRow en toolbar (principal)
- ✅ Grid adaptativo para rutas de texto
- ✅ Grid de 4 columnas para rutas numéricas

---

### 🎛️ LocationInputRow

**Estructura:**
```
HStack(spacing: 4) {
    // Botón Origen (esquinas redondeadas izquierda)
    LocationButton(
        text: origen?.name ?? "",
        placeholder: "ORIGEN",
        position: .leading
    )
    .disabled(sin ciudad O viaje activo)
    
    HStack(spacing: 4) {
        // Botón Swap (circular)
        CircularIconButton(
            icon: "arrow.left.arrow.right",
            isEnabled: tiene ubicaciones Y ciudad Y NO viaje
        )
        
        // Botón Favorito (circular)
        CircularIconButton(
            icon: isFavorite ? "star.fill" : "star",
            isEnabled: tiene ambas ubicaciones Y ciudad
        )
    }
    
    // Botón Destino (esquinas redondeadas derecha)
    LocationButton(
        text: destino?.name ?? "",
        placeholder: "DESTINO",
        position: .trailing
    )
    .disabled(sin ciudad O viaje activo)
}
```

**Características:**
- ✅ 4 botones: Origen, Swap, Favorito, Destino
- ✅ Efecto glass (`.ultraThinMaterial` o `.glassEffect`)
- ✅ Esquinas redondeadas asimétricas (origen/destino)
- ✅ Botones circulares centrales (swap/favorito)
- ✅ Animaciones de presión y rotación
- ✅ Deshabilitado durante viaje activo
- ✅ Deshabilitado sin ciudad seleccionada

---

### 🎮 MapControlsBar

**Estructura:**
```
HStack(spacing: 8) {
    // TripBanner (si hay viaje activo)
    if isTripActive {
        TripBanner()
            .frame(maxWidth: .infinity)
    } else {
        Spacer()
    }
    
    // Play / Stop
    AppButton(icon: isTripActive ? "stop.fill" : "play.fill")
    
    // Reset
    AppButton(icon: "trash")
    
    // Radio
    AppButton(icon: "ruler")
    
    // Selección en mapa (con menú)
    Menu {
        Button("Nuevo origen y destino")
        Button("Cambiar origen")
        Button("Cambiar destino")
        Button("Cancelar selección")
    } label: {
        AppButton(icon: "location.viewfinder")
    }
    
    // Búsqueda
    AppButton(icon: "magnifyingglass")
}
.padding(.horizontal, 16)
.padding(.bottom, 70)
```

**Características:**
- ✅ 6 botones con efecto glass
- ✅ TripBanner expandible durante viaje
- ✅ Menú contextual para selección de mapa
- ✅ Botones deshabilitados según estado
- ✅ Posicionado arriba del PersistentBottomModal

---

### 🎯 RouteChip

**Estructura:**
```
Button {
    Text(route.displayName)
        .font(isNumeric ? .system(size: 26) : .system(size: 13))
        .foregroundColor(isSelected ? .white : .primary)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(isSelected ? accent : gray6)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(isSelected ? accent : .clear, lineWidth: 2)
        )
}
.aspectRatio(3.5, contentMode: .fit)
```

**Características:**
- ✅ Tamaño de fuente dinámico (26 para numéricos, 13 para texto)
- ✅ Aspect ratio 3.5:1
- ✅ Fondo gris cuando no seleccionado
- ✅ Fondo accent + borde cuando seleccionado
- ✅ Texto blanco cuando seleccionado

---

### 🎪 TripBanner

**Estructura:**
```
VStack(spacing: 2) {
    if hasData {
        // Distancia
        HStack(spacing: 2) {
            Text(String(format: "%.1f", distance))
                .font(.system(size: 11, weight: .bold))
            Text("km")
                .font(.system(size: 8))
                .opacity(0.7)
        }
        
        // Tiempo
        HStack(spacing: 2) {
            Text("\(time)")
                .font(.system(size: 11, weight: .bold))
            Text("min")
                .font(.system(size: 8))
                .opacity(0.7)
        }
    } else {
        Image(systemName: "location.slash")
        Text("Tap")
    }
}
.frame(width: 48, height: 48)
.background(Color.black)
.clipShape(Circle())
.shadow(color: .black.opacity(0.3), radius: 4, y: 2)
.onTapGesture {
    toggleDetailedInfo()
}
```

**Características:**
- ✅ Círculo negro de 48x48
- ✅ Muestra distancia y tiempo
- ✅ Tap para expandir info detallada
- ✅ Placeholder cuando no hay datos

---

## 📊 Comparación iOS vs Android Actual

| Componente | iOS | Android Actual | Estado |
|---|---|---|---|
| **Design System** | ✅ Completo | ⚠️ Parcial | 🔧 Necesita expansión |
| **HomeView** | ✅ ZStack + Sheet | ✅ Box + Column | ✅ Correcto |
| **PersistentBottomModal** | ✅ Sheet persistente | ✅ Surface overlay | ✅ Correcto |
| **LocationInputRow** | ✅ 4 botones glass | ✅ 4 botones glass | ✅ Correcto |
| **MapControlsBar** | ✅ 6 botones + banner | ✅ 6 botones + banner | ✅ Correcto |
| **RouteChip** | ✅ Aspect 3.5:1 | ⚠️ Sin aspect ratio | 🔧 Ajustar |
| **TripBanner** | ✅ Círculo negro | ❌ No existe | 🔧 Crear |
| **EmptyStateView** | ✅ 3 estados | ✅ 3 estados | ✅ Correcto |
| **Modales** | ✅ 15 modales | ✅ 15 modales | ✅ Correcto |
| **Onboarding** | ✅ 7 pantallas | ❌ No existe | 🔧 Crear |
| **Settings** | ✅ Completo | ❌ No existe | 🔧 Crear |
| **AR View** | ✅ Completo | ⚠️ Placeholder | 🔧 Implementar |
| **Dynamic Island** | ✅ LiveActivity | ❌ No aplica | ⏭️ Skip |
| **StoreKit** | ✅ Compras | ❌ Gratis | ⏭️ Skip |

---

## 🎨 Elementos Visuales Faltantes en Android

### 1️⃣ **TripBanner Circular Negro** ❌
- Círculo negro de 48x48
- Muestra distancia y tiempo durante viaje
- Tap para expandir info detallada
- Posicionado en MapControlsBar

### 2️⃣ **RouteChip Aspect Ratio** ⚠️
- Aspect ratio 3.5:1 no implementado
- Tamaño de fuente dinámico (26 vs 13)
- Necesita ajuste visual

### 3️⃣ **Onboarding** ❌
- 7 pantallas con imágenes SVG
- Permisos de ubicación
- Tutorial de uso
- Bienvenida

### 4️⃣ **Settings Screen** ❌
- About
- Help
- Tutorial
- Privacy Policy
- Terms of Service
- Licenses
- Contact
- Developer Settings

### 5️⃣ **AR View Real** ⚠️
- Actualmente solo placeholder
- Necesita implementación con ARCore

### 6️⃣ **Imágenes de Ciudades** ❌
- tuxtla.svg
- sancristobal.svg
- comitan.svg
- tapachula.svg

### 7️⃣ **Imágenes de Modos** ❌
- ida.svg
- regreso.svg
- completo.svg

### 8️⃣ **Imágenes de Onboarding** ❌
- onboarding_welcome.svg
- onboarding_location.svg
- onboarding_search.svg
- onboarding_planner.svg
- onboarding_favorites.svg
- onboarding_notifications.svg
- onboarding_ready.svg

---

## 🚀 Plan de Acción para Android

### Fase 8: Design System Completo 🎨
- [ ] Crear `DesignSystem.kt` con todos los valores
- [ ] Definir colores en `colors.xml`
- [ ] Crear theme completo Material 3
- [ ] Implementar colores adaptativos light/dark

### Fase 9: TripBanner Circular ⭕
- [ ] Crear `TripBannerCircular.kt`
- [ ] Círculo negro 48x48
- [ ] Mostrar distancia/tiempo
- [ ] Tap para expandir
- [ ] Integrar en MapControlsBar

### Fase 10: Ajustes Visuales 🎯
- [ ] RouteChip con aspect ratio 3.5:1
- [ ] Tamaño de fuente dinámico
- [ ] Ajustar espaciados según DesignSystem
- [ ] Verificar corner radius

### Fase 11: Onboarding 📚
- [ ] Crear OnboardingScreen
- [ ] 7 páginas con ViewPager
- [ ] Importar/crear imágenes SVG
- [ ] Permisos de ubicación
- [ ] Guardar estado en DataStore

### Fase 12: Settings 🔧
- [ ] Crear SettingsScreen
- [ ] About, Help, Tutorial
- [ ] Privacy Policy, Terms
- [ ] Licenses, Contact
- [ ] Developer Settings

### Fase 13: Recursos Visuales 🖼️
- [ ] Importar SVGs de ciudades
- [ ] Importar SVGs de modos
- [ ] Importar SVGs de onboarding
- [ ] Crear drawables vectoriales

### Fase 14: AR View Real 🥽
- [ ] Implementar ARCore
- [ ] Renderizar rutas en AR
- [ ] Controles de cámara
- [ ] Indicadores de dirección

---

## 📈 Progreso Actual

### ✅ Completado (85%)
- Core functionality (rutas, cálculos, tracking)
- Database (trips, favorites, saved places)
- 15 modales funcionales
- MapControlsBar
- PersistentBottomSheet
- LocationInputRow
- HomeScreen refactorizado
- RouteGrid
- EmptyStates

### 🔧 En Progreso (10%)
- Design System completo
- TripBanner circular
- RouteChip aspect ratio

### ❌ Pendiente (5%)
- Onboarding
- Settings
- Recursos visuales (SVGs)
- AR View real

---

## 🎯 Prioridad de Implementación

1. **ALTA** 🔥
   - TripBanner circular (visual crítico)
   - Design System completo (base para todo)
   - RouteChip aspect ratio (consistencia visual)

2. **MEDIA** 🟡
   - Onboarding (primera impresión)
   - Recursos visuales (SVGs)
   - Settings básico

3. **BAJA** 🟢
   - AR View real (feature avanzado)
   - Settings avanzado
   - Developer tools

---

## 📝 Notas Importantes

### 🎨 Efecto Glass
iOS usa `.ultraThinMaterial` o `.glassEffect` (iOS 26+)
Android debe usar `Surface` con `alpha = 0.9f` y `tonalElevation`

### 🔘 Botones Circulares
iOS: `Circle().fill(.ultraThinMaterial)`
Android: `Surface(shape = CircleShape, color = surface.copy(alpha = 0.9f))`

### 📐 Aspect Ratio
iOS: `.aspectRatio(3.5, contentMode: .fit)`
Android: `Modifier.aspectRatio(3.5f)`

### 🌑 Sombras
iOS: `.shadow(color:radius:x:y:)`
Android: `shadowElevation` en `Surface`

### 🎭 Animaciones
iOS: `withAnimation(.spring())`
Android: `animateFloatAsState(animationSpec = spring())`

---

**Documentado por:** Kiro AI 🤖  
**Fecha:** 5 de Marzo, 2026 🗓️  
**Versión:** 1.0 📋
