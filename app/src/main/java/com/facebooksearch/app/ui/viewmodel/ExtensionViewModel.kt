package com.facebooksearch.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebooksearch.app.data.model.*
import com.facebooksearch.app.data.repository.ExtensionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExtensionViewModel(
    private val repository: ExtensionRepository
) : ViewModel() {

    private val _selectedExtension = MutableStateFlow<Extension?>(null)
    val selectedExtension: StateFlow<Extension?> = _selectedExtension.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ExtensionCategory?>(null)
    val selectedCategory: StateFlow<ExtensionCategory?> = _selectedCategory.asStateFlow()

    val allExtensions: Flow<List<Extension>> = repository.getAllExtensions()

    val installedExtensions: Flow<List<Extension>> = repository.getInstalledExtensions()

    val enabledExtensions: Flow<List<Extension>> = repository.getEnabledExtensions()

    val filteredExtensions: Flow<List<Extension>> = combine(
        _searchQuery,
        _selectedCategory,
        repository.getAllExtensions()
    ) { query, category, extensions ->
        var result = extensions

        if (query.isNotBlank()) {
            result = result.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }

        category?.let { cat ->
            result = result.filter { it.category == cat }
        }

        result
    }

    init {
        // Initialize with default extensions
        viewModelScope.launch {
            val defaults = repository.getDefaultExtensions()
            repository.insertAll(defaults)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ExtensionCategory?) {
        _selectedCategory.value = category
    }

    fun selectExtension(extension: Extension) {
        _selectedExtension.value = extension
    }

    fun clearSelectedExtension() {
        _selectedExtension.value = null
    }

    fun installExtension(extension: Extension) {
        viewModelScope.launch {
            repository.install(extension)
        }
    }

    fun uninstallExtension(extensionId: String) {
        viewModelScope.launch {
            repository.uninstall(extensionId)
        }
    }

    fun enableExtension(extensionId: String) {
        viewModelScope.launch {
            repository.enable(extensionId)
        }
    }

    fun disableExtension(extensionId: String) {
        viewModelScope.launch {
            repository.disable(extensionId)
        }
    }

    fun getExtensionsByCategory(category: ExtensionCategory): Flow<List<Extension>> =
        repository.getByCategory(category)
}
