/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.browser.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

/**
 *
 * @author ecostlow
 */
public final class FXUtil {
    public static final String resource(Class<? extends Initializable> clazz) {
        final String className = clazz.getCanonicalName();
        return className.replace('.', '/').replace("Controller", "") + ".fxml";
    }
    
    public static FXMLLoader load(Class clazz){
        final FXMLLoader loader = new FXMLLoader(FXUtil.class.getClassLoader().getResource(resource(clazz)));
        return loader;
    }
}
