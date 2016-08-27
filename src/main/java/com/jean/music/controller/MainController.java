package com.jean.music.controller;

import com.jean.music.Main;
import com.jean.music.constant.Constant;
import com.jean.music.model.Music;
import com.jean.music.utils.FormatUtil;
import com.jean.music.utils.SerializeUtil;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class MainController extends BaseController {
    @FXML
    private Button prevStageBtn;
    @FXML
    private Button nextStageBtn;
    @FXML
    private TextField searchBox;
    @FXML
    private Button miniBtn;
    @FXML
    private Button hideBtn;
    @FXML
    private Button maxBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private Button prevMusic;
    @FXML
    private Button playMusicBtn;
    @FXML
    private Button nextMusicBtn;
    @FXML
    private Label playTime;
    @FXML
    private Slider playProgress;
    @FXML
    private Slider volume;
    @FXML
    private Label durationTime;
    @FXML
    private Hyperlink findMusic;
    @FXML
    private Hyperlink myFM;
    @FXML
    private Hyperlink mv;
    @FXML
    private Hyperlink friend;
    @FXML
    private Hyperlink localMusic;
    @FXML
    private Hyperlink downloadManage;
    @FXML
    private Hyperlink myCloudMusic;
    @FXML
    private Hyperlink mySinger;
    @FXML
    private Hyperlink myfavorite;
    @FXML
    private Hyperlink hiMusic;
    @FXML
    private Label musicName;
    @FXML
    private Label singerName;
    @FXML
    private Button favoriteBtn;
    @FXML
    private VBox content;

    private boolean playing = false;
    private MediaPlayer mediaPlayer = null;
    private ObservableList<Parent> subContents = FXCollections.observableArrayList();
    private List<Music> playList = new ArrayList<>();
    private Music currentMusic = null;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding subContentEmptyBinding = new BooleanBinding() {
            {
                bind(subContents);
            }

            @Override
            protected boolean computeValue() {
                return subContents.size() <= 1;
            }
        };
        prevStageBtn.disableProperty().bind(subContentEmptyBinding);
        nextStageBtn.disableProperty().bind(subContentEmptyBinding);
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });

        playMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mediaPlayer == null) {
                    return;
                }
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    pause();
                } else {
                    play();
                }
            }
        });
        playProgress.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mediaPlayer != null) {
                    if (playProgress.isValueChanging()) {
                        mediaPlayer.seek(new Duration(newValue.doubleValue()));
                    }
                }
            }
        });
        volume.setMax(1D);
        volume.setMin(0D);
        volume.setValue(0.3D);
        prevMusic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });

        nextMusicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });

        localMusic.setOnAction(event ->
            changeContent("localMusic")
        );
        changeContent("localMusic");
    }

    private MediaPlayer getMediaPlayer(Music music) {
        Media media = new Media(music.getUrl());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.statusProperty().addListener(new ChangeListener<MediaPlayer.Status>() {
            @Override
            public void changed(ObservableValue<? extends MediaPlayer.Status> observable, MediaPlayer.Status oldValue, MediaPlayer.Status newValue) {
                if (newValue == MediaPlayer.Status.READY) {
                    Duration duration = mediaPlayer.getMedia().getDuration();
                    String durationString = FormatUtil.formatTime(duration);
                    durationTime.setText(durationString);
                    playProgress.setMin(0D);
                    playProgress.setMax(duration.toMillis());
                    musicName.setText(music.getTitle());
                    singerName.setText(music.getSinger());
                    mediaPlayer.volumeProperty().bind(volume.valueProperty());
                } else if (newValue == MediaPlayer.Status.PLAYING) {
                    playMusicBtn.getStyleClass().add("play-pause-btn-pause");
                    playMusicBtn.getStyleClass().removeAll("play-pause-btn-play");
                } else {
                    playMusicBtn.getStyleClass().add("play-pause-btn-play");
                    playMusicBtn.getStyleClass().removeAll("play-pause-btn-pause");
                }
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                playProgress.setValue(newValue.toMillis());
                playTime.setText(FormatUtil.formatTime(newValue));
            }
        });
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        int index = playList.indexOf(currentMusic);
                        if (!playList.isEmpty()) {
                            if (index == playList.size() - 1) {
                                play(playList.get(0));
                            } else {
                                play(playList.get(index + 1));
                            }
                        }
                    }
                });
            }
        });
        return mediaPlayer;
    }


    private void changeContent(String subContentName) {
        Parent parent = Main.pages.get(subContentName);
        subContents.add(parent);
        if (!content.getChildren().contains(parent)) {
            content.getChildren().clear();
            content.getChildren().add(parent);
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                return;
            }
            mediaPlayer.play();
            playing = true;
        }
    }

    public void play(Music music) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        addToPlayList(music);
        mediaPlayer = getMediaPlayer(music);
        play();
        currentMusic = music;
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playing = false;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playing = false;
        }
    }

    public void addToPlayList(Music music) {
        if (!playList.contains(music)) {
            playList.add(music);
        }
    }

    public void savePlayList() {
        if (!playList.isEmpty()) {
            SerializeUtil.serialize(playList, Constant.playListSavePath);
        }
    }
}
