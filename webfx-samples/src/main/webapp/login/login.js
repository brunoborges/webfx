var jString = Packages.java.lang.String;
var $webfx = {title: "%title"};

function doLogin() {
    // example of the NavigationContext feature
    var user = usernameField.getText();
    if (user === "metronome") {
        $webfx.navigation.goTo("../metronome/metronome.fxml");
    } else {
        var welcomeMessage = $webfx.i18n.getString("unknowUserMessage");
        welcomeMessage = jString.format(welcomeMessage, user);
        actiontarget.setText(welcomeMessage);
    }
}
