package com.novex.gaiardener.uiScreens.components

import com.novex.gaiardener.R

fun getDrawableResource(imageName: String?): Int {
    return when (imageName?.removeSuffix(".jpg")) { // 🔥 Eliminamos la extensión .jpg
        "cactus" -> R.drawable.cactus
        "aloe_vera" -> R.drawable.aloe_vera
        "lavanda" -> R.drawable.lavanda
        "girasol" -> R.drawable.girasol
        "menta" -> R.drawable.menta
        "tomatera" -> R.drawable.tomatera
        "bambu" -> R.drawable.bambu
        "suculenta" -> R.drawable.suculenta
        "romero" -> R.drawable.romero
        "margarita" -> R.drawable.margarita
        "millonaria" -> R.drawable.millonaria
        else -> R.drawable.default_image
    }
}
