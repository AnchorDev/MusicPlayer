import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
 
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Music Player");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent arg0) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
 
 public static void main(String[] args) {
        launch(args);
    }
}