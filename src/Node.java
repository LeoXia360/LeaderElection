import java.util.UUID;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;

import handlers.FollowerHandler;
import handlers.LeaderHandler;

public class Node {
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

    public void updateHandler(boolean isLeader) {
        this.server.removeContext(this.httpContext);
        if(isLeader){
            this.httpContext = server.createContext("/", new LeaderHandler());
        } else {
            this.httpContext = server.createContext("/", new FollowerHandler());
        }
    }
}
