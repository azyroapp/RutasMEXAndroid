# 📋 Plan de Implementación: Onboarding, Settings y Vistas Legales

## 🎯 Objetivo
Implementar las funcionalidades faltantes para paridad 100% con iOS:
1. Onboarding (primera vez)
2. Vista de Configuraciones completa
3. Vistas legales con sistema de caché (Política, Términos, Ayuda, etc.)

---

## 📦 Componentes a Crear

### 1. Onboarding System 🎯

#### OnboardingService.kt
```kotlin
class OnboardingService(context: Context) {
    private val prefs = context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
    private val HAS_SEEN_ONBOARDING_KEY = "hasSeenOnboarding"
    
    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(HAS_SEEN_ONBOARDING_KEY, false)
        set(value) = prefs.edit().putBoolean(HAS_SEEN_ONBOARDING_KEY, value).apply()
    
    fun markOnboardingAsCompleted() {
        hasSeenOnboarding = true
    }
    
    fun resetOnboarding() {
        hasSeenOnboarding = false
    }
}
```

#### OnboardingScreen.kt
- HorizontalPager con 7 páginas
- Páginas:
  1. Bienvenido a RutasMEX
  2. Busca tu Ruta
  3. Planifica tu Viaje
  4. Guarda Favoritos
  5. Permisos de Ubicación
  6. Alertas de Proximidad
  7. ¡Todo Listo!
- Botones: Skip / Siguiente / Comenzar
- Indicadores de página
- Solicitud de permisos en página 5

### 2. Settings Screen ⚙️

#### SettingsScreen.kt (Modal)
Secciones:
- **General**
  - Ver Onboarding
  - Limpiar Caché
  - Reiniciar App
  
- **Mapa**
  - Tipo de Mapa (Normal/Satélite)
  - Modo de Cálculo (Ida/Regreso/Completo)
  
- **Notificaciones**
  - Alertas de Proximidad
  - Sonido
  - Vibración
  
- **Ayuda**
  - Tutorial
  - Ayuda
  - Soporte
  
- **Legal**
  - Política de Privacidad
  - Términos de Servicio
  - EULA
  - Política de Reembolso
  
- **Acerca de**
  - Versión de la App
  - Desarrollador

### 3. Legal Document Cache System 📄

#### LegalDocumentCacheService.kt
```kotlin
class LegalDocumentCacheService(private val context: Context) {
    
    enum class LegalDocument(val fileName: String, val remoteUrl: String) {
        PRIVACY_POLICY("privacy-policy.html", "https://azyroapp.com/rutasmex/privacy-policy.html"),
        TERMS_OF_SERVICE("terms-of-service.html", "https://azyroapp.com/rutasmex/terms-of-service.html"),
        EULA("eula.html", "https://azyroapp.com/rutasmex/eula.html"),
        REFUND_POLICY("refund-policy.html", "https://azyroapp.com/rutasmex/refund-policy.html"),
        SUPPORT("support.html", "https://azyroapp.com/rutasmex/support.html"),
        TUTORIAL("tutorial.html", "https://azyroapp.com/rutasmex/tutorial.html")
    }
    
    data class DocumentVersions(
        val privacyPolicy: String,
        val termsOfService: String,
        val eula: String,
        val refundPolicy: String,
        val support: String,
        val tutorial: String
    )
    
    // Funciones principales:
    // - getDocument(document): Result<Pair<String, Boolean>> // (html, isFromCache)
    // - preloadAllDocuments()
    // - fetchRemoteVersions(): DocumentVersions?
    // - hasCachedVersion(document): Boolean
    // - clearCache()
}
```

#### WebViewScreen.kt (con caché)
- Muestra HTML con WebView
- Intenta cargar desde internet
- Si falla, usa versión cacheada
- Badge "Offline" cuando usa caché
- Loading indicator
- Error view con retry

### 4. Help/Tutorial Views 📚

#### HelpScreen.kt
- Lista de secciones con ayuda
- Primeros Pasos
- Funcionalidades Principales
- Consejos y Trucos
- Preguntas Frecuentes

---

## 🔄 Flujo de Implementación

### Fase 1: Onboarding (Prioridad Alta)
1. ✅ Crear OnboardingService
2. ✅ Crear OnboardingScreen con HorizontalPager
3. ✅ Integrar en MainActivity/ContentView
4. ✅ Solicitar permisos en página correspondiente

### Fase 2: Settings (Prioridad Alta)
1. ✅ Crear SettingsModal
2. ✅ Integrar con HomeScreen
3. ✅ Conectar con PreferencesManager
4. ✅ Agregar acciones (limpiar caché, reiniciar, etc.)

### Fase 3: Legal Documents Cache (Prioridad Media)
1. ✅ Crear LegalDocumentCacheService
2. ✅ Implementar sistema de versionado
3. ✅ Crear WebViewScreen con caché
4. ✅ Integrar con Settings

### Fase 4: Help/Tutorial (Prioridad Media)
1. ✅ Crear HelpScreen
2. ✅ Agregar contenido de ayuda
3. ✅ Integrar con Settings

---

## 📁 Estructura de Archivos

```
app/src/main/java/com/azyroapp/rutasmex/
├── core/
│   └── services/
│       ├── OnboardingService.kt
│       └── LegalDocumentCacheService.kt
├── ui/
│   ├── screens/
│   │   ├── OnboardingScreen.kt
│   │   ├── SettingsScreen.kt (Modal)
│   │   ├── HelpScreen.kt
│   │   └── WebViewScreen.kt
│   └── components/
│       ├── OnboardingPage.kt
│       └── SettingsSection.kt
└── data/
    └── model/
        └── OnboardingPage.kt
```

---

## 🎨 Diseño UI/UX

### Onboarding
- Material 3 design
- HorizontalPager con animaciones
- Ilustraciones/iconos grandes
- Texto claro y conciso
- Botones prominentes

### Settings
- ModalBottomSheet
- Secciones con dividers
- Icons para cada opción
- Switches para toggles
- NavigationLinks para sub-pantallas

### Legal Documents
- WebView fullscreen
- Loading skeleton
- Offline badge (auto-hide 3s)
- Error state con retry
- Pull-to-refresh

---

## ✅ Checklist de Paridad iOS

- [ ] Onboarding en primera apertura
- [ ] 7 páginas de onboarding
- [ ] Solicitud de permisos en onboarding
- [ ] Settings como modal
- [ ] Todas las opciones de settings
- [ ] Sistema de caché para documentos legales
- [ ] Versionado de documentos
- [ ] Offline badge en WebView
- [ ] Help/Tutorial screen
- [ ] Limpiar caché desde settings
- [ ] Ver onboarding desde settings
- [ ] Reiniciar app desde settings

---

## 🚀 Próximos Pasos

1. Empezar con OnboardingService y OnboardingScreen
2. Integrar en MainActivity
3. Crear SettingsModal
4. Implementar LegalDocumentCacheService
5. Crear WebViewScreen con caché
6. Agregar HelpScreen
7. Testing completo
8. Build y verificación

---

**Fecha de creación**: Marzo 6, 2026
**Estado**: Planificación completa ✅
