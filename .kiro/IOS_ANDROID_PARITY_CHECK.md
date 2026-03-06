# 📱 Verificación de Paridad iOS-Android

## 🎯 Objetivo
Verificar que los componentes, funcionalidad y diseño sean idénticos entre iOS y Android.

---

## 📊 RESUMEN EJECUTIVO

**Fecha:** 2026-03-06 (Viernes)  
**Componentes Analizados:** 1/10 (10%)  
**Estado General:** 🟡 Diferencias críticas encontradas

### 🔴 PROBLEMAS CRÍTICOS DETECTADOS

1. **Persistent Bottom Sheet - Falta funcionalidad colapsado/expandido en iOS**
   - ❌ Android: 2 estados (COLLAPSED 80dp, EXPANDED 50%)
   - ❌ iOS: 1 estado (siempre visible, altura fija)
   - 💥 **Impacto:** Experiencia de usuario inconsistente, iOS ocupa más espacio permanentemente

### 🟡 DIFERENCIAS MEDIAS

2. **LocationInputRow en diferente posición**
   - Android: Dentro del contenido del sheet
   - iOS: En toolbar del NavigationStack
   - 📐 **Impacto:** Diseño visual diferente, espaciado diferente

3. **Radios de búsqueda diferentes**
   - Android: Radio fijo 200m
   - iOS: Radio variable configurable
   - ⚙️ **Impacto:** iOS más flexible, Android más simple

### 🟢 DIFERENCIAS MENORES

4. **Estado vacío "Sin ciudad" falta en iOS**
   - Android: 3 estados vacíos
   - iOS: 2 estados vacíos
   - 📊 **Impacto:** Mensaje menos claro en iOS

---

## 🎯 PLAN DE ACCIÓN INMEDIATO

### Prioridad 1 (CRÍTICA): Implementar estados en iOS
```swift
// Opción recomendada: Usar .presentationDetents dinámicos
@State private var selectedDetent: PresentationDetent = .height(80)

.sheet(isPresented: .constant(true)) {
    PersistentBottomModal(...)
}
.presentationDetents([.height(80), .fraction(0.5)], selection: $selectedDetent)
```

### Prioridad 2 (MEDIA): Unificar posición de LocationInputRow
- Decidir: ¿Mover a contenido (Android) o a toolbar (iOS)?
- Implementar en ambas plataformas

### Prioridad 3 (BAJA): Agregar estado "Sin ciudad" en iOS
```swift
if AppStateManager.shared.selectedCityId == nil {
    EmptyStateView(icon: "building.2", title: "Selecciona una ciudad", ...)
}
```

---

## 1️⃣ PERSISTENT BOTTOM SHEET / MODAL INFERIOR

### 📊 COMPARACIÓN COMPLETA

---

#### 🤖 ANDROID: `PersistentBottomSheet.kt`
**Ubicación:** `app/src/main/java/com/azyroapp/rutasmex/ui/components/PersistentBottomSheet.kt`

**Arquitectura:**
- ✅ Componente independiente con estados propios
- ✅ Enum `BottomSheetState` (COLLAPSED, EXPANDED)
- ✅ Gestión de estado local con `remember { mutableStateOf() }`
- ✅ Drag gesture manual con `detectVerticalDragGestures`

**Estados del Modal:**
- 🔽 **COLLAPSED:** 80dp (solo LocationInputRow)
- 🔼 **EXPANDED:** 50% pantalla (LocationInputRow + rutas)
- ⚡ **Transición:** Animación 300ms con `animateDpAsState`
- 📏 **Umbral:** 30% de la diferencia de altura

**Interacción:**
- ✅ Drag handle visual (barra gris 32dp × 4dp)
- ✅ Drag gesture nativo con acumulación de offset
- ✅ Cambio de estado al soltar (onDragEnd)
- ✅ Deslizar arriba: COLLAPSED → EXPANDED
- ✅ Deslizar abajo: EXPANDED → COLLAPSED

**Contenido:**
- 📍 **LocationInputRow:** Siempre visible (ambos estados)
- 🚌 **RouteGrid:** Solo visible en EXPANDED
- 📊 **Estados vacíos:**
  - Sin ciudad seleccionada
  - Sin ubicación seleccionada
  - Sin rutas disponibles

**Diseño Visual:**
```
┌─────────────────────────┐
│    ━━━━ (drag handle)   │ ← 4dp height, 32dp width
│                         │
│  📍 Origen → Destino 🔄 │ ← LocationInputRow (SIEMPRE)
│                         │
├─────────────────────────┤ ← Solo en EXPANDED
│                         │
│  🚌 Ruta 1  🚌 Ruta 2  │
│  🚌 Ruta 3  🚌 Ruta 4  │
│                         │
└─────────────────────────┘
```

