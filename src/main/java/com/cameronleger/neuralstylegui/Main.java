package com.cameronleger.neuralstylegui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.exit;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("main", Locale.US);
        FXMLLoader loader = new FXMLLoader();
        Parent root;
        MainController controller;

        try {
            loader.setLocation(getClass().getResource("/main.fxml"));
            loader.setResources(bundle);
            root = loader.load();
            controller = loader.getController();
            controller.setStage(primaryStage);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            exit();
            return;
        }

        try {
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.stopService();
                }
            });
            primaryStage.setTitle(bundle.getString("title"));
            primaryStage.setScene(new Scene(root, 600, 275));
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
