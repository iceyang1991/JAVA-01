package com.pikachu.gateway.router;

import com.pikachu.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-30 下午11:34
 */
public class WeightRoundRobinHttpRouter implements HttpEndpointRouter{
    private static AtomicInteger indexAtomic = new AtomicInteger(0);
    @Override
    public String route(List<String> endpoints) {

        ArrayList<String> serverList = new ArrayList<>();
        Set<String> serverSet = Server.serverMap.keySet();
        Iterator<String> iterator = serverSet.iterator();
        while(iterator.hasNext()){
            String server = iterator.next();
            Integer weight = Server.serverMap.get(server);
            for (int i = 0; i < weight; i++) {
                serverList.add(server);
            }
        }

        if (indexAtomic.get() >= serverList.size()) {
            indexAtomic.set(0);
        }
        String server = serverList.get(indexAtomic.getAndIncrement());
        return server;
    }
}