**Filtrado de Rutas:**
- ✅ Radio de búsqueda: 200m (fijo)
- ✅ Filtrado por origen Y destino
- ✅ Método `filterRoutes()` local
- ✅ Usa `route.passesNearPoint()`

**Propiedades:**
- 🎨 Surface con alpha 0.95
- 📐 Elevación: 8dp (tonal + shadow)
- 🔲 Esquinas redondeadas solo arriba
- 📦 Padding: 16dp horizontal, 4dp top, 16dp bottom

---

#### 🍎 iOS: `PersistentBottomModal.swift`
**Ubicación:** `RutasMEX/Features/Home/Views/PersistentBottomModal.swift`

**Arquitectura:**
- ✅ Modal presentado como `.sheet(isPresented: .constant(true))`
- ❌ **NO tiene estados colapsado/expandido**
- ✅ Usa `NavigationStack` como contenedor
- ✅ Gestión de múltiples modales secundarios

**Estados del Modal:**
- 📱 **ÚNICO ESTADO:** Siempre visible, altura fija
- ❌ **NO hay COLLAPSED/EXPANDED**
- ❌ **NO hay animación de transición**
- ✅ Usa `.presentationDetents([.medium])` para otros modales

**Interacción:**
- ✅ Drag indicator nativo de SwiftUI (`.presentationDragIndicator(.visible)`)
- ❌ **NO tiene drag gesture para cambiar estados**
- ❌ **NO se puede colapsar/expandir**
- ✅ Se puede deslizar para cerrar (comportamiento nativo de sheet)

**Contenido:**
- 📍 **LocationInputRow:** En toolbar (`.principal`)
- 🚌 **RouteGrid:** Siempre visible en ScrollView
- 📊 **Estados vacíos:**
  - Sin origen ni destino
  - Sin rutas disponibles
  - ❌ NO verifica si hay ciudad seleccionada

**Diseño Visual:**
```
┌─────────────────────────┐
│  📍 Origen → Destino 🔄 │ ← Toolbar (NavigationStack)
├─────────────────────────┤
│                         │
│  🚌 Ruta 1  🚌 Ruta 2  │ ← ScrollView (SIEMPRE visible)
│  🚌 Ruta 3  🚌 Ruta 4  │
│                         │
│  (scroll infinito)      │
│                         │
└─────────────────────────┘
```

**Filtrado de Rutas:**
- ✅ Radio de búsqueda: Variable (`homeViewModel.originRadius`, `homeViewModel.destinationRadius`)
- ✅ Filtrado por origen Y destino
- ✅ Computed property `filteredRoutes`
- ✅ Usa `RouteAnalysisEngine.shared.isLocationNearRoute()`
- ✅ Análisis completo con `RoutePlannerViewModel`

**Propiedades:**
- 🎨 NavigationStack con toolbar
- 📐 Padding: 16dp horizontal, variable vertical
- 🔲 Esquinas redondeadas nativas de sheet
- 📦 `.ignoresSafeArea(.container, edges: .bottom)`

**Modales Secundarios:**
- ✅ LocationSelectionModal
- ✅ RouteSearchModal
- ✅ RadiusConfigModal
- ✅ FavoritesModal
- ✅ SaveFavoriteModal
- ✅ TripHistoryModal
- ✅ SavedPlacesManagerModal
- ✅ ARModal (fullScreenCover)
- ✅ ProximityConfigModal
- ✅ ArrivalModal
- ✅ CityStoreView
- ✅ RouteSelectionForTripModal

---

### 🔍 ANÁLISIS DE DIFERENCIAS CRÍTICAS

| Característica | Android | iOS | Diferencia |
|----------------|---------|-----|------------|
| **Arquitectura** | Componente con estados | Sheet siempre visible | 🔴 DIFERENTE |
| **Estados** | COLLAPSED + EXPANDED | Solo 1 estado | 🔴 FALTA EN iOS |
| **Drag gesture** | Manual con offset | Nativo de sheet | 🔴 DIFERENTE |
| **Animación** | 300ms entre estados | No aplica | 🔴 FALTA EN iOS |
| **Drag handle** | Manual (barra gris) | Nativo de SwiftUI | 🟡 SIMILAR |
| **LocationInputRow** | En contenido | En toolbar | 🟡 DIFERENTE POSICIÓN |
| **RouteGrid** | Solo en EXPANDED | Siempre visible | 🔴 DIFERENTE |
| **Estados vacíos** | 3 tipos | 2 tipos | 🟡 FALTA 1 EN iOS |
| **Filtrado rutas** | Radio fijo 200m | Radio variable | 🟡 DIFERENTE |
| **Análisis rutas** | Método local | ViewModel dedicado | 🟡 DIFERENTE |
| **Modales secundarios** | Pocos | 12 modales | 🔴 iOS MÁS COMPLETO |

---

### ⚠️ PROBLEMAS DE PARIDAD DETECTADOS

