# 🗺️ Configuración de Google Maps API

## 📋 Pasos para Obtener la API Key

### 1. Ir a Google Cloud Console
Visita: https://console.cloud.google.com/

### 2. Crear un Proyecto (si no tienes uno)
1. Haz clic en el selector de proyectos en la parte superior
2. Clic en "Nuevo Proyecto"
3. Nombre: "RutasMEX"
4. Clic en "Crear"

### 3. Habilitar Google Maps SDK para Android
1. En el menú lateral, ve a "APIs y servicios" → "Biblioteca"
2. Busca "Maps SDK for Android"
3. Haz clic en el resultado
4. Clic en "Habilitar"

### 4. Crear Credenciales (API Key)
1. Ve a "APIs y servicios" → "Credenciales"
2. Clic en "Crear credenciales" → "Clave de API"
3. Se creará una API Key
4. **IMPORTANTE:** Copia la API Key

### 5. Restringir la API Key (Recomendado)
1. Haz clic en la API Key recién creada
2. En "Restricciones de aplicación":
   - Selecciona "Aplicaciones de Android"
   - Clic en "Agregar nombre de paquete y huella digital"
   - Nombre del paquete: `com.azyroapp.rutasmex`
   - Huella digital SHA-1: (obtener con el comando de abajo)

### 6. Obtener SHA-1 Fingerprint

**Para Debug:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Para Release:**
```bash
keytool -list -v -keystore /path/to/your/keystore.jks -alias your_alias_name
```

### 7. Agregar la API Key al Proyecto

Abre `app/src/main/AndroidManifest.xml` y reemplaza `YOUR_API_KEY_HERE` con tu API Key:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

## 🔒 Seguridad

**IMPORTANTE:** 
- ⚠️ NO subas tu API Key a GitHub
- ⚠️ Agrega restricciones a tu API Key
- ⚠️ Usa variables de entorno para producción

### Opción Segura: Usar local.properties

1. Abre `local.properties` (este archivo NO se sube a Git)
2. Agrega tu API Key:
```properties
MAPS_API_KEY=TU_API_KEY_AQUI
```

3. Modifica `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        // ...
        
        // Leer API Key desde local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY", "")
    }
}
```

4. Modifica `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

## ✅ Verificar que Funciona

1. Ejecuta la app
2. Deberías ver el mapa de Google Maps
3. Si ves un mapa gris, revisa:
   - La API Key está correcta
   - Maps SDK está habilitado
   - SHA-1 fingerprint está agregado
   - Permisos de ubicación están en el Manifest

## 📚 Documentación Oficial

- [Google Maps Platform](https://developers.google.com/maps/documentation/android-sdk/overview)
- [Obtener API Key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
- [Restringir API Key](https://cloud.google.com/docs/authentication/api-keys#api_key_restrictions)

---

**¡Listo! 🎉 Ahora puedes usar Google Maps en tu app.**
