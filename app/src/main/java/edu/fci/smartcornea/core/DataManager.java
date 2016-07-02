package edu.fci.smartcornea.core;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static DataManager ourInstance = new DataManager();

    private Map<String, Object> ourData;

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        ourData = new HashMap<>();
    }

    public void putObject(String key, Object value) {
        ourData.put(key, value);
    }

    public Object getObject(String key) {
        if(ourData.containsKey(key)) {
            return ourData.get(key);
        }else {
            return null;
        }
    }
}
