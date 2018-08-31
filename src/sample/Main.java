package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getResource("sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.centerOnScreen();
        primaryStage.setScene(new Scene(root, 200, 300));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Controller controller = fxmlLoader.getController();
                controller.stage = primaryStage;
                if (controller.isConnect) {
                    System.out.println("close websocket");
                    controller.closeSocket();
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
