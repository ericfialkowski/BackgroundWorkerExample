package com.ericski.backgroundworkerexample;

import com.hazelcast.core.Hazelcast;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starts up the GrizzlyHttpServer to handle the REST calls
 */
public class Main
{
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:%d/";

	private Main() {}

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
	@SuppressWarnings("squid:S106")
    public static void main(String[] args) throws IOException
    {
        String uriString = String.format(BASE_URI, freePort());
        ResourceConfig rc = new ResourceConfig().packages("com.ericski.backgroundworkerexample.resources");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(uriString), rc);

        System.out.printf("Jersey app started with at %s %nHit enter to stop it...%n", uriString);
        System.in.read();
        server.shutdownNow();

        Hazelcast.shutdownAll();
    }

	@SuppressWarnings("squid:S1166")
    private static int freePort()
    {
        int port = 9090;
        boolean found = false;
        do
        {
            try(ServerSocket ss = new ServerSocket())
            {
                SocketAddress sa = new InetSocketAddress(port);
                ss.bind(sa);
                found = true;
                ss.close();
            }
            catch (IOException ex)
            {
                port++;
            }
        }
        while (!found);
        return port;
    }
}
