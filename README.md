WebFX
=====
The purpose of this project is to investigate the capabilities of using JavaFX (FXML + JS + CSS) to build rich web pages, instead of using HTML.

Idea is to build an FX browser, a security layer, a navigation scheme where one FXML can tell the browser to go to another FXML and a protocol for server-side communication.

Security Layer
=====
The security layer must provide a sandbox on each tab, to run JavaFX pages. The sandbox must ensure that:
- unsecure code will be run (i.e. local access to files, System.exit, network, etc)
- dialogs/windows can't be created, unless the user gives permition
- access to parent objects (the Tab object, for example)
- provide management and control for long running process, memory consumption, etc.

Navigation Scheme
=====
The navigation scheme must allow the developer to define links between FXMLs, similar to HTML. The new page must replace the previous.

FX Protocol (optional)
=====
There should be an specific protocol to allow server-side communication. It is already possible though, to use HTTP.


Steps
=====
1. Start the WebFX application
2. Type "example.fxml" on the URL box
3. Press FX!
