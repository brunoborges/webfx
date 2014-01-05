/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfx;

import java.net.URL;

/**
 *
 * @author bruno
 */
public interface NavigationContext {

    public void forward();

    public void back();

    public void goTo(URL url);

    public void goTo(String url);
    
    public void reload();

}
