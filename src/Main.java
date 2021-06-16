import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

        int i = 0;
        int numNodes = 3;
        int port = 8000;
        while(i < numNodes && port < 65535) {
            Node node = new Node(port);
            System.out.println("Starting up server on localhost:" + port);

            nodes.put(port, node);
            port++;
            i++;
        }
    }


}
