package com.ericski.backgroundworkerexample.resources;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("worker")
public class WorkerResource
{
    private static final Map<UUID, Long> work = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll()
    {
        Gson gson = new Gson();
        return Response.ok(gson.toJson(work)).build();
    }

    @GET
    @Path("{jobid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("jobid") UUID id)
    {
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", id);

        if (work.containsKey(id))
        {
            Long when = work.get(id);
            
            if (when < System.currentTimeMillis())
            {
                // work is done, return it
                responseMap.put("work", when.toString());
                work.remove(id);
                return Response.ok(gson.toJson(responseMap)).build();
            }
            
            // work hasn't completed yet
            return Response.status(Response.Status.ACCEPTED).entity(gson.toJson(responseMap)).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).entity(gson.toJson(responseMap)).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addWork(@FormParam("workUnit") int workUnit)
    {
        Gson gson = new Gson();

        // adds "work"
        UUID key = UUID.randomUUID();
        long whenToFinish = System.currentTimeMillis() + (ThreadLocalRandom.current().nextInt(1, workUnit) * 1000);
        work.put(key, whenToFinish);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", key);

        return Response.status(Response.Status.CREATED).entity(gson.toJson(responseMap)).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{jobid}")
    public Response delWork(@PathParam("jobid") UUID id)
    {
        Gson gson = new Gson();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", id);

        // deletes work in progress
        if (work.containsKey(id))
        {
            work.remove(id);
            return Response.ok(gson.toJson(responseMap)).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).entity(gson.toJson(responseMap)).build();
        }
    }
}
