package com.azyroapp.rutasmex.core.services

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para cachear documentos legales localmente con sistema de versionado
 * Permite acceso offline y actualización inteligente basada en versiones
 * Paridad 100% con iOS LegalDocumentCacheService
 */
@Singleton
class LegalDocumentCacheService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cacheDirectory: File = File(context.cacheDir, "LegalDocuments").apply {
        if (!exists()) mkdirs()
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    companion object {
        private const val TAG = "LegalDocumentCache"
        private const val VERSIONS_URL = "https://azyroapp.com/rutasmex/versions.json"
        private const val VERSIONS_FILE_NAME = "versions.json"
        private const val TIMEOUT_MS = 10000
    }
    
    /**
     * Estructura para versiones remotas
     */
    @Serializable
    private data class DocumentVersions(
        @SerialName("privacy-policy") val privacyPolicy: String,
        @SerialName("terms-of-service") val termsOfService: String,
        @SerialName("tutorial") val tutorial: String,
        @SerialName("eula") val eula: String,
        @SerialName("refund-policy") val refundPolicy: String,
        @SerialName("support") val support: String
    ) {
        fun version(document: LegalDocument): String = when (document) {
            LegalDocument.PRIVACY_POLICY -> privacyPolicy
            LegalDocument.TERMS_OF_SERVICE -> termsOfService
            LegalDocument.TUTORIAL -> tutorial
            LegalDocument.EULA -> eula
            LegalDocument.REFUND_POLICY -> refundPolicy
            LegalDocument.SUPPORT -> support
        }
    }
    
    /**
     * Tipos de documentos legales disponibles
     */
    enum class LegalDocument(val fileName: String, val remoteUrl: String) {
        PRIVACY_POLICY(
            "privacy-policy.html",
            "https://azyroapp.com/rutasmex/privacy-policy.html"
        ),
        TERMS_OF_SERVICE(
            "terms-of-service.html",
            "https://azyroapp.com/rutasmex/terms-of-service.html"
        ),
        TUTORIAL(
            "tutorial.html",
            "https://azyroapp.com/rutasmex/tutorial.html"
        ),
        EULA(
            "eula.html",
            "https://azyroapp.com/rutasmex/eula.html"
        ),
        REFUND_POLICY(
            "refund-policy.html",
            "https://azyroapp.com/rutasmex/refund-policy.html"
        ),
        SUPPORT(
            "support.html",
            "https://azyroapp.com/rutasmex/support.html"
        );
        
        val versionFileName: String get() = "$fileName.version"
    }
    
    /**
     * Resultado de obtener un documento
     */
    data class DocumentResult(
        val html: String,
        val isFromCache: Boolean
    )
    
    /**
     * Obtiene el contenido de un documento legal (online o caché)
     * Usa sistema de versionado inteligente para evitar descargas innecesarias
     */
    suspend fun getDocument(document: LegalDocument): Result<DocumentResult> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📖 Solicitando documento: ${document.fileName}")
            
            // 1. Verificar si tenemos caché válido
            val localVersion = getCachedVersion(document)
            val hasHTMLCache = hasCachedVersion(document)
            
            Log.d(TAG, "🔍 Versión local: ${localVersion ?: "ninguna"}, HTML en caché: $hasHTMLCache")
            
            if (localVersion != null && hasHTMLCache) {
                // 2. Tenemos caché, verificar si está actualizado
                Log.d(TAG, "🌐 Consultando versiones remotas...")
                
                val remoteVersions = fetchRemoteVersions()
                if (remoteVersions != null) {
                    val remoteVersion = remoteVersions.version(document)
                    Log.d(TAG, "🔍 Versión remota: $remoteVersion")
                    
                    if (localVersion == remoteVersion) {
                        // ✅ Caché actualizado, usarlo directamente
                        Log.d(TAG, "✅ Usando caché v$localVersion para ${document.fileName}")
                        val cachedHTML = loadFromCache(document)
                        if (cachedHTML != null) {
                            return@withContext Result.success(DocumentResult(cachedHTML, true))
                        }
                    } else {
                        // 🆕 Versión nueva disponible, descargar
                        Log.d(TAG, "🆕 Nueva versión disponible: v$localVersion → v$remoteVersion")
                    }
                } else {
                    // ⚠️ No se pudo verificar versión remota, usar caché existente
                    Log.w(TAG, "⚠️ Sin conexión, usando caché v$localVersion")
                    val cachedHTML = loadFromCache(document)
                    if (cachedHTML != null) {
                        return@withContext Result.success(DocumentResult(cachedHTML, true))
                    }
                }
            }
            
            // 3. No hay caché válido o hay versión nueva, descargar
            Log.d(TAG, "📥 Descargando HTML...")
            val html = downloadDocument(document)
            
            // Guardar en caché
            saveToCache(html, document)
            
            // Obtener y guardar versión remota
            val remoteVersions = fetchRemoteVersions()
            if (remoteVersions != null) {
                val remoteVersion = remoteVersions.version(document)
                saveCachedVersion(remoteVersion, document)
                Log.d(TAG, "✅ Guardado v$remoteVersion en caché")
            }
            
            Result.success(DocumentResult(html, false))
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo documento: ${e.message}")
            
