package com.pikachu.gateway.router;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-30 下午11:34
 */
public class RoundRobinHttpRouter implements  HttpEndpointRouter{
    private static AtomicInteger indexAtomic = new AtomicInteger(0);
    @Override
    public String route(List<String> endpoints) {
        if (indexAtomic.get() >= endpoints.size()) {
            indexAtomic.set(0);
        }
        return endpoints.get(indexAtomic.getAndIncrement());
    }
}
