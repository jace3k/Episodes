import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Jacek on 20.02.2017.
 */
public class SeasonListController implements Initializable {
    @FXML
    VBox vbox_list;
    @FXML
    Button back_button;

    ArrayList<String> epList = Manager.getInstance().episodesListed;

    public void initialize(URL location, ResourceBundle resources) {
        back_button.setDefaultButton(true);
        vbox_list.getChildren().removeAll(vbox_list.getChildren());
        int i = 1;
        for(String t : epList) {
            DynamicLabel dl = new DynamicLabel(t);
            dl.setStyle("-fx-background-color: #fffbe9");
            vbox_list.getChildren().add(dl);
            i++;
        }
        back_button.setOnAction(event -> ((Stage) ((Node)event.getSource()).getScene().getWindow()).close());
    }
}
