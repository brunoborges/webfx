var jString = Packages.java.lang.String;
var $webfx = {title: "%title"};

function doHello() {
    var user = usernameField.getText();
    var welcomeMessage = $webfx.i18n.getString("greetingMessage");
    welcomeMessage = jString.format(welcomeMessage, user);
    greetingField.setText(welcomeMessage);
}
