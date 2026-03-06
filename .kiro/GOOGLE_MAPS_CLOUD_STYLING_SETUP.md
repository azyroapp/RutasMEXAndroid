# 🗺️ Google Maps Cloud Styling - Configuración Completa

**Fecha**: 6 de Marzo de 2026  
**Método**: ✅ Map ID + Cloud Styling (Recomendado 2025+)

---

## 🎯 ¿POR QUÉ USAR MAP ID + CLOUD STYLING?

### ✅ Ventajas sobre JSON Styling

| Característica | JSON Styling | Map ID + Cloud Styling |
|---------------|--------------|------------------------|
| Cambio automático light/dark | ❌ Manual | ✅ Automático |
| Actualización de estilos | ❌ Recompilar app | ✅ Desde la nube |
| Mantenimiento | ❌ Código hardcoded | ✅ Google lo mantiene |
| Consistencia | ⚠️ Puede desactualizarse | ✅ Siempre actualizado |
| Facilidad | ⚠️ JSON largo | ✅ Solo un ID |
| Recomendación Google | ⚠️ Legacy (2024-) | ✅ Moderna (2025+) |

---

## 📋 PASOS PARA CONFIGURAR MAP ID

### 1️⃣ **Acceder a Google Cloud Console**

1. Ve a: https://console.cloud.google.com/
2. Selecciona tu proyecto (o crea uno nuevo)
3. Asegúrate de tener habilitada la **Maps SDK for Android**

---

### 2️⃣ **Crear un Map ID**

1. En el menú lateral, ve a:
   ```
   APIs & Services → Credentials
   ```

2. O accede directamente a:
   ```
   https://console.cloud.google.com/google/maps-apis/studio/maps
   ```

3. Click en **"Create Map ID"** o **"Crear ID de mapa"**

4. Configura tu Map ID:
   ```
   Nombre: RutasMEX Android
   Tipo de mapa: Android
   Descripción: Mapa principal de RutasMEX con modo oscuro automático
   ```

5. Click en **"Create"** o **"Crear"**

6. **Copia el Map ID** generado (formato: `abc123def456`)

---

### 3️⃣ **Configurar Estilo en Cloud Console**

1. En la lista de Map IDs, click en el que acabas de crear

2. En la sección **"Map Style"**, selecciona:
   ```
   ✅ Automatic (light/dark based on device settings)
   ```
   
   O si prefieres personalizar:
   ```
   - Light theme: Standard (o personalizado)
   - Dark theme: Dark (o personalizado)
   ```

3. Click en **"Save"** o **"Guardar"**

---

### 4️⃣ **Agregar Map ID a tu App**

#### Opción A: En el código (Recomendado)

Edita `MapView.kt`:

```kotlin
// Reemplaza esta línea:
val mapId = "YOUR_MAP_ID_HERE"

// Por tu Map ID real:
val mapId = "abc123def456" // Tu Map ID de Google Cloud Console
```

Y descomenta esta línea en `MapProperties`:

```kotlin
val properties by remember(mapType, isLocationPermissionGranted) {
    mutableStateOf(
        MapProperties(
            mapType = when (mapType) {
                MapType.NORMAL -> com.google.maps.android.compose.MapType.NORMAL
                MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
            },
            isMyLocationEnabled = isLocationPermissionGranted,
            mapId = mapId // ← Descomenta esta línea
        )
    )
}
```

#### Opción B: En AndroidManifest.xml

Agrega en `AndroidManifest.xml`:

```xml
<application>
    <!-- Otras configuraciones -->
    
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="${MAPS_API_KEY}" />
    
    <!-- Agregar Map ID -->
    <meta-data
        android:name="com.google.android.gms.maps.MAP_ID"
        android:value="abc123def456" /> <!-- Tu Map ID -->
</application>
```

---

### 5️⃣ **Verificar Configuración**

1. Compila y ejecuta la app:
   ```bash
   ./gradlew assembleDebug
   ```

2. Prueba el modo oscuro:
   - Activa modo oscuro en el dispositivo
   - Abre RutasMEX
   - El mapa debe cambiar automáticamente a modo oscuro

3. Prueba el modo claro:
   - Desactiva modo oscuro
   - El mapa debe cambiar automáticamente a modo claro

---

## 🎨 PERSONALIZAR ESTILOS (Opcional)

### Crear Estilo Personalizado

1. Ve a: https://console.cloud.google.com/google/maps-apis/studio/styles

2. Click en **"Create Style"** o **"Crear estilo"**

3. Usa el editor visual para personalizar:
   - Colores de carreteras
   - Colores de agua
   - Colores de parques
   - Etiquetas
   - POIs

4. Guarda el estilo con un nombre descriptivo

