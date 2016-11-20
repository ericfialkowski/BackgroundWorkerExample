package com.ericski.backgroundworkerexample.dao;

import java.util.UUID;

public class JobResponse<T>
{

    public enum JobStatus
    {
        QUEUED,
        NOTSTARTED,
        INPROGRESS,
        DONE,
        ERROR,
        CANCELED,
        NOTFOUND
    }

    private UUID jobId;
    private JobStatus status;
    private T job;


    public JobResponse()
    {
        this.jobId = UUID.randomUUID();
        this.status = JobStatus.QUEUED;
    }

    public JobResponse(UUID jobId, JobStatus status)
    {
        this.jobId = jobId;
        this.status = status;
    }

    public JobResponse(UUID jobId, T job)
    {
        this.jobId = jobId;
        this.status = JobStatus.DONE;
        this.job = job;
    }

    public boolean hasWork()
    {
        return job != null;
    }

    public T getJob()
    {
        return job;
    }

    public void setJob(T job)
    {
        this.job = job;
    }

    public JobStatus getStatus()
    {
        return status;
    }

    public void setStatus(JobStatus status)
    {
        this.status = status;
    }

    public UUID getJobId()
    {
        return jobId;
    }

    public void setJobId(UUID jobId)
    {
        this.jobId = jobId;
    }
}
