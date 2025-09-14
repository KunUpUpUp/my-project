package jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JMXTest {
    public static void main(String[] args) throws Exception {
        FirstJMX obj = new FirstJMX();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        ObjectName name = new ObjectName("jmx:type=FirstJMX");

        mbs.registerMBean(obj, name);
        System.out.println("Press enter to exit.");
        System.in.read();
    }
}
