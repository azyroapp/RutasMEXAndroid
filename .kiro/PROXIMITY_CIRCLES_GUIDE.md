# 🎯 Guía de Círculos de Proximidad

## 📅 Fecha: 6 de Marzo, 2026

---

## 🗺️ Visualización de Círculos en el Mapa

### 📍 Círculos de Radio de Búsqueda

Estos círculos se muestran **SIEMPRE** que hay origen/destino seleccionado:

#### 🟢 Círculo de Origen
- **Color:** Verde (`#00FF00`)
- **Radio por defecto:** 200 metros
- **Propósito:** Indica el área de búsqueda alrededor del punto de origen
- **Configurable:** Sí, mediante `RadiusConfigModal`
- **Visible cuando:** `origenLocation != null`

#### 🔴 Círculo de Destino
- **Color:** Rojo (`#FF0000`)
- **Radio por defecto:** 200 metros
- **Propósito:** Indica el área de búsqueda alrededor del punto de destino
- **Configurable:** Sí, mediante `RadiusConfigModal`
- **Visible cuando:** `destinoLocation != null`

---

### 🔔 Círculos de Proximidad (Far/Medium/Near)

Estos círculos se muestran **SIEMPRE** que hay un destino seleccionado:

#### 🔴 Círculo Far (Lejos)
- **Color:** Rojo claro (`#FF6B6B`)
- **Radio por defecto:** 500 metros (100% de `proximityConfig.distance`)
- **Propósito:** Primera alerta de proximidad al destino
- **Notificación:** "Te estás acercando al destino"
- **Visible cuando:** `destinoLocation != null`

#### 🟡 Círculo Medium (Medio)
- **Color:** Amarillo (`#FFA500`)
- **Radio por defecto:** 300 metros (60% de `proximityConfig.distance`)
- **Propósito:** Segunda alerta de proximidad al destino
- **Notificación:** "Estás cerca del destino"
- **Visible cuando:** `destinoLocation != null`

#### 🟢 Círculo Near (Cerca)
- **Color:** Verde (`#00FF00`)
- **Radio por defecto:** 150 metros (30% de `proximityConfig.distance`)
- **Propósito:** Alerta final de proximidad al destino
- **Notificación:** "¡Prepárate para bajar!"
- **Visible cuando:** `destinoLocation != null`

---

## 🎨 Estilos Visuales

### Círculos de Radio de Búsqueda
```kotlin
Circle(
    center = location,
    radius = radius,
    fillColor = Color(0x20XXXXXX), // 12% opacidad
    strokeColor = Color(0xFFXXXXXX), // Sólido
    strokeWidth = 2f
)
```

### Círculos de Proximidad
```kotlin
Circle(
    center = destinoLocation,
    radius = radius,
    fillColor = Color(0x10XXXXXX), // 6% opacidad
    strokeColor = Color(0xFFXXXXXX), // Sólido
    strokeWidth = 3f
)
```

---

## 🔧 Configuración

### Radios de Búsqueda
**Acceso:** Botón de configuración en `MapControlsBar`

**Modal:** `RadiusConfigModal`

**Valores configurables:**
- Radio de origen: 50m - 1000m
- Radio de destino: 50m - 1000m

**Persistencia:** `PreferencesManager.saveSearchRadii()`

### Radios de Proximidad
**Acceso:** Menú de opciones → "Configurar proximidad"

**Modal:** `ProximityConfigModalNew`

**Valores configurables:**
- Radio base: 100m - 1000m
- Far = Radio base × 1.0
- Medium = Radio base × 0.6
- Near = Radio base × 0.3

**Persistencia:** `PreferencesManager.saveProximityConfig()`

---

## 📊 Comparación iOS vs Android

### iOS
```swift
// Círculos de proximidad solo si notificaciones autorizadas
if notificationService.isAuthorized {
    ProximityCircles(center: destino.coordinate)
}
```

**Condición:** `destino != nil && notificationService.isAuthorized`

### Android
```kotlin
// Círculos de proximidad siempre que hay destino
showProximityCircles = destinoLocation != null
```

