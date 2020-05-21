import java.text.DecimalFormat
import java.text.SimpleDateFormat

val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SS")

val fixedPointFormat = DecimalFormat("0.000")

fun Byte.toPositiveInt() = toInt() and 0xFF

