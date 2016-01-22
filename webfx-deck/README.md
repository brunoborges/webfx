WebFX Deck
=====
This project is designed to provide you a simple JavaFX deck to your WebFX applications. Run this on your laptop or on an embedded device like RaspberryPi with the -Dwebfx.url parameter pointing to a remote URL with a valid FXML file:
```bash
$ java -jar webfx-deck.jar -Dwebfx.url=http://localhost:8080/webfx-samples/login/login.fxml
```

To reload the page, press F5 or Ctrl+R. You can also connect with jconsole and manipulate the WebFX Deck through the DeckServerMBean object.

To start the WebFX Deck with Remote JMX access enabled, you can use for example the following command-line parameters:
```bash
$ java -Dcom.sun.management.jmxremote \ 
       -Dcom.sun.management.jmxremote.registry.ssl=false \
       -Dcom.sun.management.jmxremote.port=9999 \
       -Dcom.sun.management.jmxremote.authenticate=false \
       -Dcom.sun.management.jmxremote.ssl=false \ 
       -jar webfx-deck.jar
```