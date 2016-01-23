package sample.applet;

import java.util.Base64;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

/**
 * POJO to interact and provide sample data to a browser through an Applet using NPAPI's JSObject.
 * 
 * @author Bruno Borges
 */
public class DataSummary {

    private final JSObject window;

    public DataSummary(JSObject window) {
        this.window = window;
    }

    /**
     * Method is invoked by Javascript. Encodes a String to Base64
     *
     * @param text to be encoded in Base64
     * @return encoded String in Base64
     */
    public String encryptText(String text) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(text.getBytes());
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
                    + addressStr + " : " + phoneNumStr;
            window.call("writeSummary", new Object[]{summary});
        } catch (JSException jse) {
            jse.printStackTrace();
        }
    }

}
