import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Sisteme distribuite - Laborator 2
 * Client UDP pentru UdpPingServer. Trimite N datagrame numerotate "PING <nr> <timp_ns>",
 * primește răspunsurile pe un fir separat și raportează: RTT, datagramele pierdute
 * și răspunsurile sosite în altă ordine decât au fost trimise.
 *
 * Utilizare:  java UdpPingClient.java <host> <port> [--count 10] [--interval 200] [--timeout 2000]
 */
public class UdpPingClient {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        int count = 10;
        long interval = 200;
        int timeout = 2000;
        for (int i = 2; i + 1 < args.length; i += 2) {
            switch (args[i]) {
                case "--count" -> count = Integer.parseInt(args[i + 1]);
                case "--interval" -> interval = Long.parseLong(args[i + 1]);
                case "--timeout" -> timeout = Integer.parseInt(args[i + 1]);
                default -> throw new IllegalArgumentException("opțiune necunoscută: " + args[i]);
            }
        }

        InetAddress server = InetAddress.getByName(host);
        Map<Integer, Double> rtt = new ConcurrentHashMap<>();   // nr -> RTT în ms
        int[] reordered = {0};
        AtomicBoolean doneSending = new AtomicBoolean(false);

        try (DatagramSocket socket = new DatagramSocket()) {     // port local ales de sistem
            socket.setSoTimeout(timeout);

            // firul de recepție: UDP nu are conexiune, deci citim răspunsurile independent de trimitere
            Thread receiver = new Thread(() -> {
                byte[] buffer = new byte[1500];
                int highest = 0;
                while (true) {
                    try {
                        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                        socket.receive(reply);
                        String[] parts = new String(reply.getData(), 0, reply.getLength(), StandardCharsets.UTF_8).split(" ");
                        int seq = Integer.parseInt(parts[1]);
                        double ms = (System.nanoTime() - Long.parseLong(parts[2])) / 1e6;
                        rtt.put(seq, ms);
                        String note = "";
                        if (seq < highest) {
                            reordered[0]++;
                            note = "   <- sosit după #" + highest + " (ordine schimbată)";
                        }
                        highest = Math.max(highest, seq);
                        System.out.printf("răspuns #%d  RTT %.3f ms%s%n", seq, ms, note);
                    } catch (SocketTimeoutException e) {
                        if (doneSending.get()) return;              // gata: nimic nou în ultimele `timeout` ms
                    } catch (Exception e) {
                        System.out.println("eroare la recepție: " + e.getMessage());
                        return;
                    }
                }
            }, "receptie");
            receiver.start();

            for (int seq = 1; seq <= count; seq++) {
                byte[] data = ("PING " + seq + " " + System.nanoTime()).getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(data, data.length, server, port));
                if (interval > 0) Thread.sleep(interval);
            }
            doneSending.set(true);
            receiver.join();
        }

        TreeSet<Integer> lost = new TreeSet<>();
        for (int seq = 1; seq <= count; seq++) if (!rtt.containsKey(seq)) lost.add(seq);
        System.out.println("----------------------------------------");
        System.out.printf("trimise %d, primite %d, pierdute %d (%.0f%%), sosite în altă ordine %d%n",
                count, rtt.size(), lost.size(), 100.0 * lost.size() / count, reordered[0]);
        if (!lost.isEmpty()) System.out.println("pierdute: " + lost);
        var stats = rtt.values().stream().mapToDouble(Double::doubleValue).summaryStatistics();
        if (stats.getCount() > 0) {
            System.out.printf("RTT min %.3f / mediu %.3f / max %.3f ms%n", stats.getMin(), stats.getAverage(), stats.getMax());
        }
    }
}
