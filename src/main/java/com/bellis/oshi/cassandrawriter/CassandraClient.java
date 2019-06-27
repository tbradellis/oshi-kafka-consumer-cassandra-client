package com.bellis.oshi.cassandrawriter;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.time.Duration;

public abstract class CassandraClient {


    private final Cluster cluster;

    private final Session session;



    public CassandraClient(String ipAddress, String keyspace){
        this.cluster = Cluster.builder()
                .addContactPoint(ipAddress)
                .build();
        this.session = blockingRetryingConnect(keyspace);
    }
    //naturally, retry
    private Session blockingRetryingConnect(String keyspace) {
        Duration retryDelay = Duration.ofMillis(1);
        while (true) {
            try {
                return cluster.connect(keyspace);
            } catch (Exception exception) {
                trySleep(retryDelay);
                retryDelay = min(retryDelay.multipliedBy(2), Duration.ofSeconds(5));
            }
        }
    }


    private void trySleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private Duration min(Duration first, Duration second) {
        return first.compareTo(second) <= 0 ? first : second;
    }
}


