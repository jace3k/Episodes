import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by Jacek on 18.02.2017.
 */
public class Start extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("addEvent.fxml"));
        primaryStage.setTitle("Filmy i Seriale");
        primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
        //primaryStage.setWidth(635);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

}
