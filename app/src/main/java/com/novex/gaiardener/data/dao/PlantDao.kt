package com.novex.gaiardener.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.novex.gaiardener.data.entities.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant)

    @Query("SELECT * FROM plant")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plant WHERE plantId = :id")
    suspend fun getPlantById(id: Int): Plant?

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Query("DELETE FROM plant")
    suspend fun deleteAllPlants()

    // 🔥 método para contar cuántas plantas hay en la base de datos
    @Query("SELECT COUNT(*) FROM plant")
    suspend fun getPlantCount(): Int

    // 🔥 método para insertar una lista de plantas de una sola vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)
}
