package com.pikachu;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-30 下午11:22
 */
public class Server {

    public volatile static Map<String, Integer> serverMap = new TreeMap<>();

    static {
        serverMap.put("http://localhost:8801", 1);
        serverMap.put("http://localhost:8802", 2);
        serverMap.put("http://localhost:8803", 3);
    }
}