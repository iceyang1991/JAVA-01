package com.pikachu.gateway.router;

import java.util.List;
import java.util.Random;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-30 下午11:34
 */
public class HashHttpRouter implements HttpEndpointRouter {
    @Override
    public String route(List<String> endpoints) {
        String sourceIp = "ip" + new Random().nextInt(1000);
        int index = sourceIp.hashCode() % endpoints.size();
        return endpoints.get(index);
    }
}
