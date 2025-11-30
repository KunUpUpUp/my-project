package com.seasugar.registry;

import com.seasugar.registry.core.RegistryStarter;

public class ServiceRegistry {
    private static RegistryStarter registryStarter;

    public static void main(String[] args) throws InterruptedException {
        registryStarter = new RegistryStarter();
        registryStarter.startUp();
    }
}
