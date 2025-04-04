package com.novex.gaiardener.uiScreens.components

import com.novex.gaiardener.R

fun getDrawableResource(imageName: String?): Int {
    return when (imageName?.removeSuffix(".jpg")) { // 🔥 Eliminamos la extensión .jpg
        "cactus" -> R.drawable.cactus
        "aloe_vera" -> R.drawable.aloe_vera
        "bonsai" -> R.drawable.bonsai
        "helecho" -> R.drawable.helecho
        "orquidea" -> R.drawable.orquidea
        else -> R.drawable.default_image
    }
}
