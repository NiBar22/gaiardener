package com.novex.gaiardener.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novex.gaiardener.data.entities.Plant
import com.novex.gaiardener.data.repository.PlantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {

    // Estado de la lista de plantas (flujo que se mantiene activo)
    val plants: StateFlow<List<Plant>> = repository.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Estado de la planta seleccionada
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant

    /**
     * Obtiene una planta específica por su ID.
     */
    fun getPlantById(plantId: Int) {
        viewModelScope.launch {
            val plant = repository.getPlantById(plantId)
            _selectedPlant.value = plant
        }
    }
}
