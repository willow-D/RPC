package com.factory;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

// 获取单例对象的工厂类
public class SingletonFactory {
    private static Map<String, Object> objectMap = new HashMap<>();

    private SingletonFactory(){};

//    public static synchronized <T> T getInstance(Class<T> clazz){
//        String key = clazz.toString();
//        Object instance = objectMap.get(key);
//        if(instance==null){
//            try {
//                instance = clazz.newInstance();
//                objectMap.put(key, instance);
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        return clazz.cast(instance);
//    }
    public static <T> T getInstance(Class<T> clazz) {
        String key = clazz.toString();
        Object instance = objectMap.get(key);
        if (instance == null) {
            synchronized (clazz) {
                if (instance == null) {
                    try {
                       // instance = clazz.getDeclaredConstructor().newInstance();
                        instance = clazz.newInstance();
                        objectMap.put(key, instance);
                    } catch (InstantiationException |IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
