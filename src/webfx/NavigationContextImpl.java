/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import webfx.context.NavigationContext;

/**
 *
 * @author bruno
 */
public class NavigationContextImpl implements NavigationContext {

    private final Callback forward;
    private final Callback back;
    private final Callback goTo;

    public NavigationContextImpl(final BrowserTab tab, final PageContext context) {
        forward = new Callback() {
            @Override
            public void call(Object... args) {
                tab.forward();
            }
        };

        back = new Callback() {
            @Override
            public void call(Object... args) {
                tab.back();
            }
        };

        goTo = new Callback() {
            @Override
            public void call(Object... args) {
                try {
                    tab.goTo(new URL(context.getBasePath() + "/" + args[0].toString()));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(NavigationContextImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }

    @Override
    public void forward() {
        forward.call();
    }

    @Override
    public void back() {
        back.call();
    }

    @Override
    public void goTo(String url) {
        goTo.call(url);
    }

    private interface Callback {

        public void call(Object... args);
    }
}
