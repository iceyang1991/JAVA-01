package com.pikachu.gateway.router;

import com.pikachu.Server;
import com.sun.corba.se.spi.activation.ServerManager;

import java.util.*;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-30 下午11:34
 */
public class WeightRandomHttpRouter implements HttpEndpointRouter{
    @Override
    public String route(List<String> endpoints) {
        ArrayList<String> serverList = new ArrayList<>();
        Set<String> serverSet = Server.serverMap.keySet();
        Iterator<String> iterator = serverSet.iterator();

        Integer weightSum = 0;
        while(iterator.hasNext()){
            String server = iterator.next();
            Integer weight = Server.serverMap.get(server);
            weightSum += weight;
            for (int i = 0; i < weight; i++) {
                serverList.add(server);
            }
        }

        Random random = new Random();
        String server = serverList.get(random.nextInt(weightSum));
        return server;
    }
}
