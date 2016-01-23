package sample.applet;

import java.applet.Applet;
import java.util.Calendar;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import webfx.applet.JSObjectFX;

/**
 * Sample applet that when invoked from Javascript in a web page, it will send data back to the page
 * either through Live Connect on supported browsers (NPAPI) or a JavaFX browser
 * configured for this Applet (this applet lib must be in classpath).
 *
 * @author Bruno Borges (@brunoborges)
 */
public class DataSummaryApplet extends Applet {

    private JSObject window;

    @Override
    public void init() {
        super.init();
        System.out.println("DataSummaryApplet.init() invoked");

        try {
            Class.forName("webfx.applet.JSObjectFX");
            window = JSObjectFX.getWindow(this);
        } catch (ClassNotFoundException e) {
            window = JSObject.getWindow(this);
        }
    }

    @Override
    public void start() {
        super.start();
        System.out.println("DataSummaryApplet.start() invoked");
    }

    /**
     * Method is invoked by Javascript, but then will invoke several Javascript
     * methods. Needs access to the JSObject.
     */
    public void writeSummary() {
        try {
            String userName = "James Gosling";

            // set JavaScript variable
            window.setMember("userName", userName);

            // invoke JavaScript function
            Number age = (Number) window.eval("getAge()");

            // get a JavaScript object and retrieve its contents
            JSObject address = (JSObject) window.eval("new address();");
            String addressStr = (String) address.getMember("street") + ", "
                    + (String) address.getMember("city") + ", "
                    + (String) address.getMember("state");

            // get an array from JavaScript and retrieve its contents
            JSObject phoneNums = (JSObject) window.eval("getPhoneNums()");
            String phoneNumStr = (String) phoneNums.getSlot(0) + ", "
                    + (String) phoneNums.getSlot(1);

            // dynamically change HTML in page; write data summary
            String summary = userName + " : " + age + " : "
                    + addressStr + " : " + phoneNumStr + " // Information generated at " + Calendar.getInstance().getTime();
            window.call("writeSummary", new Object[]{summary});
        } catch (JSException jse) {
            jse.printStackTrace();
        }
    }

}
