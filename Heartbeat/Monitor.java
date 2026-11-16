import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sisteme distribuite - Laborator 3  (SCHELET - de completat)
 * Monitor de heartbeat: primește prin UDP semnalele nodurilor și ține, pentru fiecare nod,
 * momentul ultimului semnal. Un fir de verificare rulează la fiecare 100 ms și schimbă starea:
 *   ACTIV   -> SUSPECT  dacă nu a venit nimic de `suspect` ms
 *   SUSPECT -> CAZUT    dacă nu a venit nimic de `fail` ms
 *   SUSPECT/CAZUT -> ACTIV la primul heartbeat nou
 * La revenire, numărul heartbeat-ului spune ce s-a întâmplat:
 *   numerele continuă   -> nodul nu a căzut, doar a tăcut (pauză, rețea): suspiciune falsă
 *   numerele reîncep de la 1 -> procesul a fost repornit
 *
 * Completați cele 3 locuri marcate cu TODO. Argumentele, tabela și logarea sunt deja scrise.
 *
 * Utilizare:  java Monitor.java <port> [--suspect 2000] [--fail 4000] [--status 0]
 *   --status  la câte ms se afișează tabela nodurilor (0 = doar schimbările de stare)
 */
public class Monitor {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    enum State { ACTIV, SUSPECT, CAZUT }

    /** Ce știe monitorul despre un nod. */
    static class NodeInfo {
        final String id;
        State state = State.ACTIV;
        long lastSeen;          // momentul ultimului heartbeat primit (ms)
        long lastSeq;           // numărul ultimului heartbeat primit
        long received;          // câte heartbeat-uri au sosit
        int suspicions;         // de câte ori a devenit SUSPECT
        int falseFailures;      // declarat CAZUT, dar a revenit fără repornire
        int restarts;           // a revenit cu numerele de la 1

        NodeInfo(String id) {
            this.id = id;
        }
    }

    private final long suspectMs;
    private final long failMs;
    private final Map<String, NodeInfo> nodes = new TreeMap<>();

    Monitor(long suspectMs, long failMs) {
        this.suspectMs = suspectMs;
        this.failMs = failMs;
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6000;
        long suspect = 2000, fail = 4000, status = 0;
        for (int i = 1; i + 1 < args.length; i += 2) {
            switch (args[i]) {
                case "--suspect" -> suspect = Long.parseLong(args[i + 1]);
                case "--fail" -> fail = Long.parseLong(args[i + 1]);
                case "--status" -> status = Long.parseLong(args[i + 1]);
                default -> throw new IllegalArgumentException("opțiune necunoscută: " + args[i]);
            }
        }
        new Monitor(suspect, fail).run(port, status);
    }

    void run(int port, long statusMs) throws Exception {
        var timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(this::check, 100, 100, TimeUnit.MILLISECONDS);
        if (statusMs > 0) timer.scheduleAtFixedRate(this::printTable, statusMs, statusMs, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::printTable));

        try (DatagramSocket socket = new DatagramSocket(port)) {
            log("monitor pe portul UDP " + port + ": SUSPECT după " + suspectMs + " ms, CAZUT după " + failMs + " ms");
            byte[] buffer = new byte[512];
            while (true) {
                // TODO 1 - recepția:
                //   primiți o datagramă în `buffer`, transformați în text primii packet.getLength() octeți,
                //   despărțiți "HB;id;nr;timp" după ';' și, dacă formatul e corect, apelați
                //   onHeartbeat(id, nr). Ignorați orice alt mesaj.
                Thread.sleep(1000);   // ștergeți această linie după ce implementați recepția
            }
        }
    }

    /** Un heartbeat a sosit: actualizează nodul și tratează revenirea. */
    private synchronized void onHeartbeat(String id, long seq) {
        long now = System.currentTimeMillis();
        NodeInfo n = nodes.get(id);
        if (n == null) {
            n = new NodeInfo(id);
            nodes.put(id, n);
            log(id + " NOU (heartbeat #" + seq + ")");
        }
        // TODO 3 - revenirea. Pentru un nod deja cunoscut, comparați seq cu n.lastSeq:
        //   a) seq <= n.lastSeq: numerele au reînceput, procesul a fost REPORNIT -> n.restarts++;
        //   b) altfel, dacă n.state nu este ACTIV: nodul A REVENIT fără repornire. Afișați după câte ms
        //      (now - n.lastSeen) și câte heartbeat-uri lipsesc (seq - n.lastSeq - 1). Dacă era CAZUT,
        //      declarația a fost greșită -> n.falseFailures++.
        //   Logați fiecare caz.
        n.state = State.ACTIV;
        n.lastSeen = now;
        n.lastSeq = seq;
        n.received++;
    }

    /** Rulează la 100 ms: compară tăcerea fiecărui nod cu cele două praguri. */
    private synchronized void check() {
        long now = System.currentTimeMillis();
        for (NodeInfo n : nodes.values()) {
            long silent = now - n.lastSeen;
            // TODO 2 - tranzițiile:
            //   ACTIV   și silent > suspectMs -> SUSPECT (n.suspicions++);
            //   SUSPECT și silent > failMs    -> CAZUT.
            //   Logați fiecare schimbare, cu numărul de ms de tăcere.
        }
    }

    private synchronized void printTable() {
        long now = System.currentTimeMillis();
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-6s %-8s %10s %8s %11s %13s %10s%n", "nod", "stare", "tăcere ms", "primite", "suspiciuni", "căderi false", "reporniri");
        for (NodeInfo n : nodes.values()) {
            System.out.printf("%-6s %-8s %10d %8d %11d %13d %10d%n",
                    n.id, n.state, now - n.lastSeen, n.received, n.suspicions, n.falseFailures, n.restarts);
        }
    }

    private void log(String msg) {
        System.out.printf("%s %s%n", LocalTime.now().format(TIME), msg);
    }
}
