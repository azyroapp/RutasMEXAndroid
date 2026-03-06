# 🔍 Funcionalidad de Búsqueda de Rutas

## 📋 Descripción General

La aplicación ahora permite buscar rutas que pasan por dos puntos específicos: origen y destino.

## 🎯 Flujo de Usuario

### 1️⃣ Seleccionar Ciudad
- Toca el FAB "Seleccionar Ciudad"
- Elige una ciudad del bottom sheet
- Las rutas de esa ciudad se cargan automáticamente

### 2️⃣ Seleccionar Origen
- En el bottom sheet inferior, toca el botón "Ubicar" junto a "Origen"
- Toca cualquier punto en el mapa
- El punto se marca con un marcador verde 🟢
- Se muestra el nombre de la ubicación (geocoding reverso)

### 3️⃣ Seleccionar Destino
- Toca el botón "Ubicar" junto a "Destino"
- Toca otro punto en el mapa
- El punto se marca con un marcador rojo 🔴
- Se muestra el nombre de la ubicación

### 4️⃣ Buscar Rutas
- El botón "Buscar Rutas" se habilita automáticamente cuando:
  - ✅ Hay una ciudad seleccionada
  - ✅ Hay un origen seleccionado
  - ✅ Hay un destino seleccionado
- Toca "Buscar Rutas"
- Se abre un modal con los resultados

### 5️⃣ Ver Resultados
El modal de resultados muestra:
- 📊 Número de rutas encontradas
- 📍 Origen y destino de la búsqueda
- 📋 Lista de rutas con:
  - Nombre de la ruta
  - Tipo (IDA, REGRESO, IDA Y REGRESO)
  - Número de paradas
  - Color identificador
  - Checkbox para seleccionar

### 6️⃣ Mostrar en Mapa
- Selecciona las rutas que quieres ver (checkbox)
- Toca "Seleccionar Todas" para marcar todas
- Toca "Ver en Mapa" para mostrar las rutas seleccionadas
- Las rutas se dibujan en el mapa con sus colores:
  - 🔵 Azul (#00C3FF) - IDA
  - 🟠 Naranja (#FF6B00) - REGRESO
  - 🟣 Morado (#9C27B0) - COMPLETO

## 🔧 Funcionalidades Adicionales

### Intercambiar Ubicaciones
- Toca el botón de intercambio (↕️) en el bottom sheet
- Origen y destino se intercambian automáticamente

### Cambiar Tipo de Mapa
- Toca el chip "Satélite" en la esquina superior derecha
- Alterna entre vista Normal y Satélite

### Long Press en el Mapa
- Mantén presionado cualquier punto del mapa
- Aparece un diálogo para establecer como origen o destino

## 🎨 Componentes UI

### Bottom Sheet Principal
- Siempre visible en la parte inferior
- Muestra origen y destino actuales
- Botones para seleccionar ubicaciones
- Botón de búsqueda (habilitado condicionalmente)

### Modal de Resultados
- Bottom sheet modal que se abre al buscar
- Muestra información de la búsqueda
- Lista scrolleable de resultados
- Acciones: Seleccionar todas, Ver en mapa

### FAB Principal
- Esquina inferior derecha
- Cambia de texto según el estado:
  - "Seleccionar Ciudad" (inicial)
  - "Seleccionar Rutas" (después de elegir ciudad)

## 🧮 Algoritmo de Búsqueda

### Radio de Búsqueda
- Por defecto: 500 metros
- Configurable en `viewModel.searchRoutes(radiusMeters)`

### Cálculo de Distancia
- Usa la fórmula de Haversine
- Calcula distancia en metros entre dos coordenadas
- Considera la curvatura de la Tierra

### Lógica de Búsqueda
```kotlin
// Una ruta se considera válida si:
1. Pasa cerca del origen (dentro del radio)
2. Y pasa cerca del destino (dentro del radio)
```

### Optimización
- Búsqueda asíncrona con coroutines
- No bloquea la UI
- Muestra loading indicator durante la búsqueda

## 📱 Estados de la UI

### Botón "Buscar Rutas"
- ❌ Deshabilitado: Falta ciudad, origen o destino
- ✅ Habilitado: Todo listo para buscar

### Modal de Resultados
- 📋 Con resultados: Muestra lista y botones de acción
- 🔍 Sin resultados: Muestra mensaje informativo

### Bottom Sheet
- 📍 Peek height: 120dp (siempre visible)
- 📏 Expandible: Arrastra hacia arriba para ver más
- 🔒 No ocultable: Siempre accesible

## 🎯 Casos de Uso

### Caso 1: Usuario Nuevo
1. Abre la app
2. Ve el mapa centrado en Tuxtla Gutiérrez
3. Toca FAB para seleccionar ciudad
4. Sigue el flujo de selección

### Caso 2: Búsqueda Rápida
1. Ya tiene ciudad seleccionada
2. Toca "Ubicar" en origen
3. Toca punto en mapa
4. Toca "Ubicar" en destino
5. Toca otro punto
6. Toca "Buscar Rutas"
7. Ve resultados inmediatamente

### Caso 3: Sin Resultados
1. Selecciona puntos muy alejados de rutas
2. Busca rutas
3. Ve mensaje: "No se encontraron rutas"
4. Sugerencia: "Intenta seleccionar puntos más cercanos"

## 🔄 Flujo de Datos

```
Usuario → MainActivity → ViewModel → Repository → Model
                ↓
         Observers ← LiveData ← ViewModel
                ↓
            UI Update
```

## 🎨 Material Design 3

### Colores
- Primary: #1976D2 (Azul)
- Accent: #FF6B00 (Naranja)
- Success: #4CAF50 (Verde - origen)
- Error: #F44336 (Rojo - destino)

### Componentes
- MaterialCardView con elevación y bordes redondeados
- MaterialButton con estilos Filled, Outlined, Text
- BottomSheetDialog con handle visual
- MaterialCheckBox con color primary
- Chip para acciones rápidas

## 🚀 Próximas Mejoras Sugeridas

1. 🎯 Ajustar radio de búsqueda dinámicamente
2. 📊 Mostrar distancia estimada del viaje
3. ⏱️ Calcular tiempo estimado de viaje
4. 💰 Mostrar costo estimado
5. 🗺️ Dibujar ruta óptima entre origen y destino
6. 📍 Guardar búsquedas recientes
7. ⭐ Marcar rutas como favoritas
8. 🔔 Notificaciones de proximidad a paradas

---

**Última actualización:** 2026-03-05
**Versión:** 1.0.0
