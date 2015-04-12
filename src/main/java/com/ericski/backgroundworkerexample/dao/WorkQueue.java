
package com.ericski.backgroundworkerexample.dao;

import java.util.List;
import java.util.UUID;


public interface WorkQueue
{
    public JobResponse<Long> consumeJob(UUID key);
    
    public JobResponse<Long> getJob(UUID key);
        
    public JobResponse<Long> submitJob(Long workItem);
    
    public JobResponse<Long> cancelJob(UUID jobId);
    
    public List<JobResponse<Long>> getAllJobs();
    
}
