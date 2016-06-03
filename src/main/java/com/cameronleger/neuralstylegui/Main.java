package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.Image;
import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        NeuralStyle neuralStyle = new NeuralStyle();
        neuralStyle.contentImage = new Image(new File("/home/cameron/input/41406_1_miscellaneous_digital_art_trippy_funky_trippy_tree.jpg"));
        neuralStyle.styleImage = new Image(new File("/home/cameron/input/2015-10-09 03.18.42.JPG"));
        neuralStyle.start();

        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
