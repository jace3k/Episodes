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
    private ArrayList<JSONObject> jsonSeasons;
    private boolean existsInFav = false;

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
        back_button.setOnAction(event -> ((Stage) ((Node)event.getSource()).getScene().getWindow()).close());
        back_button.setDefaultButton(true);

        for(DynamicLabel dl : Manager.getInstance().fav_list) {
            if(dl.getID().equals(jsonObject.getString("imdbID"))) existsInFav = true;
        }
        if(existsInFav) fav_button.setText("Usuń z ulubionych");
        else fav_button.setText("Dodaj do ulubionych");

        fav_button.setOnAction(event -> {
            // dodawanie do ulubionych
            if(existsInFav) {
                int indexofDl = -1;
                for(DynamicLabel dl : Manager.getInstance().fav_list) {
                    if(dl.getID().equals(jsonObject.getString("imdbID"))) {
                        indexofDl = Manager.getInstance().fav_list.indexOf(dl);
                        Manager.getInstance().mFav_vbox.getChildren().remove(dl);
                        Manager.getInstance().favListCounter--;
                    }
                }
                Manager.getInstance().fav_list.remove(indexofDl);
                fav_button.setText("Dodaj do ulubionych");
                existsInFav = false;
            } else {
                Manager.getInstance().favListCounter++;
                DynamicLabel favLabel = new DynamicLabel(
                        Manager.getInstance().favListCounter,
                        jsonObject.getString("Title"),
                        jsonObject.getString("imdbID"),
                        jsonObject.getString("Type"),
                        jsonObject.getString("Year")
                );
                Manager.getInstance().fav_list.add(favLabel);
                Manager.getInstance().mFav_vbox.getChildren().add(favLabel);
                fav_button.setText("Usuń z ulubionych");
                existsInFav = true;
            }
        });

        seasons_button.setDisable(true);
        if(jsonObject.getString("Type").equals("series") && (!(jsonObject.getString("totalSeasons").equals("N/A")))) seasons_button.setDisable(false);

        seasons_button.setOnAction(new SeasonsAction());
    }

    class SeasonsAction implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            jsonSeasons = Manager.getInstance().getListOfSeasons(jsonObject);
            showSeasons(event);
        }

        private void showSeasons(ActionEvent event) {
            ArrayList<String> episodesListed = new ArrayList<>();
            for(JSONObject j : jsonSeasons) {
                if(j.getString("Response").equals("False")) {
                    Lo.g("Nie ma tego sezonu w bazie.");
                } else {
                    JSONArray jarray = j.getJSONArray("Episodes");
                    int actualSeason = Integer.parseInt(j.getString("Season"));
                    for (Object j2 : jarray) {
                        JSONObject j2o = (JSONObject) j2;
                        episodesListed.add(j2o.getString("Title") + " [S" + actualSeason + "E" + j2o.getString("Episode") + "] [" + j2o.getString("Released") + "]");
                    }
                }
            }
            Manager.getInstance().episodesListed = episodesListed;
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("seasonLayout.fxml"));
                Scene scene = new Scene(root);
                dialog.setScene(scene);
                dialog.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
