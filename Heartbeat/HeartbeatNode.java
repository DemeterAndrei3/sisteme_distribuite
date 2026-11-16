import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Sisteme distribuite - Laborator 3
 * Nod care trimite periodic semnale de activitate (heartbeat) unui monitor, prin UDP.
 * Mesaj: "HB;<id>;<nr>;<timp_ms>". Numărul crește la fiecare heartbeat și pornește de la 1
 * la fiecare (re)pornire a procesului, astfel încât monitorul poate deosebi o repornire de o pauză.
 *
 * Utilizare:  java HeartbeatNode.java <id> <host_monitor> <port_monitor>
 *                  [--interval 1000] [--loss 0.0] [--jitter 0]
 *   --interval  perioada dintre heartbeat-uri, în ms
 *   --loss      probabilitatea ca un heartbeat să nu fie trimis (simulează pierderi în rețea)
 *   --jitter    întârziere aleatoare suplimentară, între 0 și valoarea dată, în ms
 */
public class HeartbeatNode {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("utilizare: java HeartbeatNode.java <id> <host_monitor> <port_monitor>"
                    + " [--interval 1000] [--loss 0.0] [--jitter 0]");
            System.exit(1);
        }
        String id = args[0];
        InetAddress monitor = InetAddress.getByName(args[1]);
        int port = Integer.parseInt(args[2]);
        long interval = 1000, jitter = 0;
        double loss = 0;
        for (int i = 3; i + 1 < args.length; i += 2) {
            switch (args[i]) {
                case "--interval" -> interval = Long.parseLong(args[i + 1]);
                case "--loss" -> loss = Double.parseDouble(args[i + 1]);
                case "--jitter" -> jitter = Long.parseLong(args[i + 1]);
                default -> throw new IllegalArgumentException("opțiune necunoscută: " + args[i]);
            }
        }

        Random random = new Random();
        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.printf("%s [%s] trimit heartbeat la %s:%d la fiecare %d ms (pierderi %.0f%%, jitter %d ms)%n",
                    LocalTime.now().format(TIME), id, monitor.getHostAddress(), port, interval, loss * 100, jitter);
            for (long seq = 1; ; seq++) {
                if (random.nextDouble() >= loss) {
                    byte[] data = ("HB;" + id + ";" + seq + ";" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
                    socket.send(new DatagramPacket(data, data.length, monitor, port));
                }
                long extra = jitter > 0 ? (long) (random.nextDouble() * jitter) : 0;
                Thread.sleep(interval + extra);
            }
        }
    }
}
