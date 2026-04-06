package com.frontend

import android.content.Context
import androidx.core.content.ContextCompat
import com.sudokuanke.R

object ColorPrefs {

    const val PREFS_NAME = "settings"

    // Color keys — match colors.xml names
    const val DIGIT_FIXED = "digit_fixed"
    const val DIGIT_USER = "digit_user"
    const val DIGIT_CONFLICT = "digit_conflict"
    const val CELL_HIGHLIGHT = "cell_highlight"
    const val NUMBER_SELECTED_BACKGROUND = "number_selected_background"
    const val BUTTON_BG = "button_bg"
    const val BUTTON_SHINE_1 = "button_shine_1"
    const val BUTTON_SHINE_2 = "button_shine_2"
    const val BUTTON_SHINE_3 = "button_shine_3"
    const val BUTTON_SHINE_ON = "button_shine_enabled"

    private val colorDefaults = mapOf(
        DIGIT_FIXED                     to R.color.digit_fixed,
        DIGIT_USER                      to R.color.digit_user,
        DIGIT_CONFLICT                  to R.color.digit_conflict,
        CELL_HIGHLIGHT                  to R.color.cell_highlight,
        NUMBER_SELECTED_BACKGROUND      to R.color.number_selected_background,
        BUTTON_BG                       to R.color.button_bg,
        BUTTON_SHINE_1                  to R.color.button_shine_1,
        BUTTON_SHINE_2                  to R.color.button_shine_2,
        BUTTON_SHINE_3                  to R.color.button_shine_3,
    )

    fun getColor(context: Context, key: String): Int {
        val default =
            ContextCompat.getColor(context, colorDefaults[key] ?: return 0xFF000000.toInt())
        return prefs(context).getInt(key, default)
    }

    fun setColor(context: Context, key: String, color: Int) {
        prefs(context).edit().putInt(key, color).apply()
    }

    fun getShineEnabled(context: Context): Boolean = prefs(context).getBoolean(
        BUTTON_SHINE_ON, context.resources.getBoolean(R.bool.button_shine_enabled)
    )

    fun setShineEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(BUTTON_SHINE_ON, enabled).apply()
    }

    fun resetToDefaults(context: Context) {
        prefs(context).edit().clear().apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
