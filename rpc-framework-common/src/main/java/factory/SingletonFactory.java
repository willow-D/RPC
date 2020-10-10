package factory;

import java.util.HashMap;
import java.util.Map;

/*
*
* 获取单例对象的工厂类
* */
public class SingletonFactory {

    private static Map<String, Object> objectMap = new HashMap<>();

    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> t){
        String key = t.toString();
        Object instance = objectMap.get(key);
        if(instance==null){
            synchronized (t){
                try{
                    instance = t.newInstance();
                    objectMap.put(key, instance);
                }catch (IllegalAccessException|InstantiationException e) {
                    throw new RuntimeException(e.getMessage(),e);
                }

            }
        }
        return t.cast(instance);  //instance现在是object类，将其转为t类
    }
}
