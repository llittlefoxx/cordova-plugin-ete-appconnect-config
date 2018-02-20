/*
 * Distributed by MobileIron, Inc.
 * Customers and partners may freely copy and re-use.
 */

package com.mobileiron.cordova.appconnect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppConnectConfigService extends IntentService {

    public static final String TAG = "AppConnectConfigService";

    public static final String ACTION_HANDLE_CONFIG = "com.mobileiron.HANDLE_CONFIG";
    public static final String ACTION_REQUEST_CONFIG = "com.mobileiron.REQUEST_CONFIG";
    public static final String PACKAGE_NAME = "packageName";
    public static final String CONFIG_APPLIED_INTENT = "configAppliedIntent";
    public static final String CONFIG_ERROR_INTENT = "configErrorIntent";
    public static final String ERROR_STRING = "errorString";
    public static final String CONFIG = "config";

    // Change to "com.mobileiron.appconnecttester" when testing with AppConnectTester
    private static final String TARGET_PKG = "com.forgepond.locksmith";


    public AppConnectConfigService() {
        super("AppConnectConfigService");
    }

    public static boolean isServiceExisted(Context ctx, Intent intent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);

        // Make sure only one match was found
        return (resolveInfo != null && resolveInfo.size() == 1);
    }

    public static void requestConfig(Context ctx) {
        Intent intent = new Intent(ACTION_REQUEST_CONFIG);
        intent.putExtra(PACKAGE_NAME, ctx.getPackageName());

        Log.d(TAG, "Requesting: " + intent);
        intent.setPackage(TARGET_PKG);
        if (isServiceExisted(ctx, intent)) {
            ctx.startService(intent);
        } else {
            Log.e(TAG, "package: " + TARGET_PKG + " does not exist");
        }
    }

    private static String toString(Bundle b) {
        if (b == null) {
            return "";
        }

        Map<String, String> map = new HashMap<String, String>();

        for (String key : b.keySet()) {
            Object o = b.get(key);
            if (o instanceof Intent) {
                map.put(key, "Intent <" + toString(((Intent)o).getExtras()) + ">");
            } else if (o instanceof Bundle) {
                map.put(key, "Bundle <" + toString((Bundle)o) + ">");
            } else {
                map.put(key, b.getString(key));
            }
        }

        return map.toString();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (!Boolean.parseBoolean(System.getProperty("com.mobileiron.wrapped", "false"))
                    && !getPackageName().equals(getPackageManager().getPermissionInfo(
                    "com.mobileiron.CONFIG_PERMISSION", 0).packageName)) {
                Log.d(TAG, "Refusing intent as we don't own our permission?!, myPackageName=" + getPackageName() + ", configPermission=" + getPackageManager().getPermissionInfo(
                        "com.mobileiron.CONFIG_PERMISSION", 0).packageName);

                return;
            }

        } catch (PackageManager.NameNotFoundException ex) {
            Log.d(TAG, "Refusing intent as we can't find our permission?!" + ex.getLocalizedMessage());
            return;
        }

        if (ACTION_HANDLE_CONFIG.equals(intent.getAction())) {
            Log.d(TAG, "Config received from MI");
            Log.d(TAG, "Received intent : " + intent + " " + toString(intent.getExtras()) + " from package " + intent.getStringExtra(PACKAGE_NAME));

            // Extract config
            Bundle config = intent.getBundleExtra(CONFIG);
            String configStr = toString(config);
            if (config == null) {
                Log.d(TAG, "Received config: No config!");
            } else {
                Log.d(TAG, "Received config: " + configStr);
            }

            try {
                if (isValidConfig()) {
                    Intent i = (Intent)intent.getParcelableExtra(CONFIG_APPLIED_INTENT);
                    i.setPackage(TARGET_PKG);
                    startService(i); // Applied successfully
                    Log.d(TAG, "Return intent: " + i);
                } else {
                    Intent i = (Intent)intent.getParcelableExtra(CONFIG_ERROR_INTENT);
                    i.putExtra(ERROR_STRING, "This is a sample error message.");
                    i.setPackage(TARGET_PKG);
                    startService(i); // There was a problem. Report error
                    Log.d(TAG, "Return intent: " + i);
                }

                // Notify UI that configurations has received.
                Intent configIntent = new Intent(ACConfigPlugin.CONFIG_RECEIVED_BROADCAST_KEY);
                configIntent.putExtra("data", configStr);
                this.sendBroadcast(configIntent);
            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.getLocalizedMessage() + e.getStackTrace());
            }
        }
    }

    private boolean isValidConfig() {
        return true;
    }
}
