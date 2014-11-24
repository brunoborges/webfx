var jString = Packages.java.lang.String;

var $webfx = {
    title: "%title",
    initWebFX: function () {
        print("WebFX initialized in Javascript!");
    }
};

function doHello() {
    var user = usernameField.getText();
    var welcomeMessage = $webfx.i18n.getString("greetingMessage");
    welcomeMessage = jString.format(welcomeMessage, user);
    greetingField.setText(welcomeMessage);
}
