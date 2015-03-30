package com.ericski.backgroundworkerexample;

import java.io.IOException;
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
    public static final String BASE_URI = "http://localhost:9090/";

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        ResourceConfig rc = new ResourceConfig().packages("com.ericski.backgroundworkerexample.resources");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        System.out.printf("Jersey app started with at %s %nHit enter to stop it...%n", BASE_URI);
        System.in.read();
        server.shutdownNow();
    }
}
