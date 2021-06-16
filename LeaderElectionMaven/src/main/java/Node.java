import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import handlers.FollowerHandler;
import handlers.LeaderHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.UUID;

public class Node implements Runnable {
    private String nodeId;
    private Integer port;
    private HttpServer server;
    private HttpContext httpContext;
    private boolean isLeader = false;

    public Node(int port) {
        // set port
        this.port = port;

        //generate a unique id for this node
        this.nodeId = UUID.randomUUID().toString();
        System.out.println("NodeId: " + this.nodeId);

        //needs to start up a server
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set the handler, all Nodes should start off as Followers
        this.httpContext = server.createContext("/", new FollowerHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    /**
     * Updates the handler based on a boolean on whether or not this Node should be a Leader
     *
     * @param isLeader - flag for decision between Leader and Follower handler
     */
    public void updateHandler(boolean isLeader) {
        this.server.removeContext(this.httpContext);
        if(isLeader){
            this.httpContext = server.createContext("/", new LeaderHandler());
        } else {
            this.httpContext = server.createContext("/", new FollowerHandler());
        }
    }

    public void run() {
        while(true) {
            if(isLeader) {
                // Send heart beat that leader is still alive
                Main.isLeaderAlive = true;

            } else {
                //check to see if I can be the leader
                long currentTime = Main.clock.millis();

                if (!Main.isLeaderAlive) {
                    Main.contenders.put(port, this);
                }
            }

            //slumber - 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //used for testing
    public boolean randomDeath() {
        Random rand = new Random();
        int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
        System.out.println(randomNum);
        if (randomNum > 900) {
            return true;
        }
        return false;
    }
}
