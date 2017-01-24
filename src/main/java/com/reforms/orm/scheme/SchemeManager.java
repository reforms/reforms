package com.reforms.orm.scheme;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author evgenie
 */
public class SchemeManager implements ISchemeManager {

    private static final String DEFAULT_SCHEME_NAME = "__default__";

    private Map<String, String> schemes = new HashMap<>();

    @Override
    public String getSchemeName(String name) {
        return schemes.get(name);
    }

    public void putSchemeName(String schemeKey, String schemeName) {
        schemes.put(schemeKey, schemeName);
    }

    @Override
    public String getDefaultSchemeName() {
        return getSchemeName(DEFAULT_SCHEME_NAME);
    }

    public void putDefaultSchemeName(String schemeName) {
        putSchemeName(DEFAULT_SCHEME_NAME, schemeName);
    }
}
