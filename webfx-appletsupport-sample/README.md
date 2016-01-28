JavaFX WebView + Applet: Proof of Concept
========
This project is a proof of concept for those who want to slowly and as smoothly as possible migrate away from Applets. Browsers are slownly dropping support of NPAPI, and thus at some point there might not be a major browser able to run Applets.

## How this POC works
This POC shows how legacy Applets can be reused as part of your team's effort of building a modern JavaFX version of your application that leverages the existing Web application. Your web application will be executed and displayed inside a WebView. The sample applet in this POC is loaded as a regular Java object from a library as part of the classpath of the desktop JavaFX application, and injected in the Javascript DOM tree of the WebView component. 

As part of the migration process, a normal browser would interact with this Applet as usual, through Live Connect.

## Running the POC
To run this proof of concept, first build the entire WebFX project:

        $ cd webfx/
        $ mvn install

Now go to project webfx-appletsupport-sample and then start Jetty server:

        $ mvn jetty:run

Then try the Applet first on a browser that still supports NPAPI (Firefox for instance):

        http://localhost:8080/applet/launch.html

Then try using the JavaFX WebView with added Applet support:

        $ mvn exec:java

You've now seen a proof of concept of an Applet that can be deployed as part of a JavaFX Desktop application that loads most of its UI from a Web application inside a WebView component, and if part of this application's functionalities come from Applets, these are loaded as libraries and injected in the engine of WebView to allow existing legacy Javascript to interact with the object. The applet and the web application will continue to work on browsers, as long as browsers support NPAPI. But this will give you a good time window to migrate your applications to a modern solution.
