package webfx.applet;

import java.applet.Applet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Allows an Applet to be executed either through Live Connect on supported browsers,
 * or through a JavaFX browser using WebFX Component, such as WebFX Browser.</p>
 *
 * <p>This class may also work with any JavaFX WebView browser implementation.</p>
 * 
 * @author Bruno Borges
 */
public class JSObjectWrapper extends JSObject {

    public static void initAppletsForFX(WebView webView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static JSObjectWrapperMapper register(Class aAppletClass, Object aThis) {
        JSObjectWrapperMapper m = new JSObjectWrapperMapper();
        return m.register(aAppletClass, aThis);
    }

    public static void initAppletsForFX(WebView webView, JSObjectWrapperMapper appletMapper) {
        webView.getEngine().setOnAlert(e -> System.out.println(e.getData()));
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue ov, State oldState, State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    injectApplets();
                }
            }

            private void injectApplets() throws JSException, DOMException {
                NodeList applets = webView.getEngine().getDocument().getElementsByTagName("applet");
                for (int i = 0; i < applets.getLength(); i++) {
                    try {
                        Node applet = applets.item(i);
                        String code = applet.getAttributes().getNamedItem("code").getNodeValue();
                        String appletName = applet.getAttributes().getNamedItem("name").getNodeValue();
                        Class appletClazz = getClass().getClassLoader().loadClass(code);
                        Object found = determineMappedObjectToApplet(appletClazz);
                        injectJavascriptObjects(appletName, found);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(JSObjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            private Object determineMappedObjectToApplet(Class appletClazz) {
                Object found = null;
                for (Class implInt : appletClazz.getInterfaces()) {
                    if (appletMapper.mapper.containsKey(implInt)) {
                        found = appletMapper.mapper.get(implInt);
                        break;
                    }
                }
                return found;
            }

            private void injectJavascriptObjects(String appletName, Object found) throws JSException {
                JSObject doc = (JSObject) webView.getEngine().executeScript("window");
                String internalName = "fx__applet__" + appletName;
                doc.setMember(internalName, found);
                doc.setMember(appletName, found);
                doc.eval("$appletfx.setAppletFromFX('" + appletName + "'," + internalName + ")");
                doc.eval("$appletfx.fxApplet = new Array()");
            }
        });

    }

    public static class JSObjectWrapperMapper {

        private Map<Class, Object> mapper = new HashMap<>();

        private JSObjectWrapperMapper() {
        }

        public JSObjectWrapperMapper register(Class aAppletClass, Object aObject) {
            mapper.put(aAppletClass, aObject);
            return this;
        }

    }

    private final JSObject netscapeJSObject;

    public static JSObject getWindow(Applet applet) {
        return JSObject.getWindow(applet);
    }

    public static JSObject getWindow(Object obj) {
        if (obj instanceof Applet) {
            return new JSObjectWrapper((Applet) obj);
        } else {
            return new JSObjectWrapper((WebEngine) obj);
        }
    }

    private JSObjectWrapper(Applet applet) {
        netscapeJSObject = JSObject.getWindow(applet);
    }

    private JSObjectWrapper(WebEngine webEngine) {
        netscapeJSObject = (JSObject) webEngine.executeScript("window");
    }

    @Override
    public Object call(String methodName, Object[] args) throws JSException {
        return netscapeJSObject.call(methodName, args);
    }

    @Override
    public Object eval(String s) throws JSException {
        return netscapeJSObject.eval(s);
    }

    @Override
    public Object getMember(String name) throws JSException {
        return netscapeJSObject.getMember(name);
    }

    @Override
    public void setMember(String name, Object value) throws JSException {
        netscapeJSObject.setMember(name, value);
    }

    @Override
    public void removeMember(String name) throws JSException {
        netscapeJSObject.removeMember(name);
    }

    @Override
    public Object getSlot(int index) throws JSException {
        return netscapeJSObject.getSlot(index);
    }

    @Override
    public void setSlot(int index, Object value) throws JSException {
        netscapeJSObject.setSlot(index, value);
    }
}
