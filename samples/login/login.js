/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var String = Packages.java.lang.String;
var webfx = {title: "%title"};

function handleSubmitButtonAction() {
    var user = usernameField.getText();

    var welcomeMessage = webfx.i18n.getString("welcomeMessage");
    welcomeMessage = String.format(welcomeMessage, user);
    actiontarget.setText(welcomeMessage);

    if (user == "metronome") {
        webfx.navigation.goTo("../metronome/metronome.fxml");
    }
}
