package com.stytch.mobile;

import java.util.prefs.Preferences;

/**
 * Thin wrapper around {@link Preferences} for the demo app's persisted state.
 * All keys are defined as constants so they stay in sync with the spec.
 */
public class AppPreferences {

    public static final String TYPE_CONSUMER = "CONSUMER";
    public static final String TYPE_B2B = "B2B";

    private static final String KEY_DEMO_APP_TYPE = "DEMO_APP_TYPE";
    private static final String KEY_PUBLIC_TOKEN = "STYTCH_PUBLIC_TOKEN";
    private static final String KEY_ORG_ID = "STYTCH_ORG_ID";

    private final Preferences prefs;

    public AppPreferences(Class<?> anchor) {
        prefs = Preferences.userNodeForPackage(anchor);
    }

    public String getDemoAppType() {
        return prefs.get(KEY_DEMO_APP_TYPE, null);
    }

    public void setDemoAppType(String type) {
        prefs.put(KEY_DEMO_APP_TYPE, type);
    }

    public String getPublicToken() {
        return prefs.get(KEY_PUBLIC_TOKEN, null);
    }

    public void setPublicToken(String token) {
        prefs.put(KEY_PUBLIC_TOKEN, token);
    }

    public String getOrgId() {
        return prefs.get(KEY_ORG_ID, null);
    }

    public void setOrgId(String orgId) {
        if (orgId == null || orgId.isEmpty()) {
            prefs.remove(KEY_ORG_ID);
        } else {
            prefs.put(KEY_ORG_ID, orgId);
        }
    }

    public void clearAll() {
        prefs.remove(KEY_DEMO_APP_TYPE);
        prefs.remove(KEY_PUBLIC_TOKEN);
        prefs.remove(KEY_ORG_ID);
    }
}
