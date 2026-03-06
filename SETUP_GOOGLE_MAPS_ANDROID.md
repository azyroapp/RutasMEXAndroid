# 🗺️ Configuración de Google Maps para Android

## 📋 Pasos para configurar Google Maps API Key

### 1️⃣ Obtener SHA-1 Fingerprint

Ejecuta en terminal:

```bash
# Para debug keystore (desarrollo)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Para release keystore (producción)
keytool -list -v -keystore /path/to/your/keystore.jks -alias your-alias
```

Copia el **SHA-1** que aparece en la salida.

---

### 2️⃣ Configurar en Google Cloud Console

1. **Ve a:** https://console.cloud.google.com/

2. **Crea o selecciona un proyecto**

3. **Habilita las APIs necesarias:**
   - Ve a "APIs & Services" → "Library"
   - Busca y habilita:
     - ✅ Maps SDK for Android
     - ✅ Places API (opcional, para búsqueda)
     - ✅ Directions API (opcional, para rutas)

4. **Crea una API Key:**
   - Ve a "APIs & Services" → "Credentials"
   - Click en "Create Credentials" → "API Key"
   - Copia la API Key generada

5. **Restringe la API Key (Importante para seguridad):**
   - Click en la API Key recién creada
   - En "Application restrictions":
     - Selecciona "Android apps"
     - Click "Add an item"
     - Package name: `com.azyroapp.rutasmex`
     - SHA-1 fingerprint: (pega el que obtuviste en paso 1)
   - En "API restrictions":
     - Selecciona "Restrict key"
     - Marca las APIs que habilitaste
   - Click "Save"

---

### 3️⃣ Agregar API Key al proyecto

La API Key ya está configurada en `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

**⚠️ IMPORTANTE:** Reemplaza `TU_API_KEY_AQUI` con tu API Key real.

---

### 4️⃣ Verificar configuración

1. **Limpia y reconstruye el proyecto:**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

2. **Instala en dispositivo:**
   ```bash
   ./gradlew installDebug
   ```

3. **Verifica en logcat:**
   ```bash
   adb logcat | grep -i "maps\|google"
   ```

   Deberías ver:
   - ✅ "Google Play services maps renderer version"
   - ✅ "Selected MapView map renderer: P"
   - ❌ NO deberías ver: "Waiting for active network connection"

---

## 🐛 Troubleshooting

### Problema: Mapa en blanco

**Causas comunes:**
1. ❌ API Key no configurada o inválida
2. ❌ SHA-1 fingerprint incorrecto
3. ❌ APIs no habilitadas en Google Cloud
4. ❌ Restricciones de API Key incorrectas
5. ❌ Sin conexión a internet

**Soluciones:**
1. Verifica que la API Key esté en `AndroidManifest.xml`
2. Verifica que el SHA-1 coincida con el de tu keystore
3. Habilita "Maps SDK for Android" en Google Cloud
4. Verifica que el package name sea correcto
5. Verifica conexión a internet del dispositivo

### Problema: "Waiting for active network connection"

**Solución:**
- Verifica que el dispositivo tenga internet
- Verifica que la app tenga permiso `INTERNET` (ya configurado)
- Verifica que la API Key sea válida

### Problema: "This app is not authorized to use Google Maps"

**Solución:**
- Verifica que el SHA-1 fingerprint esté agregado en Google Cloud
- Verifica que el package name sea exactamente: `com.azyroapp.rutasmex`
- Espera unos minutos (los cambios pueden tardar en propagarse)

---

## 📱 Testing

### En Emulador:
```bash
# Obtener SHA-1 del emulador
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### En Dispositivo Físico:
- Usa el mismo SHA-1 del debug keystore
- O genera un release keystore y usa su SHA-1

---

## 🔒 Seguridad

**NUNCA subas tu API Key a repositorios públicos!**

### Opción 1: Usar local.properties (Recomendado)

1. Agrega a `local.properties`:
   ```properties
   MAPS_API_KEY=TU_API_KEY_AQUI
   ```

2. Modifica `build.gradle.kts`:
   ```kotlin
   android {
       defaultConfig {
           val properties = Properties()
           properties.load(project.rootProject.file("local.properties").inputStream())
           manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY", "")
       }
   }
   ```

3. Modifica `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```

### Opción 2: Usar variables de entorno

```bash
export MAPS_API_KEY="TU_API_KEY_AQUI"
```

---

## ✅ Checklist

- [ ] Obtener SHA-1 fingerprint
- [ ] Crear proyecto en Google Cloud Console
- [ ] Habilitar Maps SDK for Android
- [ ] Crear API Key
- [ ] Agregar restricciones (package name + SHA-1)
- [ ] Agregar API Key a AndroidManifest.xml
- [ ] Limpiar y reconstruir proyecto
- [ ] Probar en dispositivo/emulador
- [ ] Verificar que el mapa cargue correctamente

---

**Fecha:** Marzo 6, 2026  
**Proyecto:** RutasMEX Android
