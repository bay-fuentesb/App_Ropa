package cl.duoc.myapplication.ui.utils
import android.graphics.Color

object ColorUtils {

    // Convierte HSV (Matiz, Saturación, Valor) a un Int de Android Color
    fun hsvToColorInt(h: Float, s: Float, v: Float): Int {
        val hsv = floatArrayOf(h, s, v)
        return Color.HSVToColor(hsv)
    }

    // Convierte un Int de Color a un String Hexadecimal (#RRGGBB)
    fun intToHexString(colorInt: Int): String {
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }
}