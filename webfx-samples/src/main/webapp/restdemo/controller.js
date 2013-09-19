/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var webfx = {title: "Search REST Demo", i18n: null};

var java = Packages.java;
var javafx = Packages.javafx;
var restfx = Packages.restfx;

var Desktop = java.awt.Desktop;
var ContentDisplay = javafx.scene.control.ContentDisplay;
var Image = javafx.scene.image.Image;
var ImageView = javafx.scene.image.ImageView;
var URL = java.net.URL;
var QueryListener = restfx.web.QueryListener;
var GetQuery = restfx.web.GetQuery;
var FXCollections = javafx.collections.FXCollections;
var Media = javafx.scene.media.Media;
var MediaPlayer = javafx.scene.media.MediaPlayer;

var mediaPlayer = null;
var getQuery = null;

function handleSearchAction(event) {
    if (getQuery === null) {
        var searchTerms = searchTermTextField.getText();

        if (searchTerms.length() > 0) {
            getQuery = new GetQuery(QUERY_HOSTNAME, BASE_QUERY_PATH);
            getQuery.getParameters().put("term", searchTerms);
            getQuery.getParameters().put("media", MEDIA);
            getQuery.getParameters().put("limit", LIMIT);
            getQuery.getParameters().put("output", "json");

            statusLabel.setText(webfx.i18n.getString("searching"));
            updateActivityState();

            getQuery.execute(queryListener);
        }
    } else {
        getQuery.cancel(true);

        searchButton.setDisable(true);
        statusLabel.setText(webfx.i18n.getString("aborting"));
    }
}

function handlePreviewAction(event) {
    var selectedResult = resultsTableView.getSelectionModel().getSelectedItem();
    var sUrl = selectedResult.get("previewUrl");
    
    if (mediaPlayer != null) {
        mediaPlayer.stop();
    }

    var media = new Media(sUrl);

    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setAutoPlay(true);
    mediaView.setMediaPlayer(mediaPlayer);
    //Desktop.getDesktop().browse(url.toURI());
}

function updateArtwork(index) {
    var result = resultsTableView.getItems().get(index);
    var artworkURL;
    if (result === null) {
        artworkURL = null;
        previewButton.setDisable(true);
    } else {
        artworkURL = result.get("artworkUrl100");
        previewButton.setDisable(false);
    }
    artworkImageView.setImage(artworkURL === null ? null : new Image(artworkURL));
}

function updateActivityState() {
    var active = (getQuery !== null);
    searchTermTextField.setDisable(active);
    //searchButton.setStyle(active ? CANCEL_STYLE : SEARCH_STYLE);
}

/*
 * Initialize
 */
var QUERY_HOSTNAME = "ax.phobos.apple.com.edgesuite.net";
var BASE_QUERY_PATH = "/WebObjects/MZStoreServices.woa/wa/itmsSearch";
var MEDIA = "music";
var LIMIT = 100;
var getQuery;
var mediaPlayer;

searchButton.setStyle("-fx-background-image: url('bullet_cross.png')");

// we can't access the ResourceBundle object from fx:script, so this is a workaround
var originalStatusLabel = statusLabel.getText();

var listChangeListener = new JavaAdapter(javafx.collections.ListChangeListener, {
    onChanged: function(change) {
        while (change.next())
            if (change.wasAdded())
                updateArtwork(change.getAddedSubList().get(0).getRow());
    }
});

var queryListener = new JavaAdapter(QueryListener, {
    queryExecuted: function(task) {
        if (task === getQuery) {
            if (task.isCancelled()) {
                statusLabel.setText(webfx.i18n.getString("cancelled"));
                searchTermTextField.requestFocus();
            } else {
                var exception = task.getException();
                if (exception === null) {
                    var value = task.getValue();
                    var results = value.get("results");

                    // Update the table data
                    var items = FXCollections.observableList(results);
                    resultsTableView.setItems(items);
                    statusLabel.setText(java.lang.String.format(webfx.i18n.getString("resultCountFormat"), new java.lang.Integer(results.size())));

                    if (results.size() > 0) {
                        resultsTableView.getSelectionModel().select(0);
                        resultsTableView.requestFocus();
                    } else {
                        searchTermTextField.requestFocus();
                    }
                } else {
                    statusLabel.setText(exception.getMessage());
                    searchTermTextField.requestFocus();
                }
            }

            getQuery = null;
            searchButton.setDisable(false);

            updateActivityState();
        }
    }
});

// Add a selection change listener to the table view
resultsTableView.getSelectionModel().getSelectedCells().addListener(listChangeListener);

// Do an example initial search so that the table is populated on startup
searchTermTextField.setText("Cheap Trick");

function initialize() {
    handleSearchAction(null);
}