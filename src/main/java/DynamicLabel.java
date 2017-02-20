import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


/**
 * Created by Jacek on 20.02.2017.
 */
public class DynamicLabel extends BorderPane {
    private String id;
    private String backupStyle;

    public DynamicLabel(int number, String name, String id, String type, String year) {
        super();
        this.id = id;
        setMinHeight(50);
        //setMaxWidth(570);
        setPadding(new Insets(10,10,10,10));

        if(type.equals("series"))setStyle("-fx-background-color: #face92; -fx-border-style: dashed");
        if(type.equals("movie"))setStyle("-fx-background-color: #9dfabb; -fx-border-style: dashed");
        if(type.equals("episode"))setStyle("-fx-background-color: #fa91f9; -fx-border-style: dashed");
        setLeft(new Label(number+""));
        setRight(new Label(type));
        setCenter(new Label(name + " " + year));
        setOnMousePressed(new MouseListener());
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                setOpacity(0.8);
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                setOpacity(0.8);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                setOpacity(1);
            }
        });
    }

    public DynamicLabel(String errorMsg) {
        super();
        setMinHeight(50);
        setStyle("-fx-background-color: brown;");
        setCenter(new Label(errorMsg));
    }

    class MouseListener implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            setOpacity(0.5);
            Manager.getInstance().activeID = id;
            Parent root = null;
            try {
                Stage dialog = new Stage();
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
                root = FXMLLoader.load(getClass().getResource("movieLayout.fxml"));
                Scene scene = new Scene(root);
                dialog.setScene(scene);
                dialog.show();

                //root = FXMLLoader.load(getClass().getResource("movieLayout.fxml"));
                //((Stage) ((Node)event.getSource()).getScene().getWindow()).setScene(new Scene(root));

            } catch (IOException e) {
                e.printStackTrace();
                Lo.g("Problem ze zmianą okna.");
            }

        }
    }
}
