package com.tiny.mybatis.type;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TypeAliasRegistry {

    private final Map<String, Class<?>> typeAliases = new HashMap<>();


    public void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            throw new RuntimeException("The parameter alias cannot be null");
        }
        // issue #748
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (typeAliases.containsKey(key) && typeAliases.get(key) != null && !typeAliases.get(key).equals(value)) {
            throw new RuntimeException("The alias '" + alias + "' is already mapped to the value '" + typeAliases.get(key).getName() + "'.");
        }
        typeAliases.put(key, value);
    }

    public <T> Class<T> resolveAlias(String string) {
        try {
            if (string == null) {
                return null;
            }
            // issue #748
            String key = string.toLowerCase(Locale.ENGLISH);
            Class<T> value;
            if (typeAliases.containsKey(key)) {
                value = (Class<T>) typeAliases.get(key);
            } else {
                value = (Class<T>) Class.forName(string);
            }
            return value;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not resolve type alias '" + string);
        }

    }
}
