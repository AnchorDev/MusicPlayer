import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MainSceneController implements Initializable{

    @FXML
    private AnchorPane backgroundPane;

    @FXML
    private Button chooseSongButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button playButton;

    @FXML
    private Button previousButton;

    @FXML
    private Slider progressBar;

    @FXML
    private Button resetButton;

    @FXML
    private Button rewindButton;

    @FXML
    private Label songLabel;

    @FXML
    private ComboBox<String> speedBox;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Text durationField;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> playlist;

    private int songNumber;
    private int[] speeds = {25,50,75,100,125,150,175,200};

    private double currentVolume = 100;

    private final String[] textColors = {"#F0D90B", "#240F79", "#D65B16"};
    private final String[] backgroundColors = {"#999B23", "#3B239B", "#9B3923"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playlist = new ArrayList<File>();
    
        directory = new File("playlist");
    
        files = directory.listFiles();
    
        if (files != null) {
            for (File file : files) {
                playlist.add(file);
                System.out.println(file);
            }
        }
    
        if (playlist.isEmpty()) {
            songLabel.setText("Add Songs!");
            TurnButtons(true);
        } else {
            media = new Media(playlist.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
    
            String songName = playlist.get(songNumber).getName();
            if (songName.endsWith(".mp3") || songName.endsWith(".mp4")) {
                songName = songName.substring(0, songName.length() - 4);
            }
            songLabel.setText(songName);
    
            for (int i = 0; i < speeds.length; i++) {
                speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
            }
            speedBox.setOnAction(this::changeSpeed);
    
            volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                }
            });
    
            changeValueSong();
    
            setMaxDurationSong();
        }
    }

    @FXML
    void changeSpeed(ActionEvent event) {
        if(speedBox.getValue() == null){
            mediaPlayer.setRate(1);
        }
        else {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1))*0.01);
        }
    }

    @FXML
    void chooseSong(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose song to add!");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            File destination = new File("playlist/" + selectedFile.getName());
            try {
                Files.copy(selectedFile.toPath(), destination.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            playlist.add(destination);

            if (!playlist.isEmpty()) {
                TurnButtons(false);
                String songName = destination.getName();
                if (songName.endsWith(".mp3") || songName.endsWith(".mp4")) {
                    songName = songName.substring(0, songName.length() - 4);
                }
                songLabel.setText(songName);
            }

            System.out.println("Added song: " + destination);

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            media = new Media(destination.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                }
            });

            speedBox.getItems().clear();
            for (int i = 0; i < speeds.length; i++) {
                speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
            }

            mediaPlayer.setRate(1.0);

            playMedia();
            progressBar.setValue(0);
            changeValueSong();
            setMaxDurationSong();
        }
    }

    @FXML
    void nextMedia(ActionEvent event) {
        
        if(songNumber < playlist.size()-1){
            songNumber++;
        }
        else{
            songNumber = 0;
        }
        mediaPlayer.stop();

        media = new Media(playlist.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        String songName = playlist.get(songNumber).getName();
        if (songName.endsWith(".mp3") || songName.endsWith(".mp4")) {
            songName = songName.substring(0, songName.length() - 4);
        }
        songLabel.setText(songName);

        playMedia();
        progressBar.setValue(0);
        changeValueSong();
        setMaxDurationSong();
    }

    @FXML
    void playMedia() {
        changeSpeed(null);
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            Image playImage = new Image("file:imgs/Play.png");
            ImageView playImageView = new ImageView(playImage);
            playImageView.setFitWidth(30);
            playImageView.setFitHeight(30);
            playButton.setGraphic(playImageView);


            playButton.getTooltip().setText("Play");
        } else {
            mediaPlayer.play();
            Image playImage = new Image("file:imgs/Pause.png");
            ImageView playImageView = new ImageView(playImage);
            playImageView.setFitWidth(30);
            playImageView.setFitHeight(30);
            playButton.setGraphic(playImageView);
            
            playButton.getTooltip().setText("Pause");
        }
    }

    @FXML
    void previousMedia(ActionEvent event) {
        
        if(songNumber > 0){
            songNumber--;
        }
        else{
            songNumber = playlist.size()-1;
        }
        mediaPlayer.stop();
    
        media = new Media(playlist.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    
        String songName = playlist.get(songNumber).getName();
        if (songName.endsWith(".mp3") || songName.endsWith(".mp4")) {
            songName = songName.substring(0, songName.length() - 4);
        }
        songLabel.setText(songName);
    
        playMedia();
        progressBar.setValue(0);
        changeValueSong();
        setMaxDurationSong();
    }

    @FXML
    void resetMedia(ActionEvent event) {
        mediaPlayer.seek(Duration.seconds(0));
    }

    @FXML
    void rewindMedia(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
    }

    @FXML
    void forwardMedia(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
    }

    @FXML
    void progressbarPressed(MouseEvent event) {
        mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
    }
    @FXML
    void progressbarDragged(MouseEvent event) {
        mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
    }
    @FXML
    void progressbarReleased(MouseEvent event) {

    }

    void setMaxDurationSong(){
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = media.getDuration();
            progressBar.setMax(totalDuration.toSeconds());
        });
    }

    void changeValueSong(){
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setValue(newValue.toSeconds());

            long currentSeconds = (long) newValue.toSeconds();
            long currentMinutes = currentSeconds / 60;
            currentSeconds %= 60;

            Duration totalDuration = mediaPlayer.getTotalDuration();
            long totalSeconds = (long) totalDuration.toSeconds();
            long totalMinutes = totalSeconds / 60;
            totalSeconds %= 60;

            durationField.setText(String.format("%02d:%02d / %02d:%02d", currentMinutes, currentSeconds, totalMinutes, totalSeconds));
        });
    }

    void TurnButtons(boolean x){
        playButton.setDisable(x);
        nextButton.setDisable(x);
        previousButton.setDisable(x);
        forwardButton.setDisable(x);
        rewindButton.setDisable(x);
        speedBox.setDisable(x);
        resetButton.setDisable(x);
    }

}
