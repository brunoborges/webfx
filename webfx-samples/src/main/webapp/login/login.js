var jString = Packages.java.lang.String;
var $webfx = {title: "%title"};

function handleSubmitButtonAction() {
    var user = usernameField.getText();

    var welcomeMessage = $webfx.i18n.getString("welcomeMessage");
    welcomeMessage = jString.format(welcomeMessage, user);
    actiontarget.setText(welcomeMessage);

    // example of the NavigationContext feature
    if (user === "metronome") {
        $webfx.navigation.goTo("../metronome/metronome.fxml");
    }
}
