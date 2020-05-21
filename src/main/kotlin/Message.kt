import org.apache.commons.net.ntp.TimeStamp
import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

class Message {
    var leapIndicator = 0
    var version = 3
    var mode = 0
    var stratum = 0
    var pollInterval = 0
    var precision = 0
    var rootDelay = 0.0
    var rootDispersion = 0.0
    var referenceIdentifier = byteArrayOf(0, 0, 0, 0)
    val referenceTimestamp: TimeStamp
    val originateTimestamp: TimeStamp
    val receiveTimestamp: TimeStamp
    val transmitTimestamp: TimeStamp

    constructor(array: ByteArray) {
        leapIndicator = (array[0].toInt() shr 6) and 0x3
        version = (array[0].toInt() shr 3) and 0x7
        mode = array[0].toInt() and 0x7
        stratum = array[1].toPositiveInt()
        pollInterval = array[2].toInt()
        precision = array[3].toInt()

        rootDelay = array[4] * 256.0 + array[5].toPositiveInt() + array[6].toPositiveInt() / 256.0 + array[7].toPositiveInt() / 65536.0
        rootDispersion = array[8].toPositiveInt() * 256.0 + array[9].toPositiveInt() + array[10].toPositiveInt() / 256.0 + array[11].toPositiveInt() / 65536.0

        referenceIdentifier[0] = array[12]
        referenceIdentifier[1] = array[13]
        referenceIdentifier[2] = array[14]
        referenceIdentifier[3] = array[15]

        referenceTimestamp = decode(array, 16)
        originateTimestamp = decode(array, 24)
        receiveTimestamp = decode(array, 32)
        transmitTimestamp = decode(array, 40)
    }

    constructor() {
        mode = 3
        transmitTimestamp = TimeStamp(Date())
        referenceTimestamp = TimeStamp(0)
        originateTimestamp = TimeStamp(0)
        receiveTimestamp = TimeStamp(0)
    }

    fun toByteArray(): ByteArray {
        val p = ByteArray(48)
        p[0] = (leapIndicator shl 6 or (version shl 3) or mode).toByte()
        p[1] = stratum.toByte()
        p[2] = pollInterval.toByte()
        p[3] = precision.toByte()

        val l = (rootDelay * 65536.0).toInt()
        p[4] = (l shr 24 and 0xFF).toByte()
        p[5] = (l shr 16 and 0xFF).toByte()
        p[6] = (l shr 8 and 0xFF).toByte()
        p[7] = (l and 0xFF).toByte()

        val ul = (rootDispersion * 65536.0).toLong()
        p[8] = (ul shr 24 and 0xFF).toByte()
        p[9] = (ul shr 16 and 0xFF).toByte()
        p[10] = (ul shr 8 and 0xFF).toByte()
        p[11] = (ul and 0xFF).toByte()

        p[12] = referenceIdentifier[0]
        p[13] = referenceIdentifier[1]
        p[14] = referenceIdentifier[2]
        p[15] = referenceIdentifier[3]

        encode(p, 16, referenceTimestamp)
        encode(p, 24, originateTimestamp)
        encode(p, 32, receiveTimestamp)
        encode(p, 40, transmitTimestamp)

        return p
    }

    override fun toString(): String {
        val precisionStr = DecimalFormat("0.#E0").format(2.0.pow(precision.toDouble()))
        return "----------------------------------------------\n" +
                "Leap indicator:        $leapIndicator\n" +
                "Version:               $version\n" +
                "Mode:                  $mode\n" +
                "Stratum:               $stratum\n" +
                "Poll:                  $pollInterval\n" +
                "Precision:             $precision ($precisionStr sec)\n" +
                "Root delay:            ${fixedPointFormat.format(rootDelay * 1000)} ms\n" +
                "Root dispersion:       ${fixedPointFormat.format(rootDispersion * 1000)} ms\n" +
                "Reference timestamp:   ${dateFormat.format(referenceTimestamp.date)}\n" +
                "Originate timestamp:   ${dateFormat.format(originateTimestamp.date)}\n" +
                "Receive timestamp:     ${dateFormat.format(receiveTimestamp.date)}\n" +
                "Transmit timestamp:    ${dateFormat.format(transmitTimestamp.date)}\n" +
                "----------------------------------------------"
    }

    companion object {
        private fun decode(array: ByteArray, pointer: Int): TimeStamp {
            var value = 0L
            for (k in 0..7) {
                value = value * 256 + array[pointer + k].toPositiveInt()
            }
            return TimeStamp(value)
        }

        private fun encode(array: ByteArray, pointer: Int, timeStamp: TimeStamp) {
            val value: Long = timeStamp.ntpValue()
            for (k in 0..7) {
                array[pointer + k] = ((value shr (8 * (8 - k - 1))) and 0xFF).toByte()
            }
        }
    }
}