#### 🔴 CRÍTICO: Falta funcionalidad colapsado/expandido en iOS

**Android:**
```kotlin
// Usuario puede colapsar para ver más mapa
sheetState = BottomSheetState.COLLAPSED  // 80dp

// Usuario puede expandir para ver rutas
sheetState = BottomSheetState.EXPANDED   // 50% pantalla
```

**iOS:**
```swift
// ❌ NO EXISTE - Sheet siempre ocupa el mismo espacio
// Usuario NO puede colapsar/expandir
```

**Impacto:**
- 📱 iOS ocupa más espacio de pantalla permanentemente
- 🗺️ Usuario no puede maximizar vista del mapa
- 👆 Experiencia de usuario inconsistente entre plataformas

---

#### 🟡 MEDIO: LocationInputRow en diferente posición

**Android:**
```kotlin
// Dentro del contenido del sheet
Column {
    DragHandle()
    LocationInputRow()  // ← Aquí
    if (expanded) { RouteGrid() }
}
```

**iOS:**
```swift
// En el toolbar del NavigationStack
.toolbar {
    ToolbarItem(placement: .principal) {
        LocationInputRow()  // ← Aquí
    }
}
```

**Impacto:**
- 🎨 Diseño visual diferente
- 📐 Espaciado diferente
- ✅ Funcionalidad idéntica

---

#### 🟡 MEDIO: Filtrado de rutas con radios diferentes

**Android:**
```kotlin
// Radio fijo en el código
val searchRadius = 200.0
```

**iOS:**
```swift
// Radio variable desde ViewModel
homeViewModel.originRadius
homeViewModel.destinationRadius
```

**Impacto:**
- ⚙️ iOS permite configurar radios
- 🔒 Android tiene radio fijo
- 🎯 iOS más flexible

---

#### 🟢 MENOR: Estado vacío "Sin ciudad" falta en iOS

**Android:**
```kotlin
when {
    !hasCitySelected -> EmptyStates.NoCitySelected()
    origenLocation == null && destinoLocation == null -> EmptyStates.NoLocationSelected()
    filteredRoutes.isEmpty() -> EmptyStates.NoRoutesAvailable()
}
```

**iOS:**
```swift
if homeViewModel.origenLocation == nil && homeViewModel.destinoLocation == nil {
    EmptyStateView(...)
} else if filteredRoutes.isEmpty {
    EmptyStateView(...)
}
// ❌ NO verifica si hay ciudad seleccionada
```

**Impacto:**
- 📊 Mensaje menos claro en iOS cuando no hay ciudad
- ✅ Funcionalidad no afectada (AppStateManager lo maneja)

---

### 🎯 RECOMENDACIONES DE PARIDAD

#### 1️⃣ ALTA PRIORIDAD: Implementar estados colapsado/expandido en iOS

**Opción A: Usar `.presentationDetents` dinámicos**
```swift
@State private var detents: Set<PresentationDetent> = [.height(80), .fraction(0.5)]
@State private var selectedDetent: PresentationDetent = .height(80)

.sheet(isPresented: .constant(true)) {
    PersistentBottomModal(...)
}
.presentationDetents(detents, selection: $selectedDetent)
.presentationDragIndicator(.visible)
```

**Opción B: Crear componente custom con gesture**
```swift
// Similar a Android, con DragGesture y animación
@State private var sheetHeight: CGFloat = 80
@State private var isExpanded = false

var body: some View {
    VStack {
        // Drag handle
        // LocationInputRow
        if isExpanded {
            // RouteGrid
        }
    }
    .frame(height: sheetHeight)
    .gesture(DragGesture()...)
}
```

---

#### 2️⃣ MEDIA PRIORIDAD: Unificar posición de LocationInputRow

**Opción A: Mover a contenido (como Android)**
```swift
VStack {
    DragHandle()
    LocationInputRow()  // ← Mover aquí
    if isExpanded {
        RouteGrid()
    }
}
```

**Opción B: Mantener en toolbar pero ajustar Android**
```kotlin
// Mover LocationInputRow a TopAppBar en Android
```

---

#### 3️⃣ BAJA PRIORIDAD: Agregar estado "Sin ciudad" en iOS

```swift
if AppStateManager.shared.selectedCityId == nil {
    EmptyStateView(
        icon: "building.2",
        title: "Selecciona una ciudad",
        message: "Elige una ciudad para ver las rutas disponibles."
    )
} else if homeViewModel.origenLocation == nil && homeViewModel.destinoLocation == nil {
    // ...
}
```

---

### 📋 CHECKLIST DE IMPLEMENTACIÓN

#### Para lograr paridad completa:

