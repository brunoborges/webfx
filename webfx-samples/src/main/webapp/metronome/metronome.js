var $webfx = {title: 'Metronome'};

var java = Packages.java;
var javafx = Packages.javafx;

var URL = java.net.URL;
var ResourceBundle = java.util.ResourceBundle;

var Animation = javafx.animation.Animation;
var TranslateTransition = javafx.animation.TranslateTransition;
var Duration = javafx.util.Duration;

var anim = new TranslateTransition(new Duration(1000.0), circle);
anim.setByX(200);
anim.setAutoReverse(true);
anim.setCycleCount(Animation.INDEFINITE);

function handleStartButtonAction() {
    anim.play();
}

function handlePauseButtonAction() {
    anim.pause();
}

function handleResumeButtonAction() {
    anim.play();
}

function handleStopButtonAction() {
    anim.stop();
}

startButton.disableProperty().bind(anim.statusProperty().isNotEqualTo(Animation.Status.STOPPED));
pauseButton.disableProperty().bind(anim.statusProperty().isNotEqualTo(Animation.Status.RUNNING));
resumeButton.disableProperty().bind(anim.statusProperty().isNotEqualTo(Animation.Status.PAUSED));
stopButton.disableProperty().bind(anim.statusProperty().isEqualTo(Animation.Status.STOPPED));