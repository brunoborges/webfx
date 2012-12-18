/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 * @author bruno
 */
public class BrowserShortcuts {

    private final Scene scene;

    public BrowserShortcuts(Scene scene) {
        this.scene = scene;
    }

    public void setup(final BrowserFXController controller) {
        final ObservableMap<KeyCombination, Runnable> accelerators = scene.getAccelerators();

        accelerators.put(
                new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
            @Override
            public void run() {
                controller.newTab();
            }
        });
        accelerators.put(
                new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
            @Override
            public void run() {
                controller.closeTab();
            }
        });
        accelerators.put(
                new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });
    }
}
