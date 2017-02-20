import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Jacek on 20.02.2017.
 */
public class MovieController implements Initializable {

    @FXML Label title;
    @FXML Label genre;
    @FXML Label plot;
    @FXML Label date;
    @FXML Label title_label;
    @FXML Label id_label;
    @FXML Button back_button;
    @FXML Button fav_button;
    @FXML Button seasons_button;

    @FXML Pane poster_pane;
    @FXML ToolBar title_toolbar1;
    @FXML ToolBar title_toolbar2;
    @FXML ToolBar title_toolbar3;

    private double xOffset;
    private double yOffset;

    private JSONObject jsonObject;
    private ArrayList<JSONObject> jsonSeasons = new ArrayList<JSONObject>();

    public void initialize(URL location, ResourceBundle resources) {
        setDraggable();
        try {
            jsonObject = BaseConnection.makeRequest(BaseConnection.ID_URL,
                    Manager.getInstance().activeID,
                    BaseConnection.ALL_TYPE,
                    BaseConnection.PAGE_NONE,
                    BaseConnection.PAGE_NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initButtons();
        initLabels();
    }

    private void initLabels() {
        title.setWrapText(true);
        title.setText(jsonObject.getString("Title"));
        genre.setText(jsonObject.getString("Genre"));
        plot.setWrapText(true);
        plot.setText(jsonObject.getString("Plot"));
        date.setText(jsonObject.getString("Released"));
        id_label.setText(jsonObject.getString("imdbID"));
        ImageView imageView = new ImageView();
        //imageView.setFitHeight(370);
        //imageView.setFitWidth(220);
        try{
            imageView.setImage(new Image(jsonObject.getString("Poster")));
            poster_pane.getChildren().add(imageView);
        }catch (Exception e) {
            imageView.setImage(new Image(getClass().getResource("notfound.jpg").toString()));
            poster_pane.getChildren().add(imageView);
        }

        if(jsonObject.getString("Type").equals("series")) title_label.setText("SERIAL");
    }

    private void initButtons() {
        back_button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ((Stage) ((Node)event.getSource()).getScene().getWindow()).close();
            }
        });
        back_button.setDefaultButton(true);
        fav_button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // dodawanie do ulubionych
            }
        });

        seasons_button.setDisable(true);
        if(jsonObject.getString("Type").equals("series")) seasons_button.setDisable(false);

        seasons_button.setOnAction(new SeasonsAction());
    }

    class SeasonsAction implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            int totalSeasons = Integer.parseInt(jsonObject.getString("totalSeasons"));

            try {
                for (int i = 1; i <= totalSeasons; i++) {
                    Lo.g("Poczatek pętli");
                    jsonSeasons.add(
                            BaseConnection.makeRequest(
                                    BaseConnection.ID_URL,
                                    Manager.getInstance().activeID,
                                    BaseConnection.ALL_TYPE,
                                    BaseConnection.PAGE_NONE,
                                    i));
                    Lo.g("Dodano do jsonSeasons arraya");
                }
                showSeasons(event);
            } catch (Exception e) {
                e.printStackTrace();
                Lo.g("Błąd w jsonSeasons albo w showseasons");
            }

        }

        private void showSeasons(ActionEvent event) throws IOException {
            ArrayList<String> episodesListed = new ArrayList<String>();
            int actSeason = 1;
            for(JSONObject j : jsonSeasons) {
                Lo.g("Poczatek pętli wyswietlania 1");
                JSONArray jarray = j.getJSONArray("Episodes");
                for(Object j2 : jarray) {
                    Lo.g("Poczatek pętli wyswietlania 2");
                    JSONObject j2o = (JSONObject) j2;
                    episodesListed.add(j2o.getString("Title")+ " S"+ actSeason +"E" + j2o.getString("Episode")+ " " + j2o.getString("Released"));
                }
                actSeason++;
            }
            Manager.getInstance().episodesListed = episodesListed;
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            Parent root = FXMLLoader.load(getClass().getResource("seasonLayout.fxml"));
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.show();


        }

        private void insertValue(JSONObject j2o) {
            //vBox_poster.getChildren().add(new Label(j2o.getString("Title")+ " S"+ actSeason +"E: " + j2o.getString("Episode")));
        }
    }

    private void setDraggable() {

        title_toolbar1.setOnMousePressed(new MousePress());
        title_toolbar1.setOnMouseDragged(new MouseDrag());

        title_toolbar2.setOnMousePressed(new MousePress());
        title_toolbar2.setOnMouseDragged(new MouseDrag());

        title_toolbar3.setOnMousePressed(new MousePress());
        title_toolbar3.setOnMouseDragged(new MouseDrag());
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
