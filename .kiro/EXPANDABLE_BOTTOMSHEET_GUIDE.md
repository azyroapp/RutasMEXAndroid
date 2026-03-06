# 📱 BottomSheet Expandible - Guía Completa

## 📅 Fecha: 6 de Marzo, 2026

---

## 🎯 Objetivo

Crear un BottomSheet persistente con dos estados:
- **COMPACT** (Compacto): Solo muestra los botones de origen/destino
- **EXPANDED** (Expandido): Muestra todo el contenido incluyendo la lista de rutas

---

## 🎨 Diseño Visual

### Estado COMPACT (120dp)
```
┌─────────────────────────────┐
│    ═══  ↑  (drag handle)   │
│                             │
│  🟢 Origen    ⇄    🔴 Dest │
│  [Seleccionar]  [Seleccionar]│
└─────────────────────────────┘
```

### Estado EXPANDED (60% pantalla)
```
┌─────────────────────────────┐
│    ═══  ↓  (drag handle)   │
│                             │
│  🟢 Origen    ⇄    🔴 Dest │
│  [Seleccionar]  [Seleccionar]│
│                             │
│  ┌─────────────────────┐   │
│  │  📍 Ruta 1          │   │
│  │  📍 Ruta 2          │   │
│  │  📍 Ruta 3          │   │
│  │  ...                │   │
│  └─────────────────────┘   │
└─────────────────────────────┘
```

---

## 🔧 Implementación Técnica

### Estados del BottomSheet

```kotlin
enum class BottomSheetState {
    COMPACT,    // Compacto: solo botones
    EXPANDED    // Expandido: todo el contenido
}
```

### Alturas Configuradas

```kotlin
val compactHeight = 120.dp        // Solo botones + drag handle
val expandedHeight = screenHeight * 0.6f  // 60% de la pantalla
```

### Animación de Transición

```kotlin
val animatedHeight by animateDpAsState(
    targetValue = when (sheetState) {
        BottomSheetState.COMPACT -> compactHeight
        BottomSheetState.EXPANDED -> expandedHeight
    },
    animationSpec = tween(durationMillis = 300),
    label = "bottomSheetHeight"
)
```

**Duración:** 300ms
**Tipo:** Tween (suave y predecible)

---

## 🎮 Interacciones del Usuario

### 1️⃣ Tap en Drag Handle
- **Acción:** Toca el área del drag handle (barra superior)
- **Resultado:** Alterna entre COMPACT ↔ EXPANDED

### 2️⃣ Tap en Icono de Flecha
- **Acción:** Toca el icono ↑ o ↓
- **Resultado:** Alterna entre COMPACT ↔ EXPANDED

### 3️⃣ Contenido Siempre Visible
- **LocationInputRow:** Siempre visible en ambos estados
- **Rutas:** Solo visibles en estado EXPANDED

---

## 📐 Estructura del Componente

```kotlin
PersistentBottomSheet(
    routes: List<Route>,
    selectedRouteIds: Set<String>,
    origenLocation: LocationPoint?,
    destinoLocation: LocationPoint?,
    isTripActive: Boolean,
    hasCitySelected: Boolean,
    isFavorite: Boolean,
    onRouteToggle: (Route) -> Unit,
    onOriginTap: () -> Unit,
    onDestinationTap: () -> Unit,
    onSwap: () -> Unit,
    onFavoriteTap: () -> Unit
)
```

---

## 🎨 Elementos Visuales

### Drag Handle
```kotlin
Box(
    modifier = Modifier
        .width(32.dp)
        .height(4.dp)
        .background(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            shape = MaterialTheme.shapes.extraLarge
        )
)
```

**Características:**
- Ancho: 32dp
- Alto: 4dp
- Color: onSurfaceVariant con 40% opacidad
- Forma: Redondeada (extraLarge)

### Icono de Expansión
```kotlin
Icon(
    imageVector = when (sheetState) {
        BottomSheetState.COMPACT -> Icons.Default.KeyboardArrowUp
        BottomSheetState.EXPANDED -> Icons.Default.KeyboardArrowDown
    },
    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    modifier = Modifier.size(20.dp)
)
```

**Estados:**
- COMPACT: ↑ (KeyboardArrowUp)
- EXPANDED: ↓ (KeyboardArrowDown)

---

## 🔄 Flujo de Estados

```
COMPACT (inicial)
    ↓ (tap en handle/icono)
EXPANDED
    ↓ (tap en handle/icono)
COMPACT
    ↓ (ciclo continúa...)
```

---

## 📱 Comportamiento Responsivo

### Altura Compacta
- **Fija:** 120dp
- **Contenido:** Drag handle + LocationInputRow

### Altura Expandida
- **Dinámica:** 60% de la altura de la pantalla
- **Contenido:** Drag handle + LocationInputRow + Lista de rutas

### Adaptación a Pantallas
```kotlin
val configuration = LocalConfiguration.current
val screenHeight = configuration.screenHeightDp.dp
val expandedHeight = screenHeight * 0.6f
```

