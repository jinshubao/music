package com.jean.music.service;

import com.jean.music.model.Music;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Vector;

/**
 * Created by jinshubao on 2016/6/4.
 */
@org.springframework.stereotype.Service
public class ScannerService extends Service<ObservableList<Music>> {
    private File dir;
    private Vector markers = new Vector();

    @Override
    protected Task<ObservableList<Music>> createTask() {
        return new Task<ObservableList<Music>>() {
            @Override
            protected ObservableList<Music> call() throws Exception {
                ObservableList<Music> list = FXCollections.observableArrayList();
                scan(dir, list);
                while (!markers.isEmpty()) {
                }
                return list;
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        };

    }

    private void scan(File dir, ObservableList<Music> list) throws Exception {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                scan(file, list);
            }
        } else if (dir.isFile()) {
            if (dir.getName().toLowerCase().endsWith(".mp3")) {
                Music music = new Music();
                music.setTitle(dir.getName());
                music.setUrl(dir.toURI().toString());
                music.setSize(dir.length());
                Media media = new Media(music.getUrl());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                markers.add(media);
                mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Duration duration = mediaPlayer.getMedia().getDuration();
                            music.setDuration(duration.toSeconds());

                        }finally {

                            markers.remove(media);
                        }
                    }
                });
                list.add(music);
            }
        }
    }

    public void setDir(File dir) {
        this.dir = dir;
    }
}
