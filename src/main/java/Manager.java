import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jacek on 19.02.2017.
 */
public class Manager {
    private static Manager ourInstance = new Manager();
    public ArrayList<String> episodesListed;
    public String activeID;
    public ArrayList<DynamicLabel> fav_list = new ArrayList<>();
    public int favListCounter = 0;
    public VBox mFav_vbox;

    private Manager() {
    }

    public void setFavBox(VBox fav) {
        mFav_vbox = fav;
    }

    public static Manager getInstance() {
        return ourInstance;
    }

    public ArrayList<JSONObject> getListOfSeasons(JSONObject jsonObject) {
        ArrayList<JSONObject> jsonSeasons = new ArrayList<>();
        int totalSeasons = Integer.parseInt(jsonObject.getString("totalSeasons"));

        try {

            for (int i = 1; i <= totalSeasons; i++) {
                jsonSeasons.add(
                        BaseConnection.makeRequest(
                                BaseConnection.ID_URL,
                                Manager.getInstance().activeID,
                                BaseConnection.ALL_TYPE,
                                BaseConnection.PAGE_NONE,
                                i));
                Lo.g("Dodano do jsonSeasons arraya");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Lo.g("Błąd w jsonSeasons albo w showseasons");
        }
        return jsonSeasons;
    }

    public void createNewModalWindow(Event event, String layout) {
        Parent root;
        try {
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
            root = FXMLLoader.load(getClass().getResource(layout));
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
            Lo.g("Problem ze zmianą okna.");
        }
    }
}
