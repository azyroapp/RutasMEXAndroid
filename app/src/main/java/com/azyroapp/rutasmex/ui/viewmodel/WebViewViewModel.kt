package com.azyroapp.rutasmex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azyroapp.rutasmex.core.services.LegalDocumentCacheService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para WebViewScreen
 * Gestiona la carga de documentos legales con caché
 */
@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val legalDocumentService: LegalDocumentCacheService
) : ViewModel() {
    
    data class UiState(
        val isLoading: Boolean = false,
        val html: String? = null,
        val isFromCache: Boolean = false,
        val showOfflineBadge: Boolean = false,
        val error: String? = null
    )
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    /**
     * Carga un documento legal
     */
    fun loadDocument(document: LegalDocumentCacheService.LegalDocument) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            
            val result = legalDocumentService.getDocument(document)
            
            result.fold(
                onSuccess = { documentResult ->
                    _uiState.value = UiState(
                        isLoading = false,
                        html = documentResult.html,
                        isFromCache = documentResult.isFromCache,
                        showOfflineBadge = documentResult.isFromCache
                    )
                    
                    // Auto-ocultar badge después de 3 segundos
                    if (documentResult.isFromCache) {
                        delay(3000)
                        _uiState.value = _uiState.value.copy(showOfflineBadge = false)
                    }
                },
                onFailure = { error ->
                    _uiState.value = UiState(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
            )
        }
    }
}
