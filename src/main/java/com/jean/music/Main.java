package com.jean.music;

import com.jean.music.controller.BaseController;
import com.jean.music.controller.LocalMusicController;
import com.jean.music.controller.MainController;
import com.jean.music.utils.PropertiesUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinshubao on 2016/6/3.
 */
public class Main extends Application {
    public static Map<String, Parent> pages = new HashMap<>();
    private ApplicationContext context = null;
    private Stage stage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        context = new ClassPathXmlApplicationContext("/applicationContext.xml");
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Parent localMusic = initialFXML("/fxml/localMusic.fxml", LocalMusicController.class);
        pages.put("localMusic", localMusic);
        Parent root = initialFXML("/fxml/main.fxml", MainController.class);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Style.css");
        String prop = "/application.properties";
        String name = PropertiesUtil.getProperty(prop, "project.name");
        String version = PropertiesUtil.getProperty(prop, "project.version");
        stage.setTitle(name + "    " + version);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
        stage.setScene(scene);
//        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        /*stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(final WindowEvent event) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setContentText("\r\n\r\n是否退出？\r\n\r\n");
                dialog.setTitle("退出提示");
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                dialog.showAndWait().ifPresent(new Consumer<ButtonType>() {
                    public void accept(ButtonType buttonType) {
                        if (buttonType != ButtonType.OK) {
                            event.consume();
                        }
                    }
                });
            }
        });*/
    }

    public Parent initialFXML(String fxml, final Class<? extends BaseController> controllerClazz) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        BaseController controller = context.getBean(controllerClazz);
        controller.setStage(stage);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return controller;
            }
        });
        return loader.load(getClass().getResourceAsStream(fxml));
    }

    @Override
    public void stop() throws Exception {
        MainController mainController = context.getBean(MainController.class);
        mainController.savePlayList();
    }
}
