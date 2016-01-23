package sample.applet;

import webfx.applet.JSObjectWrapper;
import java.applet.Applet;

/**
 * Sample applet to share an object of class DataSummary with a web page 
 * loaded either through Live Connect on supported browsers (NPAPI)
 * or a JavaFX browser configured for this Applet (this applet lib must be in classpath).
 * 
 * @author Bruno Borges (@brunoborges)
 */
public class DataSummaryApplet extends Applet implements DataSummaryProvider {

    @Override
    public void init() {
        super.init();
        System.out.println("DataSummaryApplet.init() invoked");
    }

    @Override
    public void start() {
        super.start();
        System.out.println("DataSummaryApplet.start() invoked");
    }

    @Override
    public DataSummary getDataSummary() {
        System.out.println("DataSummaryApplet.getDataSummary() invoked");
        return new DataSummary(JSObjectWrapper.getWindow(this));
    }

}
