package com.ericski.backgroundworkerexample.dao;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 */
public enum HazelcastFactory
{
    INSTANCE;

    private final HazelcastInstance hzc = Hazelcast.newHazelcastInstance();
    
    public HazelcastInstance getHazelcastInstance()
    {
        return hzc;
    }
}
