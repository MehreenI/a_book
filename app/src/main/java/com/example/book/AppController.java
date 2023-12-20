package com.example.book;

import com.example.book.manager.CoinManager;
import com.example.book.manager.Manager;


import java.util.HashMap;
import java.util.Map;

public class AppController {
    private static AppController instance;
    public static synchronized AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }
    private AppController() {
        addManager(CoinManager.class, new CoinManager());
    }

    private Map<Class<?>, Manager> managerMap = new HashMap<>();

    private void addManager(Class<?> managerClass, Manager manager) {
        managerMap.put(managerClass, manager);
    }
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerClass) {
        return (T) managerMap.get(managerClass);
    }


}