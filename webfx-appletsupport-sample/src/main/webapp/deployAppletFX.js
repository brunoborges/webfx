var $appletfx = {
    setAppletFromFX: function(appletName, fxApplet) {
        if (typeof $appletfx.fxApplet === 'undefined') {
            $appletfx.fxApplet = new Array();
        }
        $appletfx.fxApplet[appletName] = fxApplet;
    },
    getApplet: function(appletName) {
        if ($appletfx.fxApplet && $appletfx.fxApplet[appletName]) {
            return $appletfx.fxApplet[appletName];
        }

        // No Applet from FX, try returning the one from HTML
        var appletElement = eval(appletName);
        return appletElement;
    }
};
