import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Sisteme distribuite - Laborator 2
 * Server UDP de tip "ping": trimite înapoi fiecare datagramă primită.
 * Poate simula o rețea nesigură: pierde o parte din datagrame și le întârzie aleator pe celelalte,
 * astfel încât răspunsurile pot ajunge în altă ordine decât cererile.
 *
 * Utilizare:  java UdpPingServer.java <port> [--loss 0.2] [--delay ms]
 *   --loss   probabilitatea ca o datagramă să fie ignorată (0.0 - 1.0), implicit 0
 *   --delay  întârzierea medie a răspunsului în ms (aleator între 0 și 2*delay), implicit 0
 */
public class UdpPingServer {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        double loss = 0;
        long delay = 0;
        for (int i = 1; i + 1 < args.length; i += 2) {
            switch (args[i]) {
                case "--loss" -> loss = Double.parseDouble(args[i + 1]);
                case "--delay" -> delay = Long.parseLong(args[i + 1]);
                default -> throw new IllegalArgumentException("opțiune necunoscută: " + args[i]);
            }
        }

        Random random = new Random();
        ScheduledExecutorService replies = Executors.newScheduledThreadPool(4);
        try (DatagramSocket socket = new DatagramSocket(port)) {
            log("ascult pe portul UDP " + port + " (pierderi " + (int) (loss * 100) + "%, întârziere medie " + delay + " ms)");
            byte[] buffer = new byte[1500];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);                       // blochează până sosește o datagramă
                String text = new String(request.getData(), 0, request.getLength(), StandardCharsets.UTF_8);
                SocketAddress from = request.getSocketAddress();

                if (random.nextDouble() < loss) {
                    log("PIERDUT (simulat)  " + from + " -> " + text);
                    continue;
                }
                long wait = delay > 0 ? (long) (random.nextDouble() * 2 * delay) : 0;
                log("primit " + from + " -> " + text + (wait > 0 ? "  (răspund peste " + wait + " ms)" : ""));

                byte[] data = text.getBytes(StandardCharsets.UTF_8);
                DatagramPacket reply = new DatagramPacket(data, data.length, from);
                replies.schedule(() -> {
                    try {
                        socket.send(reply);
                    } catch (Exception e) {
                        log("eroare la trimitere: " + e.getMessage());
                    }
                }, wait, TimeUnit.MILLISECONDS);
            }
        }
    }

    static void log(String msg) {
        System.out.printf("%s %s%n", LocalTime.now().format(TIME), msg);
    }
}
