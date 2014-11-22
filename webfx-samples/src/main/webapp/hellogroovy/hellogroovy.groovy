$webfx = new groovy.util.Expando([title: "%title"])

def doHello() {
    user = usernameField.getText();
    welcomeMessage = $webfx.i18n.getString("greetingMessage");
    welcomeMessage = String.format(welcomeMessage, user);
    greetingField.setText(welcomeMessage);
}
