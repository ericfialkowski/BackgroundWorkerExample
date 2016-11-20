package com.ericski.backgroundworkerexample.dao;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWorkQueue extends WorkQueue
{
	public SimpleWorkQueue()
	{
		super(new ConcurrentHashMap<UUID, Long>(), new ConcurrentHashMap<UUID, Long>());
	}
}
