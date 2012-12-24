/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author bruno
 */
public class HistoryEntry {
    
    private URL url;
    private String title;
    private Date lastAccessed;
    private HistoryManager historyManager;

    public HistoryEntry(String title, URL url) {
        this.url = url;
        this.title = title;
        this.lastAccessed = Calendar.getInstance().getTime();
    }
    
    void setHistoryManager(HistoryManager hm) {
        this.historyManager = hm;
    }
    
    /**
     * @return the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the lastAccessed
     */
    public Date getLastAccessed() {
        return lastAccessed;
    }
    
    public void delete() {
        historyManager.delete(this);
    }
    
}
