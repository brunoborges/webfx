/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author bruno
 */
public class HistoryManager {
    
    private ObservableList<HistoryEntry> historyEntries;
    
    public HistoryManager() {
        historyEntries = FXCollections.observableArrayList();
    }

    void delete(HistoryEntry aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
