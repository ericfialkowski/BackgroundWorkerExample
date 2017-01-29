package com.ericski.backgroundworkerexample.resources;

import com.ericski.backgroundworkerexample.dao.JobResponse;
import com.ericski.backgroundworkerexample.dao.SimpleWorkQueue;
import com.ericski.backgroundworkerexample.dao.WorkQueue;
import com.google.gson.Gson;
import java.util.UUID;
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
    private static final WorkQueue WORK_QUEUE = new SimpleWorkQueue();

    /**
     * Lists all outstanding background work
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll()
    {
        Gson gson = new Gson();
        return Response.ok(gson.toJson(WORK_QUEUE.getAllJobs())).build();
    }

    /**
     * Returns the work if done, otherwise HTTP "Accepted" indicating that it isn't done yet
     *
     * @param id
     * @return
     */
    @GET
    @Path("{jobid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("jobid") UUID id)
    {
        JobResponse<Long> work = WORK_QUEUE.consumeJob(id);
        return toWebResponse(work);
    }

    /**
     * Submits work to be done in the background
     *
     * @param workUnit
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addWork(@FormParam("workUnit") long workUnit)
    {
        JobResponse<Long> work = WORK_QUEUE.submitJob(workUnit);
        return toWebResponse(work);
    }

    /**
     * Used for canceling the work
     *
     * @param id
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{jobid}")
    public Response cancelWork(@PathParam("jobid") UUID id)
    {
        JobResponse<Long> canceledJob = WORK_QUEUE.cancelJob(id);
        return toWebResponse(canceledJob);
    }

    /*--------------------------------------------------------------------------------
     *
     *  Various Helpers
     *
     --------------------------------------------------------------------------------*/
    private String toJson(JobResponse<Long> response)
    {
        Gson g = new Gson();
        return g.toJson(response);
    }

    private Response toWebResponse(JobResponse response)
    {
        return Response
            .status(jobStatusToResponseStatus(response.getStatus()))
            .entity(toJson(response))
            .build();
    }

	@SuppressWarnings("squid:MethodCyclomaticComplexity")
    private Response.Status jobStatusToResponseStatus(JobResponse.JobStatus jobStatus)
    {
        switch (jobStatus)
        {
            case DONE:
            case CANCELED:
                return Response.Status.OK;
            case QUEUED:
                return Response.Status.CREATED;
            case NOTSTARTED:
            case INPROGRESS:
                return Response.Status.ACCEPTED;
            case NOTFOUND:
                return Response.Status.NOT_FOUND;
            case ERROR:
                return Response.Status.INTERNAL_SERVER_ERROR;
            default:
                return Response.Status.BAD_REQUEST; // should never get here
        }
    }
}
