cordova.define("cordova-plugin-appconnect-config.ACConfigPlugin", function(require, exports, module) {
    /*
     *
     * Distributed by MobileIron, Inc.
     * Customers and partners may freely copy and re-use.
     *
     */

    function ACConfigPlugin() {
        'use strict';
        console.log("TEST -> 0");
    }

    /**
     * Plugin Javascript interface
     *
     * @description Set config handler
     * @param {Function} successCallback The function to call when the config data is available
     */
    ACConfigPlugin.prototype.setConfigHandler = function(successCallback,failureCallback) {
        'use strict';

        console.log("TEST -> 1");
        /**
         *
         * @param {successFunction} successCallback
         * @param {failFunction} null
         * @param {service} "ACConfigPlugin"
         * @param {action} "setConfigHandler"
         */
        cordova.exec (
            successCallback,
            failureCallback,
            "ACConfigPlugin",
            "setConfigHandler",
            []
        );
    };

    /**
     * Plugin Javascript interface
     *
     * @description Request config from Core
     *
     */
    ACConfigPlugin.prototype.requestConfig = function(successCallback,failureCallback) {
        'use strict';

        console.log("TEST -> 2");
        /**
         *
         * @param {successFunction} null
         * @param {failFunction} null
         * @param {service} "ACConfigPlugin"
         * @param {action} "requestConfig"
         */
        cordova.exec (
            successCallback,
            failureCallback,
            "ACConfigPlugin",
            "requestConfig",
            []
        );
    };

    var instance = new ACConfigPlugin();
    module.exports = instance;

// Make plugin work under window.plugins
    if (!window.plugins) {
        window.plugins = {};
    }
    if (!window.plugins.appconnect) {
        window.plugins.appconnect = instance;
    }

});