5. Asigna el estilo a tu Map ID:
   - Ve a tu Map ID
   - En "Map Style", selecciona tu estilo personalizado
   - Guarda los cambios

---

## 🔧 TROUBLESHOOTING

### Problema: El mapa no cambia a modo oscuro

**Solución 1**: Verifica que el Map ID esté configurado correctamente
```kotlin
// En MapView.kt, verifica que mapId esté descomentado:
mapId = "tu_map_id_aqui"
```

**Solución 2**: Verifica en Cloud Console que el estilo sea "Automatic"

**Solución 3**: Limpia y recompila:
```bash
./gradlew clean
./gradlew assembleDebug
```

---

### Problema: Error "Map ID not found"

**Solución**: Verifica que:
1. El Map ID existe en Google Cloud Console
2. El proyecto de Cloud Console es el mismo que tu API Key
3. El Map ID está habilitado para Android

---

### Problema: El mapa se ve en blanco

**Solución**: Verifica que:
1. Tu API Key tenga permisos para Maps SDK for Android
2. El Map ID esté asociado al proyecto correcto
3. No haya restricciones de API Key que bloqueen el Map ID

---

## 📊 COMPARACIÓN: ANTES vs DESPUÉS

### Antes (JSON Styling)
```kotlin
// 100+ líneas de JSON hardcoded
val darkMapStyle = remember {
    """
    [
      { "elementType": "geometry", ... },
      { "featureType": "water", ... },
      // ... 50+ líneas más
    ]
    """.trimIndent()
}

// Lógica manual para detectar dark mode
val isDarkMode = isSystemInDarkTheme()

// Aplicar estilo manualmente
mapStyleOptions = if (isDarkMode && mapType == MapType.NORMAL) {
    MapStyleOptions(darkMapStyle)
} else {
    null
}
```

**Problemas**:
- ❌ Código largo y difícil de mantener
- ❌ Cambio manual entre modos
- ❌ Si Google actualiza estilos, hay que actualizar el JSON
- ❌ Recompilar app para cambiar estilos

---

### Después (Map ID + Cloud Styling)
```kotlin
// Solo 1 línea
val mapId = "abc123def456"

// Aplicar Map ID
MapProperties(
    mapType = ...,
    isMyLocationEnabled = ...,
    mapId = mapId // ← Automático light/dark
)
```

**Ventajas**:
- ✅ Código limpio y simple
- ✅ Cambio automático entre modos
- ✅ Google mantiene los estilos actualizados
- ✅ Cambiar estilos sin recompilar app

---

## 🚀 PRÓXIMOS PASOS

### Después de Configurar Map ID:

1. **Eliminar JSON Styling** (ya no es necesario)
   - El código ya está preparado para usar Map ID
   - Solo necesitas agregar tu Map ID real

2. **Probar en Diferentes Dispositivos**
   - Android con modo oscuro
   - Android con modo claro
   - Diferentes versiones de Android

3. **Personalizar Estilos** (opcional)
   - Crear estilos personalizados en Cloud Console
   - Ajustar colores según tu marca
   - Crear variantes para diferentes ciudades

---

## 📚 RECURSOS ADICIONALES

### Documentación Oficial:
- Map IDs: https://developers.google.com/maps/documentation/get-map-id
- Cloud Styling: https://developers.google.com/maps/documentation/cloud-customization
- Style Editor: https://console.cloud.google.com/google/maps-apis/studio/styles

### Tutoriales:
- Crear Map ID: https://developers.google.com/maps/documentation/android-sdk/cloud-setup
- Personalizar estilos: https://developers.google.com/maps/documentation/cloud-customization/style-reference

---

## ✅ CHECKLIST DE CONFIGURACIÓN

- [ ] Acceder a Google Cloud Console
- [ ] Crear Map ID para Android
- [ ] Configurar estilo como "Automatic"
- [ ] Copiar Map ID generado
- [ ] Agregar Map ID en `MapView.kt`
- [ ] Descomentar línea `mapId = mapId`
- [ ] Compilar app
- [ ] Probar modo oscuro
- [ ] Probar modo claro
- [ ] Verificar que cambie automáticamente

---

## 🎉 CONCLUSIÓN

Usar **Map ID + Cloud Styling** es la forma moderna y recomendada por Google para manejar estilos de mapas en 2025+.

**Beneficios principales**:
- 🌙 Modo oscuro automático
- ☁️ Actualización desde la nube
- 🔧 Fácil mantenimiento
- 🚀 Mejor rendimiento
- ✨ Siempre actualizado

**Una vez configurado, olvídate del JSON y deja que Google maneje los estilos!** 🎨

---

**Desarrollado con ❤️ por Kiro AI**  
**Fecha**: 6 de Marzo de 2026 🚀
