/*
 * Distributed by MobileIron, Inc.
 * Customers and partners may freely copy and re-use.
 */

package com.mobileiron.cordova.appconnect;

import java.lang.reflect.Method;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;



/**
* The necessary imports at the top of the file extends the class from {@code CordovaPlugin} ,
* whose {@code execute()} method it overrides to receive messages from {@code exec()}.
* The {@code execute()} method first tests the value of <strong>action</strong>,
* for which in this case there are two values <strong>requestConfig</strong> and <strong>setConfigHandler</strong>.
*
*/
public class ACConfigPlugin extends CordovaPlugin {
    public static final String CONFIG_RECEIVED_BROADCAST_KEY = "CORE_CONFIG_RECEIVED";
    private static final String TAG = "ACConfigPlugin";
    private CallbackContext callback = null;
    private BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (callback != null) {
                // Send result to JavaScript
                // Note: Set {@code keepCallback} to true to ensure availability of callback
                PluginResult result = new PluginResult(PluginResult.Status.OK, intent.getStringExtra("data"));
                result.setKeepCallback(true);
                callback.sendPluginResult(result);
            }
        }
    };

    // start-up logic
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        retisterBroadcastReceivers();
    }

    @Override
    public void onDestroy() {
        unregisterBroadcastReceivers();
        super.onDestroy();
    }

    /**
     * Generic plugin command executor
     *
     * @param action
     * @param data
     * @param callbackContext
     * @return
     */
    @Override
    public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) {
        Class[] params = new Class[2];
        params[0] = JSONArray.class;
        params[1] = CallbackContext.class;

        try {
            Method method = this.getClass().getDeclaredMethod(action, params);
            method.invoke(this, data, callbackContext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    /**
     * Plugin JAVA interface
     *
     * @description Request config
     * @param data
     * @param context
     * @return
     */
    public boolean requestConfig(final JSONArray data, final CallbackContext context) {
        Log.d(TAG, "requestConfig *** edited");
        AppConnectConfigService.requestConfig(cordova.getActivity());
        return true;
    }
/*
    public boolean requestConfig() {
        Log.d(TAG, "requestConfig **** edited");
        AppConnectConfigService.requestConfig(cordova.getActivity());
        return true;
    }
*/
    /**
     * Plugin JAVA interface
     *
     * @description Register handler for new/updated configs
     * @param data
     * @param context
     * @return
     */
    public boolean setConfigHandler(final JSONArray data, final CallbackContext context) {
        Log.d(TAG, "setConfigHandler");
        callback = context;
        return true;
    }

    private void retisterBroadcastReceivers() {
        if (mConfigReceiver != null) {
            cordova.getActivity().registerReceiver(mConfigReceiver, new IntentFilter(CONFIG_RECEIVED_BROADCAST_KEY));
        }
    }

    private void unregisterBroadcastReceivers() {
        if (mConfigReceiver != null) {
            cordova.getActivity().unregisterReceiver(mConfigReceiver);
        }
    }
}
