package com.novex.gaiardener.data.database

import com.novex.gaiardener.data.dao.PlantDao
import com.novex.gaiardener.data.entities.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {
    suspend fun populateDatabase(plantDao: PlantDao) {
        withContext(Dispatchers.IO) { // Ejecutar en un hilo de fondo
            println("⚡ Iniciando llenado de base de datos...")

            if (plantDao.getPlantCount() == 0) { // Evita duplicados
                val samplePlants = listOf(
                    Plant(
                        nombre = "Aloe Vera",
                        nombreCientifico = "Aloe barbadensis miller",
                        datosGenerales = "Planta medicinal con múltiples beneficios.",
                        datosCuriosos = "Se usa en productos para la piel.",
                        rangoTemperatura = "15-30",
                        rangoPh = "6.0-7.5",
                        rangoHumedad = "15-40",
                        rangoLuzSolar = "2000-3000",
                        recomendaciones = "Regar cada 2 semanas.",
                        imagenes = listOf("aloe_vera.jpg")
                    ),
                    Plant(
                        nombre = "Cactus",
                        nombreCientifico = "Cactaceae",
                        datosGenerales = "Planta resistente a la sequía.",
                        datosCuriosos = "Almacena agua en su tallo.",
                        rangoTemperatura = "20-40",
                        rangoPh = "5.5-7.0",
                        rangoHumedad = "10-30",
                        rangoLuzSolar = "2500-3500",
                        recomendaciones = "Regar una vez al mes.",
                        imagenes = listOf("cactus.jpg")
                    ),
                    Plant(
                        nombre = "Lavanda",
                        nombreCientifico = "Lavandula angustifolia",
                        datosGenerales = "Planta aromática usada en aceites esenciales.",
                        datosCuriosos = "Atrae polinizadores como abejas y mariposas.",
                        rangoTemperatura = "15-30",
                        rangoPh = "6.0-8.0",
                        rangoHumedad = "20-50",
                        rangoLuzSolar = "2000-3000",
                        recomendaciones = "Regar cuando el suelo esté seco.",
                        imagenes = listOf("lavanda.jpg")
                    ),
                    Plant(
                        nombre = "Girasol",
                        nombreCientifico = "Helianthus annuus",
                        datosGenerales = "Planta con flores grandes y semillas comestibles.",
                        datosCuriosos = "Sigue el movimiento del sol durante el día.",
                        rangoTemperatura = "18-35",
                        rangoPh = "6.0-7.5",
                        rangoHumedad = "30-60",
                        rangoLuzSolar = "3000-4000",
                        recomendaciones = "Riego moderado, evitar encharcamientos.",
                        imagenes = listOf("girasol.jpg")
                    ),
                    Plant(
                        nombre = "Menta",
                        nombreCientifico = "Mentha spicata",
                        datosGenerales = "Planta aromática utilizada en infusiones y cocina.",
                        datosCuriosos = "Es una de las hierbas más antiguas usadas por el ser humano.",
                        rangoTemperatura = "15-28",
                        rangoPh = "5.5-7.5",
                        rangoHumedad = "40-70",
                        rangoLuzSolar = "1000-2000",
                        recomendaciones = "Mantener la tierra húmeda pero bien drenada.",
                        imagenes = listOf("menta.jpg")
                    ),
                    Plant(
                        nombre = "Tomatera",
                        nombreCientifico = "Solanum lycopersicum",
                        datosGenerales = "Planta frutal que produce tomates comestibles.",
                        datosCuriosos = "El tomate es en realidad una fruta, no un vegetal.",
                        rangoTemperatura = "20-30",
                        rangoPh = "5.5-7.0",
                        rangoHumedad = "30-60",
                        rangoLuzSolar = "2500-3500",
                        recomendaciones = "Regar regularmente pero evitar encharcamiento.",
                        imagenes = listOf("tomatera.jpg")
                    ),
                    Plant(
                        nombre = "Bambú",
                        nombreCientifico = "Bambusoideae",
                        datosGenerales = "Planta de crecimiento rápido y estructura leñosa.",
                        datosCuriosos = "Algunas especies pueden crecer hasta 1 metro por día.",
                        rangoTemperatura = "18-35",
                        rangoPh = "5.0-6.5",
                        rangoHumedad = "50-80",
                        rangoLuzSolar = "1000-2500",
                        recomendaciones = "Mantener el suelo húmedo en todo momento.",
                        imagenes = listOf("bambu.jpg")
                    ),
                    Plant(
                        nombre = "Suculenta",
                        nombreCientifico = "Echeveria sp.",
                        datosGenerales = "Plantas que almacenan agua en sus hojas carnosas.",
                        datosCuriosos = "Existen más de 10,000 especies diferentes.",
                        rangoTemperatura = "15-30",
                        rangoPh = "6.0-7.5",
                        rangoHumedad = "10-30",
                        rangoLuzSolar = "2000-3500",
                        recomendaciones = "Regar solo cuando la tierra esté completamente seca.",
                        imagenes = listOf("suculenta.jpg")
                    ),
                    Plant(
                        nombre = "Romero",
                        nombreCientifico = "Salvia rosmarinus",
                        datosGenerales = "Hierba aromática usada en gastronomía y medicina.",
                        datosCuriosos = "Tiene propiedades antioxidantes y antiinflamatorias.",
                        rangoTemperatura = "10-28",
                        rangoPh = "6.0-7.0",
                        rangoHumedad = "20-50",
                        rangoLuzSolar = "2000-3000",
                        recomendaciones = "Riego moderado, prefiere suelos secos.",
                        imagenes = listOf("romero.jpg")
                    ),
                    Plant(
                        nombre = "Margarita",
                        nombreCientifico = "Bellis perennis",
                        datosGenerales = "Planta ornamental con flores blancas y amarillas.",
                        datosCuriosos = "Puede florecer durante casi todo el año.",
                        rangoTemperatura = "12-25",
                        rangoPh = "5.5-7.0",
                        rangoHumedad = "30-60",
                        rangoLuzSolar = "1500-2500",
                        recomendaciones = "Regar cuando la tierra esté seca al tacto.",
                        imagenes = listOf("margarita.jpg")
                    ),
                    Plant(
                        nombre = "Planta Millonaria",
                        nombreCientifico = "Plectranthus verticillatus",
                        datosGenerales = "También conocida como Swedish Ivy, es ideal para interiores con luz indirecta y requiere un riego moderado.",
                        datosCuriosos = "Se cree que trae buena suerte y prosperidad económica en muchos hogares.",
                        rangoTemperatura = "18-26",
                        rangoPh = "6.0-7.5",
                        rangoHumedad = "30-60",
                        rangoLuzSolar = "200-800",
                        recomendaciones = "Evitar luz solar directa. Mantener la tierra ligeramente húmeda y podar con regularidad.",
                        imagenes = listOf("millonaria.jpg")
                    )


                )


                println("⚡ Insertando nuevas plantas en la base de datos...")
                plantDao.insertAll(samplePlants)
                println("✅ Población de base de datos finalizada.")
            } else {
                println("⚡ La base de datos ya contiene datos, se omite el llenado.")
            }
        }
    }

}
