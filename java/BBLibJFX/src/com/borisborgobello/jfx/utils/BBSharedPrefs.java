package com.borisborgobello.jfx.utils;

import java.util.HashMap;
import java.io.File;

/**
 *
 * @author borisborgobello
 *
 * A very simple yet powerful shared prefs class to store string to object maps
 * Thread safe
 * Requires a Serializer/Deserializer. Defaults to Jackson adapter if available
 */
public class BBSharedPrefs {
    
    private static final String ERROR_ASYNC_IN_SHARED_PREFS = "Async in shared pref transaction is forbidden !";

    // WHOLE APP SINGLETON
    private static String SOFT_CONFIG_FILE = new File("./bbsharedprefs").getAbsolutePath();
    private static boolean CRYPT_SHARED_PREFS = false;
    private static BBSharedPrefsAdapter ADAPTER;

    static {
        BBSharedPrefsAdapter tmp = null;
        try {
            tmp = (BBSharedPrefsAdapter) Class.forName("com.borisborgobello.utils.BBSharedPrefsJacksonAdapter").newInstance();
        } catch (Throwable ex) {}
        ADAPTER = tmp;
    }

    // Convenient method for single shared prefed apps
    public static void setSharedPrefFile(File f, boolean crypted, BBSharedPrefsAdapter adapter) {
        SOFT_CONFIG_FILE = f.getAbsolutePath();
        CRYPT_SHARED_PREFS = crypted;
        ADAPTER = adapter;
    }

    static BBSharedPrefs sp = null;
    public static synchronized final BBSharedPrefs get() {
        if (sp == null) {
            sp = new BBSharedPrefs(SOFT_CONFIG_FILE, CRYPT_SHARED_PREFS, ADAPTER);
        }
        return sp;
    }

    // CUSTOM BOX
    public static synchronized final BBSharedPrefs get(String name, boolean crypted, BBSharedPrefsAdapter adapter) {
        return new BBSharedPrefs(new File("./" + name).getAbsolutePath(), crypted, adapter);
    }

    // Object
    // Could be made more generic instead of only file
    public static interface BBSharedPrefsAdapter {
        public HashMap<String, Object> read(File f, boolean crypted) throws Exception;
        public void write(File f, HashMap<String, Object> map, boolean crypted) throws Exception;
    }

    public class BBSharedPrefsTransaction {
        private boolean saveRequired = false;
        private boolean finished = false;

        public Object get(String key) {
            if (finished) throw new RuntimeException(ERROR_ASYNC_IN_SHARED_PREFS);
            return map.get(key);
        }

        public boolean contains(String key) {
            if (finished) throw new RuntimeException(ERROR_ASYNC_IN_SHARED_PREFS);
            return map.containsKey(key);
        }

        public BBSharedPrefsTransaction set(String key, Object value) {
            if (finished) throw new RuntimeException(ERROR_ASYNC_IN_SHARED_PREFS);
            if (value == null) {
                map.remove(key);
            } else {
                map.put(key, value);
            }
            saveRequired = true;
            return this;
        }
    }

    private HashMap<String, Object> map = new HashMap<>();
    private File confFile;
    private boolean crypted;
    private BBSharedPrefsAdapter adapter;

    private BBSharedPrefs(String boxname, boolean crypted, BBSharedPrefsAdapter adapter) {
        confFile = new File(boxname);
        this.crypted = crypted;
        this.adapter = adapter;
        if (confFile.exists()) {
            try {
                map = adapter.read(confFile, crypted);
            } catch (Exception e) {
                throw new RuntimeException("Unable to read config file");
            }
        } else {
            map = new HashMap<>();
        }
    }

    private void save() {
        try {
            adapter.write(confFile, map, crypted);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to write shared prefs -> " + ex.getMessage());
        }
    }

    public synchronized void atomicallyDo(Callb<BBSharedPrefsTransaction> transac) {
        BBSharedPrefsTransaction t = new BBSharedPrefsTransaction();
        transac.run(t);
        if (t.saveRequired) {
            save();
        }
        t.finished = true;
    }
    
    public synchronized boolean contains(String key) {
        return map.containsKey(key);
    }
    
    public synchronized Object get(String key) {
        return map.get(key);
    }
}
