package com.ericski.backgroundworkerexample.resources;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Simulates doing work on the caller's thread (bad if it takes a long time)
 */
@Path("do")
public class DoResource
{
    /**
     *
     * Blocking call to do work.
     *
     * @param workUnit
     * @return
     * @throws InterruptedException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doWork(@FormParam("workUnit") int workUnit) throws InterruptedException
    {
        Gson gson = new Gson();

        try
        {
            TimeUnit.SECONDS.sleep(workUnit);
        }
        catch (InterruptedException ex)
        {
            // ignore
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("job", workUnit);

        return Response.ok(gson.toJson(responseMap)).build();
    }
}