            // Intentar cargar desde caché como último recurso
            val cachedHTML = loadFromCache(document)
            if (cachedHTML != null) {
                Log.d(TAG, "✅ Documento cargado desde caché como fallback")
                Result.success(DocumentResult(cachedHTML, true))
            } else {
                Log.e(TAG, "❌ No hay versión en caché disponible")
                Result.failure(e)
            }
        }
    }

    
    /**
     * Descarga y cachea todos los documentos legales en background con versionado inteligente
     * Solo descarga documentos si la versión remota es diferente a la local
     */
    suspend fun preloadAllDocuments() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 Iniciando verificación de versiones...")
            
            // 1. Descargar versiones remotas
            val remoteVersions = fetchRemoteVersions()
            if (remoteVersions == null) {
                Log.w(TAG, "⚠️ No se pudieron obtener versiones remotas, usando caché existente")
                return@withContext
            }
            
            Log.d(TAG, "✅ Versiones remotas obtenidas")
            
            // 2. Comparar y descargar solo documentos con versión diferente
            var updatedCount = 0
            var skippedCount = 0
            
            for (document in LegalDocument.values()) {
                val remoteVersion = remoteVersions.version(document)
                val localVersion = getCachedVersion(document)
                val hasHTMLCache = hasCachedVersion(document)
                
                // ✅ Verificar que la versión coincida Y que el HTML exista
                if (localVersion == remoteVersion && hasHTMLCache) {
                    Log.d(TAG, "⏭️ ${document.fileName}: v$remoteVersion ya en caché")
                    skippedCount++
                } else {
                    if (!hasHTMLCache) {
                        Log.w(TAG, "⚠️ ${document.fileName}: HTML faltante, descargando...")
                    } else {
                        Log.d(TAG, "🆕 ${document.fileName}: v${localVersion ?: "none"} → v$remoteVersion")
                    }
                    downloadAndCacheDocument(document, remoteVersion)
                    updatedCount++
                }
            }
            
            Log.d(TAG, "🎉 Actualización completa: $updatedCount descargados, $skippedCount sin cambios")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en preload: ${e.message}")
        }
    }
    
    /**
     * Verifica si existe una versión en caché de un documento
     */
    fun hasCachedVersion(document: LegalDocument): Boolean {
        val file = File(cacheDirectory, document.fileName)
        return file.exists()
    }
    
    /**
     * Elimina todos los documentos en caché
     */
    fun clearCache() {
        try {
            cacheDirectory.deleteRecursively()
            cacheDirectory.mkdirs()
            Log.d(TAG, "🗑️ Caché limpiado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error limpiando caché: ${e.message}")
        }
    }
    
    // MARK: - Private Methods
    
    /**
     * Descarga las versiones remotas desde el JSON
     */
    private suspend fun fetchRemoteVersions(): DocumentVersions? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🌐 Descargando versions.json...")
            
            val url = URL("$VERSIONS_URL?t=${System.currentTimeMillis()}")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("Cache-Control", "no-cache")
                useCaches = false
            }
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val versions = json.decodeFromString<DocumentVersions>(jsonString)
                
                // Guardar en disco para referencia
                saveVersionsToDisk(versions)
                
                Log.d(TAG, "✅ Versiones descargadas exitosamente")
                versions
            } else {
                Log.e(TAG, "❌ Error HTTP: $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error descargando versiones: ${e.message}")
            null
        }
    }
    
    /**
     * Descarga un documento desde su URL remota
     */
    private suspend fun downloadDocument(document: LegalDocument): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "📥 Descargando: ${document.remoteUrl}")
        
        val url = URL("${document.remoteUrl}?t=${System.currentTimeMillis()}")
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            setRequestProperty("Cache-Control", "no-cache")
            useCaches = false
        }
        
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val html = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "✅ Descarga exitosa: ${html.length} bytes")
            html
        } else {
            throw Exception("HTTP Error: $responseCode")
        }
    }
    
    /**
     * Descarga y cachea un documento con su versión
     */
    private suspend fun downloadAndCacheDocument(document: LegalDocument, version: String) {
        try {
            val html = downloadDocument(document)
            saveToCache(html, document)
            saveCachedVersion(version, document)
            Log.d(TAG, "✅ ${document.fileName} v$version cacheado exitosamente")
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error cacheando ${document.fileName}: ${e.message}")
        }
    }
    
    /**
     * Guarda HTML en caché
     */
    private fun saveToCache(html: String, document: LegalDocument) {
        val file = File(cacheDirectory, document.fileName)
        file.writeText(html)
    }
    
    /**
     * Carga HTML desde caché
     */
    private fun loadFromCache(document: LegalDocument): String? {
        return try {
            val file = File(cacheDirectory, document.fileName)
            if (file.exists()) file.readText() else null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error leyendo caché: ${e.message}")
            null
        }
    }
    
    /**
     * Obtiene la versión en caché de un documento
     */
    private fun getCachedVersion(document: LegalDocument): String? {
        return try {
            val file = File(cacheDirectory, document.versionFileName)
            if (file.exists()) file.readText() else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Guarda la versión de un documento en caché
     */
    private fun saveCachedVersion(version: String, document: LegalDocument) {
        val file = File(cacheDirectory, document.versionFileName)
        file.writeText(version)
    }
    
    /**
     * Guarda versiones en disco (para referencia)
     */
    private fun saveVersionsToDisk(versions: DocumentVersions) {
        try {
            val file = File(cacheDirectory, VERSIONS_FILE_NAME)
            val jsonString = json.encodeToString(DocumentVersions.serializer(), versions)
            file.writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando versiones: ${e.message}")
        }
    }
}
