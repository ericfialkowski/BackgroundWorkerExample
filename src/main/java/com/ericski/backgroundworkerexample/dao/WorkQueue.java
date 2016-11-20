package com.ericski.backgroundworkerexample.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class WorkQueue
{
	private final Map<UUID, Long> finishedWork;
	private final Map<UUID, Long> queuedWork;
	private final ExecutorService backgroundWorker;

	protected WorkQueue(Map<UUID, Long> finishedWork, Map<UUID, Long> queuedWork)
	{
		this.finishedWork = finishedWork;
		this.queuedWork = queuedWork;
		this.backgroundWorker = Executors.newCachedThreadPool((Runnable r) ->
		{
			Thread thread = Executors.defaultThreadFactory().newThread(r);
			thread.setDaemon(true);
			return thread;
		});
	}

	public JobResponse<Long> getJob(UUID key)
	{
		return fetchJob(key, false);
	}

	public JobResponse<Long> consumeJob(UUID key)
	{
		return fetchJob(key, true);
	}

	private JobResponse<Long> fetchJob(UUID key, boolean removeIfFinished)
	{
		if (!finishedWork.containsKey(key) && !queuedWork.containsKey(key))
			return new JobResponse<>(key, JobResponse.JobStatus.NOTFOUND);

		if (finishedWork.containsKey(key))
		{
			Long workItem;
			if (removeIfFinished)
				workItem = finishedWork.remove(key);
			else
				workItem = finishedWork.get(key);
			return new JobResponse<>(key, workItem);
		}

		return new JobResponse<>(key, JobResponse.JobStatus.INPROGRESS);
	}

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
				Thread.currentThread().interrupt();
			}
			// move from queued to finished  (note: these two operations probably should be locked @ same time)
			Long work = queuedWork.remove(workResponse.getJobId());
			finishedWork.put(workResponse.getJobId(), work);
		});
		return workResponse;
	}

	public JobResponse<Long> cancelJob(UUID jobId)
	{
		if (queuedWork.containsKey(jobId))
		{
			queuedWork.remove(jobId);
			return new JobResponse<>(jobId, JobResponse.JobStatus.CANCELED);
		}
		if (finishedWork.containsKey(jobId))
		{
			Long work = finishedWork.remove(jobId);
			return new JobResponse<>(jobId, work);
		}

		return new JobResponse<>(jobId, JobResponse.JobStatus.NOTFOUND);
	}

	public List<JobResponse<Long>> getAllJobs()
	{
		List<JobResponse<Long>> jobs = new ArrayList<>();

		finishedWork.entrySet().stream().forEach(entry -> jobs.add(new JobResponse<>(entry.getKey(), entry.getValue())));

		queuedWork.entrySet().stream().forEach(entry -> jobs.add(new JobResponse<>(entry.getKey(), JobResponse.JobStatus.INPROGRESS)));

		return jobs;
	}
}
