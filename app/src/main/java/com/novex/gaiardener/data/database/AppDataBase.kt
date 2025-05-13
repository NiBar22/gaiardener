package com.novex.gaiardener.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.novex.gaiardener.data.database.Converters
import com.novex.gaiardener.data.dao.PlantDao
import com.novex.gaiardener.data.entities.Plant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Plant::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plant_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                scope.launch {
                                    DatabaseSeeder.populateDatabase(database.plantDao())
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            INSTANCE?.let { database ->
                                scope.launch {
                                    // 🔁 FORZAR SIEMPRE REPOBLACIÓN DURANTE DESARROLLO
                                    DatabaseSeeder.populateDatabase(database.plantDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
