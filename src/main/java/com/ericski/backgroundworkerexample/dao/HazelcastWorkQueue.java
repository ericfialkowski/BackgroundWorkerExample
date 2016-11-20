package com.ericski.backgroundworkerexample.dao;


public class HazelcastWorkQueue extends WorkQueue
{

    public HazelcastWorkQueue()
    {
		super(HazelcastFactory.INSTANCE.getHazelcastInstance().getMap("finished"),
			  HazelcastFactory.INSTANCE.getHazelcastInstance().getMap("queued"));
    }
}
