import org.apache.commons.net.ntp.TimeStamp
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

fun main(args: Array<String>) {
    val serverName = if (args.size == 1) args[0] else return

    // Send request
    val socket = DatagramSocket()
    val address = InetAddress.getByName(serverName)
    val buf = Message().toByteArray()
    var packet = DatagramPacket(buf, buf.size, address, 123)

    socket.send(packet)

    // Get response
    println("NTP request sent, waiting for response...")
    packet = DatagramPacket(buf, buf.size)
    socket.receive(packet)

    // Immediately record the incoming timestamp
    val destinationTimestamp = TimeStamp(Date())

    // Process response
    val msg = Message(packet.data)
    val roundTripDelay = (destinationTimestamp.time - msg.originateTimestamp.time) - (msg.receiveTimestamp.time - msg.transmitTimestamp.time)
    val localClockOffset = ((msg.receiveTimestamp.time - msg.originateTimestamp.time) + (msg.transmitTimestamp.time - destinationTimestamp.time)) / 2

    // Display response
    println("----------------------------------------------")
    println("NTP server: $serverName")
    println(msg.toString())
    println("Destination timestamp: ${dateFormat.format(destinationTimestamp.date)}")
    println("Round-trip delay:      ${fixedPointFormat.format(roundTripDelay)} ms")
    println("Local clock offset:    ${fixedPointFormat.format(localClockOffset)}  ms")
    println("Accurate local time:   ${dateFormat.format(Date(destinationTimestamp.date.time + localClockOffset))}")
    println("----------------------------------------------")
    socket.close()
}