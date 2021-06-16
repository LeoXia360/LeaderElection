import java.time.Clock;
import java.util.Collections;
import java.util.HashMap;

public class Main {

    // Trying to use these as global values in place of a DB. Need to fix these with locks
    public static Node leader;
    public static long startTime;
    public static Clock clock = Clock.systemDefaultZone();
    public static HashMap<Integer, Node> nodes;
    public static Boolean isLeaderAlive = true;
    public static HashMap<Integer, Node> contenders;

    public static void main(String[] args) {

        //starting number of nodes
        int numNodes = 3;

        //starting port
        int port = 8000;

        nodes = initNodes(numNodes, port);
        startTime = clock.millis();

        //init contenders
        contenders = nodes;

        while(true) {

            //if leader is not alive, then change it to a follower handler and select a new leader from contenders
            if (!isLeaderAlive) {
                leader.updateHandler(false);
                electLeader(contenders);
                startTime = clock.millis();
            }

            //reset isLeaderAlive to false, during main sleep leader should heartbeat
            //by setting value to true to show it's still alive
            isLeaderAlive = false;

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes nodes and elects an initial leader
     *
     * @param numNodes - number of nodes to initialize
     * @param port - starting port to initialize with. Automatically stops at 65,535 regardless of numNodes
     * @return HashMap that maps the port a Node is launched on to the Node object
     */
    private static HashMap<Integer, Node> initNodes(int numNodes, int port) {
        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

        int i = 0;
        while(i < numNodes && port < 65535) {
            Node node = new Node(port);
            Thread t = new Thread(node);
            t.start();
            System.out.println("Starting up server on localhost:" + port);
            nodes.put(port, node);
            port++;
            i++;
        }
        contenders = nodes;
        electLeader(nodes);
        return nodes;
    }

    /**
     *  Elects leader based on the largest port number
     *
     * @param nodes - all the contendor nodes or nodes that should be considered
     */
    private static void electLeader(HashMap<Integer, Node> nodes) {
        int maxPort = Collections.max(nodes.keySet());
        leader = nodes.get(maxPort);
        leader.updateHandler(true);
        isLeaderAlive = true;
        contenders.clear();
        System.out.println("ElectLeader, Leader is set to port: " + maxPort);
    }
}