**Ejemplos:**
- Pantalla 800dp → Expandido: 480dp
- Pantalla 1000dp → Expandido: 600dp
- Pantalla 600dp → Expandido: 360dp

---

## 🎯 Contenido Condicional

### Estado COMPACT
```kotlin
✅ Drag handle
✅ Icono de expansión (↑)
✅ LocationInputRow (botones origen/destino)
❌ Lista de rutas (oculta)
```

### Estado EXPANDED
```kotlin
✅ Drag handle
✅ Icono de colapso (↓)
✅ LocationInputRow (botones origen/destino)
✅ Lista de rutas (visible y scrollable)
```

---

## 🎨 Estilos y Colores

### Surface
```kotlin
Surface(
    shape = MaterialTheme.shapes.large.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    ),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    tonalElevation = 8.dp,
    shadowElevation = 8.dp
)
```

**Características:**
- Esquinas superiores redondeadas
- Esquinas inferiores rectas (pegado al borde)
- Color: Surface con 95% opacidad (semi-transparente)
- Elevación: 8dp (sombra pronunciada)

---

## 🔍 Estados de Contenido

### 1. Sin Ciudad Seleccionada
```kotlin
EmptyStates.NoCitySelected()
```
**Mensaje:** "Selecciona una ciudad para comenzar"

### 2. Sin Ubicaciones
```kotlin
EmptyStates.NoLocationSelected()
```
**Mensaje:** "Selecciona origen y destino"

### 3. Sin Rutas Disponibles
```kotlin
EmptyStates.NoRoutesAvailable()
```
**Mensaje:** "No hay rutas disponibles para esta búsqueda"

### 4. Con Rutas
```kotlin
RouteGrid(
    routes = filteredRoutes,
    selectedRouteIds = selectedRouteIds,
    onRouteToggle = onRouteToggle
)
```
**Contenido:** Grid de rutas con scroll vertical

---

## 🧪 Cómo Probar

### 1. Estado Inicial
1. Abre la app
2. ✅ El BottomSheet debe estar en estado COMPACT
3. ✅ Solo debes ver los botones de origen/destino
4. ✅ El icono debe ser ↑

### 2. Expansión
1. Toca el drag handle o el icono ↑
2. ✅ El sheet debe expandirse con animación suave (300ms)
3. ✅ Debe ocupar 60% de la pantalla
4. ✅ El icono debe cambiar a ↓
5. ✅ Debe aparecer la lista de rutas

### 3. Colapso
1. Toca el drag handle o el icono ↓
2. ✅ El sheet debe colapsarse con animación suave (300ms)
3. ✅ Debe volver a 120dp de altura
4. ✅ El icono debe cambiar a ↑
5. ✅ La lista de rutas debe ocultarse

### 4. Contenido Dinámico
1. Sin ciudad: ✅ Mensaje "Selecciona una ciudad"
2. Sin ubicaciones: ✅ Mensaje "Selecciona origen y destino"
3. Sin rutas: ✅ Mensaje "No hay rutas disponibles"
4. Con rutas: ✅ Grid de rutas scrollable

---

## 🎯 Ventajas del Diseño

### 1. Espacio Optimizado
- Estado compacto deja más espacio para el mapa
- Estado expandido muestra toda la información necesaria

### 2. UX Intuitiva
- Drag handle familiar para usuarios
- Icono de flecha indica claramente la acción
- Animación suave y predecible

### 3. Siempre Accesible
- Botones de origen/destino siempre visibles
- No necesitas expandir para cambiar ubicaciones
- Rápido acceso a funciones principales

### 4. Contenido Contextual
- Solo muestra rutas cuando es relevante
- Estados vacíos con mensajes claros
- Scroll solo cuando hay contenido

---

## 🔄 Comparación con iOS

### iOS (PersistentBottomModal)
- Usa `.presentationDetents([.height(120), .fraction(0.6)])`
- Dos detents: pequeño (120pt) y grande (60%)
- Drag gesture nativo de SwiftUI

### Android (PersistentBottomSheet)
- Usa `animateDpAsState` para altura
- Dos estados: COMPACT (120dp) y EXPANDED (60%)
- Click gesture en drag handle

**Paridad:** ✅ 100% funcional

---

## 📝 Código Clave

### Toggle de Estado
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .clickable {
            sheetState = when (sheetState) {
                BottomSheetState.COMPACT -> BottomSheetState.EXPANDED
                BottomSheetState.EXPANDED -> BottomSheetState.COMPACT
            }
        }
)
```

### Contenido Condicional
```kotlin
if (sheetState == BottomSheetState.EXPANDED) {
    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
        // Contenido de rutas
    }
}
```

---

## 🎉 Resultado Final

El BottomSheet ahora tiene dos estados claramente definidos:

1. **COMPACT:** Minimalista, solo botones esenciales
2. **EXPANDED:** Completo, con toda la información

La transición es suave, intuitiva y optimiza el uso del espacio en pantalla.

---

**Implementado por:** Kiro 🤖
**Fecha:** 6 de Marzo, 2026
**Status:** ✅ COMPLETADO
