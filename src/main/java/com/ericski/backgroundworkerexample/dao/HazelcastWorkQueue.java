package com.ericski.backgroundworkerexample.dao;

import com.hazelcast.core.HazelcastInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HazelcastWorkQueue implements WorkQueue
{    
    private final Map<UUID, Long> finishedWork;
    private final Map<UUID, Long> queuedWork;
    private final ExecutorService backgroundWorker;
    
    public HazelcastWorkQueue()
    {
        HazelcastInstance hzc = HazelcastFactory.INSTANCE.getHazelcastInstance();
        backgroundWorker = Executors.newCachedThreadPool((Runnable r) ->
        {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        finishedWork = hzc.getMap("finished");
        queuedWork = hzc.getMap("queued");
    }
    
    @Override
    public JobResponse<Long> getJob(UUID key)
    {
        return fetchJob(key, false);
    }

    @Override
    public JobResponse<Long> consumeJob(UUID key)
    {                                    
        return fetchJob(key, true);
    }
    
    private JobResponse<Long> fetchJob(UUID key, boolean removeIfFinished)
    {
        if (!finishedWork.containsKey(key) && !queuedWork.containsKey(key))
            return new JobResponse<>(key,JobResponse.JobStatus.NOTFOUND);
    
        if(finishedWork.containsKey(key))
        {
            Long workItem;            
            if(removeIfFinished)
                workItem = finishedWork.remove(key);
            else
                workItem = finishedWork.get(key);
            return new JobResponse<>(key,workItem);
        }
        
        return new JobResponse<>(key,JobResponse.JobStatus.INPROGRESS);        
    }
            
    @Override
    public JobResponse<Long> submitJob(Long workItem)
    {
        final JobResponse<Long> workResponse = new JobResponse<>();
        queuedWork.put(workResponse.getJobId(), workItem);
        backgroundWorker.submit(() ->
        {
            try
            {
                TimeUnit.SECONDS.sleep(workItem);
            }
            catch (InterruptedException ex)
            {
                // ignore
            }
            System.out.printf("Finished job %s%n", workResponse.getJobId());
            // move from queued to finished  (note: these two operations probably should be locked @ same time)
            Long work = queuedWork.remove(workResponse.getJobId());
            finishedWork.put(workResponse.getJobId(), work);
        });
        return workResponse;
    }
    
    @Override
    public JobResponse<Long> cancelJob(UUID jobId)
    {        
        if (queuedWork.containsKey(jobId))
        {
            queuedWork.remove(jobId);
            JobResponse<Long> response = new JobResponse<>(jobId, JobResponse.JobStatus.CANCELED);
            return response;
        }
        if (finishedWork.containsKey(jobId))
        {
            Long work = finishedWork.remove(jobId);
            JobResponse<Long> response = new JobResponse<>(jobId, work);
            return response;            
        }
            
        return new JobResponse<>(jobId,JobResponse.JobStatus.NOTFOUND);
    }
    
    @Override
    public List<JobResponse<Long>>  getAllJobs()
    {
        List<JobResponse<Long>> jobs = new ArrayList<>();
        
        finishedWork.entrySet().stream().forEach((entry) ->
        {
            jobs.add(new JobResponse<>(entry.getKey(), entry.getValue()));
        });

        queuedWork.entrySet().stream().forEach((entry) ->
        {
            jobs.add(new JobResponse<>(entry.getKey(),JobResponse.JobStatus.INPROGRESS));
        });
        
        return jobs;
    }
}
