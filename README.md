# MobileIron AppConnect Plugin for Cordova/PhoneGap

## Description

This plugin allows you to receive app-specific configuration from MobileIron Core.

## Installation

Add the plugin to your project using the Cordova CLI:

```bash
cordova plugin add <plugin-directory>
```

Or using the PhoneGap CLI:

```bash
phonegap local plugin add <plugin-directory>
```

## Usage

### Set Config Handler
The method passed to `setConfigHandler` will be triggered when new or updated configurations are available.

```js
window.plugins.appconnect.setConfigHandler (
    function (result) {
        console.log("config: " + result);
    },
      function () {
            console.log("failed");
        }
);
```
### Request Config
Request the configuration.

```js
window.plugins.appconnect.requestConfig(   function () {
                                                console.log("success);
                                            },
                                              function () {
                                                    console.log("failed");
                                                });
```

## Supported Platforms

- Android (>= API Level 14 / Jelly Bean)
