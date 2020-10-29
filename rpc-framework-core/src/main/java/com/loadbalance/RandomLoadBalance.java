package com.loadbalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddress) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
