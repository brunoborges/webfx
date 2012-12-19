/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var webfx = {title: "Metronome WebFX Sample"};

var java = Packages.java;
var javafx = Packages.javafx;

var URL = java.net.URL;
var ResourceBundle = java.util.ResourceBundle;

var Animation = javafx.animation.Animation;
var Interpolator = javafx.animation.Interpolator;
var Timeline = javafx.animation.Timeline;
var TranslateTransitionBuilder = javafx.animation.TranslateTransitionBuilder;
var Duration = javafx.util.Duration;

var anim = TranslateTransitionBuilder.create()
        .duration(new Duration(1000.0))
        .node(circle)
        .fromX(0)
        .toX(200)
        .interpolator(Interpolator.LINEAR)
        .autoReverse(true)
        .cycleCount(Timeline.INDEFINITE)
        .build();

function handleStartButtonAction() {
    anim.playFromStart();
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

 