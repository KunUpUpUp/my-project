package com.seasugar.registry.core;

import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.HeartBeat;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InvaildNodeRemoveTask implements Runnable {

    // 心跳检测接口
    @Override
    public void run() {
        // TODO 如果连接线程数过多，O(n)的时间复杂度会不会有问题
        while (true) {
            // 一开始睡三秒对程序没有任何影响，因为10秒才认定下线，三秒后线程就会开始正常工作
            // PS 睡9s都没问题
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Iterator<Map.Entry<String, HeartBeat>> nodeList = CommonCache.NODE_LIST.entrySet().iterator();
            while (nodeList.hasNext()) {
                Map.Entry<String, HeartBeat> node = nodeList.next();
                System.out.println(System.currentTimeMillis() + "检测到" + node.getKey() + "的心跳");
                if ((System.currentTimeMillis() - node.getValue().getLastHeartBeatTime()) > 10 * 1000) {
                    // 掉线了
                    nodeList.remove();
                    System.out.println(node.getKey() + "被剔除");
                }
            }
        }
    }
}
