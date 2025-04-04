package com.novex.gaiardener.data.repository

import com.novex.gaiardener.data.dao.PlantDao
import com.novex.gaiardener.data.entities.Plant
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {

    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    suspend fun insertPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }

    suspend fun getPlantById(id: Int): Plant? {
        return plantDao.getPlantById(id)
    }

    suspend fun deletePlant(id: Int) {
        val plant = plantDao.getPlantById(id)
        if (plant != null) {
            plantDao.deletePlant(plant)
        }
    }
}
