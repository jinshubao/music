package com.jean.music.controller;

import com.jean.music.model.Music;
import com.jean.music.service.ScannerService;
import com.jean.music.utils.FormatUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jinshubao on 2016/6/3.
 */
@Controller
public class LocalMusicController extends BaseController {
    @FXML
    Label musicCount;
    @FXML
    Button playAll;
    @FXML
    Button addAllToPlayList;
    @FXML
    Hyperlink chooseDirectory;
    @FXML
    TextField searchBox;
    @FXML
    TableView<Music> localMusicList;
    @Autowired
    MainController mainController;
    @Autowired
    ScannerService scannerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Music, Integer> indexColumn = (TableColumn<Music, Integer>) localMusicList.getColumns().get(0);
        indexColumn.setCellFactory(new Callback<TableColumn<Music, Integer>, TableCell<Music, Integer>>() {
            @Override
            public TableCell<Music, Integer> call(TableColumn<Music, Integer> param) {
                return new TableCell<Music, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (getTableRow().getItem() != null) {
                            setText(Integer.toString(getIndex() + 1));
                        }
                    }
                };
            }
        });
        TableColumn<Music, String> titleColumn = (TableColumn<Music, String>) localMusicList.getColumns().get(1);
        titleColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Music, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Music, String> param) {
                return new SimpleStringProperty(param.getValue().getTitle());
            }
        });

        TableColumn<Music, String> singerColumn = (TableColumn<Music, String>) localMusicList.getColumns().get(2);
        singerColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Music, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Music, String> param) {
                return new SimpleStringProperty(param.getValue().getSinger());
            }
        });

        TableColumn<Music, String> collectionNameColumn = (TableColumn<Music, String>) localMusicList.getColumns().get(3);
        collectionNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Music, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Music, String> param) {
                return new SimpleStringProperty(param.getValue().getCollectionName());
            }
        });

        TableColumn<Music, Double> durationColumn = (TableColumn<Music, Double>) localMusicList.getColumns().get(4);
        durationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Music, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Music, Double> param) {
                return new SimpleObjectProperty<Double>(param.getValue().getDuration());
            }
        });
        durationColumn.setCellFactory(new Callback<TableColumn<Music, Double>, TableCell<Music, Double>>() {
            @Override
            public TableCell<Music, Double> call(TableColumn<Music, Double> param) {
                return new TableCell<Music, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(FormatUtil.formatTime(item));
                        }
                    }
                };
            }
        });
        TableColumn<Music, Long> sizeColumn = (TableColumn<Music, Long>) localMusicList.getColumns().get(5);
        sizeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Music, Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(TableColumn.CellDataFeatures<Music, Long> param) {
                return new SimpleObjectProperty<Long>(param.getValue().getSize());
            }
        });
        sizeColumn.setCellFactory(new Callback<TableColumn<Music, Long>, TableCell<Music, Long>>() {
            @Override
            public TableCell<Music, Long> call(TableColumn<Music, Long> param) {
                return new TableCell<Music, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(FormatUtil.formatFileSize(item));
                        }
                    }
                };
            }
        });
        localMusicList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        localMusicList.setRowFactory(new Callback<TableView<Music>, TableRow<Music>>() {
            @Override
            public TableRow<Music> call(TableView<Music> tableView) {
                TableRow<Music> tableRow = new TableRow<Music>();
                tableRow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //左键双击
                        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                            mainController.play(tableRow.getItem());
                        }
                    }
                });
                return tableRow;
            }
        });

        scannerService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                localMusicList.getItems().addAll(scannerService.getValue());
                musicCount.setText(localMusicList.getItems().size() + "首");
            }
        });
        chooseDirectory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                File dir = chooser.showDialog(stage.getOwner());
                scannerService.setDir(dir);
                scannerService.restart();
            }
        });
        addAllToPlayList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Music music : localMusicList.getItems()) {
                    mainController.addToPlayList(music);
                }
            }
        });
    }
}
