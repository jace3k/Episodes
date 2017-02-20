import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jacek on 19.02.2017.
 */
public class Controller implements Initializable{

    @FXML
    TextField search_text_field;
    @FXML
    Button search_button;
    @FXML
    Button export_button;
    @FXML
    Button remove_button;
    @FXML
    VBox search_vbox;
    @FXML
    VBox fav_vbox;
    @FXML
    ToolBar title_toolbar1;
    @FXML
    ToolBar title_toolbar2;
    @FXML
    ToolBar title_toolbar3;
    @FXML
    Label title_label;
    @FXML
    Button exit_button;
    @FXML
    Button minimize_button;


    private double xOffset;
    private double yOffset;

    public void initialize(URL location, ResourceBundle resources) {
        //search_vbox.getChildren().add(new DynamicLabel("Lucifer","id","series","1999"));
        initButtons();
        setDraggable();
    }

    private void initButtons() {
        search_button.setOnAction(new SearchListener());
        search_button.setDefaultButton(true);
        exit_button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
        minimize_button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true);
            }
        });
    }

    private void setDraggable() {
        title_label.setOnMousePressed(new MousePress());
        title_label.setOnMouseDragged(new MouseDrag());

        title_toolbar1.setOnMousePressed(new MousePress());
        title_toolbar1.setOnMouseDragged(new MouseDrag());

        title_toolbar2.setOnMousePressed(new MousePress());
        title_toolbar2.setOnMouseDragged(new MouseDrag());

        title_toolbar3.setOnMousePressed(new MousePress());
        title_toolbar3.setOnMouseDragged(new MouseDrag());
    }

    class SearchListener implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            String search = search_text_field.getText();
            if(search.length()<3) {
                Lo.g("Too short!");
                search_vbox.getChildren().add(new DynamicLabel("Too short!"));

            } else {
                search_vbox.getChildren().removeAll(search_vbox.getChildren());
                try {
                    JSONObject json;
                    int number = 1;

                    json = BaseConnection.makeRequest(BaseConnection.TITLE_URL, search, BaseConnection.ALL_TYPE, 1, BaseConnection.PAGE_NONE);
                    BaseConnection.pages = (int) Math.ceil((double) BaseConnection.totalResults / 10);
                    for (int i = 1; i <= BaseConnection.pages; i++) {
                        BaseConnection.makeRequest(BaseConnection.TITLE_URL, search, BaseConnection.ALL_TYPE, i, BaseConnection.PAGE_NONE);
                        for (Object o : BaseConnection.jsonArraySearchResult) {
                            JSONObject j = (JSONObject) o;
                            search_vbox.getChildren().add(
                                    new DynamicLabel(
                                            number,
                                            j.getString("Title"),
                                            j.getString("imdbID"),
                                            j.getString("Type"),
                                            j.getString("Year")));
                            number++;
                        }

                    }
                    if (json.has("Error")) search_vbox.getChildren().add(new DynamicLabel(json.getString("Error")));
                } catch (IOException e) {
                    e.printStackTrace();
                    Lo.g("Click search button failed.");
                }
            }
        }
    }

    class MouseDrag implements EventHandler<MouseEvent> {

        public void handle(MouseEvent event) {
            ((Node)event.getSource()).getScene().getWindow().setX(event.getScreenX() + xOffset);
            ((Node)event.getSource()).getScene().getWindow().setY(event.getScreenY() + yOffset);
        }
    }

    class MousePress implements EventHandler<MouseEvent> {

        public void handle(MouseEvent event) {
            xOffset = ((Node)event.getSource()).getScene().getWindow().getX() - event.getScreenX();
            yOffset = ((Node)event.getSource()).getScene().getWindow().getY() - event.getScreenY();
        }
    }
}


