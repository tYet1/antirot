package com.antirot.app.ui.theme

enum class ThemePreset(
    val id: String,
    val displayName: String,
    val description: String,
) {
    PINK_LIGHT_BLUE(
        id = "pink_light_blue",
        displayName = "Pink + Light Blue",
        description = "Soft pink surfaces with light-blue accents; calm, friendly, airy",
    ),
    BLACK_YELLOW_RED(
        id = "black_yellow_red",
        displayName = "Black + Yellow + Red",
        description = "Dark/black surfaces, yellow highlights, red for limit warnings",
    ),
    GREEN_WHITE(
        id = "green_white",
        displayName = "Green + White",
        description = "Clean white surfaces with crisp green accents",
    ),
    ;

    companion object {
        val DEFAULT = PINK_LIGHT_BLUE

        fun fromId(id: String?): ThemePreset = entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}
