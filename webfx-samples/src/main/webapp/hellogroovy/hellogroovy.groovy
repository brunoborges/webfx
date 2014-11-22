// TODO inject $webfx and expand with Expando... 

//import groovy.util.Expando

//def webfx = new Expando(title: "%title")

def doHello() {
    user = usernameField.getText();
    //welcomeMessage = $webfx.i18n.getString("greetingMessage");
    //welcomeMessage = jString.format(welcomeMessage, user);
    //greetingField.setText(welcomeMessage);
    greetingField.setText("hello world");
}
