package com.thinkslynk.fabric.helpers

/**
 * Due to annotations requiring compile time constants, objects like ItemGroup cannot be
 * used directly, so we provide compile time equivalents.
 */
object AnnotationHelpers {
    object ItemGroup {
        const val BUILDING_BLOCKS = "BUILDING_BLOCKS"
        const val DECORATIONS = "DECORATIONS"
        const val REDSTONE = "REDSTONE"
        const val TRANSPORTATION = "TRANSPORTATION"
        const val MISC = "MISC"
        const val SEARCH = "SEARCH"
        const val FOOD = "FOOD"
        const val TOOLS = "TOOLS"
        const val COMBAT = "COMBAT"
        const val BREWING = "BREWING"
        const val MATERIALS = "MATERIALS"
        const val HOTBAR = "HOTBAR"
        const val INVENTORY = "INVENTORY"
    }
}