import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Sisteme distribuite - Laborator 2  (SCHELET - de completat)
 * Membru al unui grup multicast. Fiecare nod:
 *   - se alătură grupului (adresă de clasă D + port) și primește tot ce se trimite în grup;
 *   - opțional, trimite el însuși N mesaje numerotate: "id;nr;text";
 *   - urmărește, pentru fiecare expeditor, numerele primite și raportează lipsurile,
 *     mesajele sosite în altă ordine și duplicatele.
 * La oprire (Ctrl+C) afișează un rezumat pe expeditori.
 *
 * Completați cele 4 locuri marcate cu TODO. Argumentele, rezumatul și logarea sunt deja scrise.
 *
 * Utilizare:
 *   java MulticastNode.java <id> [--group 230.0.0.1] [--port 4446] [--count 0] [--interval 500]
 *                                [--ttl 1] [--if en0] [--rcvbuf octeți]
 */
public class MulticastNode {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /** Ce știm despre un expeditor: următorul număr așteptat și numerele încă lipsă. */
    static class SenderState {
        int expected = 1;
        int received = 0;
        int reordered = 0;
        int duplicates = 0;
        final Set<Integer> missing = new TreeSet<>();
    }

    private final String id;
    private final Map<String, SenderState> senders = new TreeMap<>();

    MulticastNode(String id) {
        this.id = id;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("utilizare: java MulticastNode.java <id> [--group 230.0.0.1] [--port 4446]"
                    + " [--count 0] [--interval 500] [--ttl 1] [--if en0] [--rcvbuf octeți]");
            System.exit(1);
        }
        String group = "230.0.0.1", ifName = null;
        int port = 4446, count = 0, ttl = 1, rcvbuf = 0;
        long interval = 500;
        for (int i = 1; i + 1 < args.length; i += 2) {
            switch (args[i]) {
                case "--group" -> group = args[i + 1];
                case "--port" -> port = Integer.parseInt(args[i + 1]);
                case "--count" -> count = Integer.parseInt(args[i + 1]);
                case "--interval" -> interval = Long.parseLong(args[i + 1]);
                case "--ttl" -> ttl = Integer.parseInt(args[i + 1]);
                case "--if" -> ifName = args[i + 1];
                case "--rcvbuf" -> rcvbuf = Integer.parseInt(args[i + 1]);
                default -> throw new IllegalArgumentException("opțiune necunoscută: " + args[i]);
            }
        }
        new MulticastNode(args[0]).run(group, port, count, interval, ttl, ifName, rcvbuf);
    }

    void run(String group, int port, int count, long interval, int ttl, String ifName, int rcvbuf) throws IOException {
        InetSocketAddress groupAddr = new InetSocketAddress(InetAddress.getByName(group), port);
        NetworkInterface netIf = ifName == null ? null : NetworkInterface.getByName(ifName);

        MulticastSocket socket = new MulticastSocket(port);   // ascultă pe portul grupului
        if (rcvbuf > 0) socket.setReceiveBufferSize(rcvbuf);  // buffer mic = pierderi la rafale
        if (netIf != null) socket.setNetworkInterface(netIf);

        // TODO 1 - intrarea în grup:
        //   setați TTL-ul datagramelor trimise (socket.setTimeToLive) și alăturați socket-ul
        //   grupului cu socket.joinGroup(groupAddr, netIf). Fără joinGroup, nodul nu primește nimic.
        log("configurat pentru grupul " + group + ":" + port + " (ttl " + ttl
                + (netIf != null ? ", interfața " + netIf.getName() : "") + ")");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                socket.leaveGroup(groupAddr, netIf);
            } catch (IOException ignored) {
            }
            printSummary();
        }));

        if (count > 0) {
            Thread sender = new Thread(() -> sendLoop(socket, groupAddr, count, interval), "trimitere");
            sender.start();
        }
        receiveLoop(socket);
    }

    /** Trimite `count` mesaje numerotate către grup. */
    private void sendLoop(MulticastSocket socket, InetSocketAddress groupAddr, int count, long interval) {
        // TODO 2 - trimiterea:
        //   pentru seq = 1..count, construiți textul "id;seq;salut de la id", transformați-l în octeți
        //   (UTF-8) și trimiteți-l ca DatagramPacket adresat lui groupAddr. Așteptați `interval` ms
        //   între mesaje (0 = rafală, fără pauză). Tratați excepțiile și logați la final.
        log("TODO 2: trimiterea nu este implementată");
    }

    /** Primește mesajele grupului, la nesfârșit. */
    private void receiveLoop(MulticastSocket socket) throws IOException {
        // TODO 3 - recepția:
        //   în buclă infinită, primiți o datagramă (socket.receive) într-un buffer de 1500 de octeți,
        //   transformați în text DOAR primii packet.getLength() octeți, despărțiți textul în
        //   "id;seq;text" și apelați onMessage(...). Ignorați (cu un mesaj) liniile cu alt format.
        log("TODO 3: recepția nu este implementată");
    }

    /** Compară numărul primit cu cel așteptat de la același expeditor. */
    private synchronized void onMessage(String from, int seq, String body, String address) {
        SenderState s = senders.computeIfAbsent(from, k -> new SenderState());
        String self = from.equals(id) ? " (propriu)" : "";

        // TODO 4 - lipsuri, ordine, duplicate. Comparați seq cu s.expected:
        //   a) seq == expected: mesaj în ordine -> received++, expected++;
        //   b) seq >  expected: numerele expected..seq-1 lipsesc -> adăugați-le în s.missing,
        //      afișați "LIPSĂ", apoi received++ și expected = seq + 1;
        //   c) seq <  expected și seq se află în s.missing: a sosit târziu -> scoateți-l din
        //      missing, received++, reordered++;
        //   d) altfel: duplicat -> duplicates++.
        //   Afișați fiecare caz cu log(...), marcând mesajele proprii cu variabila `self`.
        log("TODO 4: " + from + " #" + seq + self + " primit, dar neanalizat");
    }

    private synchronized void printSummary() {
        System.out.println("---------------- rezumat " + id + " ----------------");
        senders.forEach((from, s) -> System.out.printf(
                "%-6s primite %d, lipsă %d %s, sosite în altă ordine %d, duplicate %d%n",
                from, s.received, s.missing.size(), shortList(s.missing), s.reordered, s.duplicates));
    }

    /** Primele 10 numere lipsă, ca rezumatul să rămână lizibil. */
    private static String shortList(Set<Integer> missing) {
        if (missing.isEmpty()) return "";
        String first = missing.stream().limit(10).map(String::valueOf).reduce((a, b) -> a + ", " + b).orElse("");
        return "[" + first + (missing.size() > 10 ? ", ..." : "") + "]";
    }

    private void log(String msg) {
        System.out.printf("%s [%s] %s%n", LocalTime.now().format(TIME), id, msg);
    }
}