- [ ] **iOS:** Implementar estados COLLAPSED/EXPANDED
- [ ] **iOS:** Agregar drag gesture para cambiar estados
- [ ] **iOS:** Animación suave entre estados (300ms)
- [ ] **iOS:** Ocultar RouteGrid en estado COLLAPSED
- [ ] **iOS:** Agregar estado vacío "Sin ciudad"
- [ ] **Android:** Considerar radios variables (opcional)
- [ ] **Ambos:** Unificar posición de LocationInputRow
- [ ] **Ambos:** Documentar comportamiento esperado

---

### 🎨 DISEÑO OBJETIVO (PARIDAD COMPLETA)

**Estado COLLAPSED (80dp):**
```
┌─────────────────────────┐
│    ━━━━ (drag handle)   │
│                         │
│  📍 Origen → Destino 🔄 │
│                         │
└─────────────────────────┘
         ↕️ Drag arriba
```

**Estado EXPANDED (50% pantalla):**
```
┌─────────────────────────┐
│    ━━━━ (drag handle)   │
│                         │
│  📍 Origen → Destino 🔄 │
│                         │
├─────────────────────────┤
│                         │
│  🚌 Ruta 1  🚌 Ruta 2  │
│  🚌 Ruta 3  🚌 Ruta 4  │
│  🚌 Ruta 5  🚌 Ruta 6  │
│                         │
│  (scroll si hay más)    │
│                         │
└─────────────────────────┘
         ↕️ Drag abajo
```

---

---

## 📋 CHECKLIST DE COMPONENTES A VERIFICAR

### Componentes Principales
- [x] 1. Persistent Bottom Sheet / Modal Inferior ✅ **ANALIZADO**
  - 🔴 CRÍTICO: Falta estados colapsado/expandido en iOS
  - 🟡 MEDIO: LocationInputRow en diferente posición
  - 🟡 MEDIO: Radios de búsqueda diferentes
  - 🟢 MENOR: Estado vacío "Sin ciudad" falta en iOS
- [x] 2. LocationSelectionModal / Modal de selección ✅ **COMPLETADO**
  - 🟢 PARIDAD 100%: Android ahora idéntico a iOS
  - ✅ Campo de búsqueda con sugerencias
  - ✅ Botón Mi Ubicación como icono
  - ✅ Validación de ubicaciones duplicadas
  - ✅ Botones OK/Cancelar
  - ✅ Sección "Mis Lugares"
- [ ] 3. LocationInputRow / Barra de control
- [ ] 4. RouteGrid / Grid de rutas
- [ ] 5. MapView / Vista del mapa
- [ ] 6. TripHistoryModal / Modal de historial
- [ ] 7. RouteInfoModal / Modal de información
- [ ] 8. CitySelector / Selector de ciudad
- [ ] 9. SettingsModal / Modal de configuración
- [ ] 10. ProximityConfig / Configuración de proximidad

### Funcionalidades
- [ ] Drag gestures ⚠️ **DIFERENTE** (iOS no tiene para colapsar/expandir)
- [ ] Animaciones ⚠️ **DIFERENTE** (iOS no tiene transición de estados)
- [x] Estados vacíos ✅ **SIMILAR** (iOS falta 1 estado)
- [x] Filtrado de rutas ✅ **SIMILAR** (radios diferentes)
- [ ] Navegación
- [ ] Permisos de ubicación
- [ ] Modo oscuro
- [ ] Suscripciones

### Diseño Visual
- [ ] Colores
- [ ] Tipografía
- [ ] Espaciados ⚠️ **DIFERENTE** (LocationInputRow en toolbar vs contenido)
- [ ] Elevaciones/Sombras
- [ ] Esquinas redondeadas
- [ ] Iconos

---

## 🚀 ESTADO GENERAL

**Progreso:** 2/10 componentes verificados ✅ (20%)

**Componentes Completados:**
1. ✅ Persistent Bottom Sheet - Analizado (diferencias documentadas)
2. ✅ LocationSelectionModal - Paridad 100% lograda

**Prioridad de Implementación:**
1. 🔴 **ALTA:** Implementar estados colapsado/expandido en iOS (PersistentBottomSheet)
2. 🟡 **MEDIA:** Unificar posición de LocationInputRow
3. 🟡 **MEDIA:** Considerar radios variables en Android
4. 🟢 **BAJA:** Agregar estado "Sin ciudad" en iOS

**Diferencias Críticas Encontradas:**
- ❌ iOS NO tiene funcionalidad de colapsar/expandir (PersistentBottomSheet)
- ❌ iOS ocupa más espacio de pantalla permanentemente
- ✅ LocationSelectionModal ahora tiene paridad 100%

**Próximo Componente a Verificar:**
- 📍 LocationInputRow (usado en ambas plataformas)

---

**Última actualización:** 2026-03-06 (Viernes)
**Responsable:** Verificación de paridad iOS-Android
**Estado:** 🟢 En progreso - 2 componentes completados, LocationSelectionModal con paridad 100%