**Condición:** `destinoLocation != null`

**Razón de la diferencia:**
- iOS: Requiere autorización de notificaciones (restricción del sistema)
- Android: Sin restricciones, se muestran siempre para mejor UX

---

## 🎯 Comportamiento Durante Viaje

### Antes del Viaje
- ✅ Círculos de radio de búsqueda visibles
- ✅ Círculos de proximidad visibles
- ℹ️ Los círculos son informativos

### Durante el Viaje
- ✅ Círculos de radio de búsqueda visibles
- ✅ Círculos de proximidad visibles
- 🔔 Los círculos activan notificaciones cuando el usuario entra en cada zona

### Después del Viaje
- ✅ Círculos de radio de búsqueda visibles
- ✅ Círculos de proximidad visibles
- ℹ️ Los círculos vuelven a ser informativos

---

## 🧪 Cómo Probar

### 1. Círculos de Radio de Búsqueda
1. Selecciona una ciudad
2. Toca en "Origen" y selecciona un punto
3. ✅ Deberías ver un círculo verde alrededor del origen
4. Toca en "Destino" y selecciona un punto
5. ✅ Deberías ver un círculo rojo alrededor del destino

### 2. Círculos de Proximidad
1. Selecciona un destino
2. ✅ Deberías ver 3 círculos concéntricos alrededor del destino:
   - 🔴 Círculo grande (Far - rojo claro)
   - 🟡 Círculo mediano (Medium - amarillo)
   - 🟢 Círculo pequeño (Near - verde)

### 3. Configuración de Radios
1. Toca el botón de configuración en `MapControlsBar`
2. Ajusta los radios de origen y destino
3. ✅ Los círculos deberían actualizarse en tiempo real

### 4. Configuración de Proximidad
1. Abre el menú de opciones (⋮)
2. Selecciona "Configurar proximidad"
3. Ajusta el radio base
4. ✅ Los 3 círculos de proximidad deberían actualizarse proporcionalmente

---

## 🐛 Troubleshooting

### No veo los círculos de proximidad
**Causa:** No hay destino seleccionado
**Solución:** Selecciona un punto de destino

### Los círculos son muy grandes/pequeños
**Causa:** Configuración de radios
**Solución:** Ajusta los radios en la configuración

### Los círculos no se actualizan
**Causa:** Caché de mapa
**Solución:** Cambia el tipo de mapa (Normal ↔ Satélite) para forzar actualización

---

## 📝 Código Relevante

### MapView.kt
```kotlin
// Círculos de proximidad
if (showProximityCircles) {
    Circle(center = destinoLatLng, radius = proximityFarRadius, ...)
    Circle(center = destinoLatLng, radius = proximityMediumRadius, ...)
    Circle(center = destinoLatLng, radius = proximityNearRadius, ...)
}
```

### HomeScreen.kt
```kotlin
MapView(
    ...
    proximityFarRadius = proximityConfig.distance,
    proximityMediumRadius = proximityConfig.distance * 0.6,
    proximityNearRadius = proximityConfig.distance * 0.3,
    showProximityCircles = destinoLocation != null,
    ...
)
```

### HomeViewModel.kt
```kotlin
private val _proximityConfig = MutableStateFlow(ProximityConfig())
val proximityConfig: StateFlow<ProximityConfig> = _proximityConfig.asStateFlow()

fun updateProximityConfig(distance: Double, ...) {
    val config = ProximityConfig(distance = distance, ...)
    _proximityConfig.value = config
    preferencesManager.saveProximityConfig(...)
}
```

---

## 🎉 Resultado Final

Los círculos de proximidad ahora se muestran correctamente en el mapa de Android, proporcionando una visualización clara de las zonas de alerta alrededor del destino. La implementación es más simple que iOS (sin restricciones de notificaciones) pero igualmente funcional.

---

**Implementado por:** Kiro 🤖
**Fecha:** 6 de Marzo, 2026
**Status:** ✅ COMPLETADO
