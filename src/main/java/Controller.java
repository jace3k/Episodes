import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @FXML
    Label loading_label;
    @FXML
    Label loading_label2;

    private double xOffset;
    private double yOffset;

    private static final String CALENDAR_SUMMARY = "Premiery Filmowe (app)";
    private static final String SERIES = "series";
    private static final String MOVIE = "movie";


    private ArrayList<DynamicLabel> searchedList;

    public void initialize(URL location, ResourceBundle resources) {
        //search_vbox.getChildren().add(new DynamicLabel("Lucifer","id","series","1999"));
        loading_label.setText("Wyeksportowano pomyślnie.");
        loading_label.setVisible(false);
        loading_label2.setText("Eksportowanie w toku...");
        loading_label2.setVisible(false);
        initButtons();
        setDraggable();
        Manager.getInstance().mFav_vbox = fav_vbox;
        Platform.setImplicitExit(false);
    }

    private void initButtons() {

        search_button.setOnAction(event -> {

            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    Platform.runLater(() -> searchedList = searchBaby());
                      return null;
                }
            };
            task.setOnSucceeded(event12 -> {
                for(DynamicLabel dl : searchedList) search_vbox.getChildren().add(dl);
                search_button.setDisable(false);
            });
            task.run();
            search_button.setDisable(true);




        });


        search_button.setDefaultButton(true);
        exit_button.setOnAction(event -> System.exit(0));
        minimize_button.setOnAction(event -> ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true));


        export_button.setOnAction((ActionEvent event) -> {
            loading_label.setVisible(false);
            loading_label2.setVisible(true);
            new Thread(() -> {
                try {
                    export();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                export_button.setDisable(false);
                loading_label.setVisible(true);
                loading_label2.setVisible(false);
            }).start();
            export_button.setDisable(true);




//            Task task = new Task() {
//                @Override
//                protected Object call() throws Exception {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                export();
//                            } catch (IOException | InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                    return null;
//                }
//            };
//            task.setOnScheduled(new EventHandler<WorkerStateEvent>() {
//                @Override
//                public void handle(WorkerStateEvent event) {
//                    loading_label.setText("Ładowanie..");
//                    export_button.setDisable(true);
//                }
//            });
//            task.setOnSucceeded(event1 -> {
//                loading_label.setText("Wyeksportowano pomyślnie.");
//                export_button.setDisable(false);
//            });
//            task.run();
        });
    }

    private String createAndGetCalendarID(Calendar service) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(CALENDAR_SUMMARY);

        service.calendars().insert(calendar).execute();

        // Iterate through entries in calendar list
        String calendarID = "";
        String pageToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                if(calendarListEntry.getSummary().equals(CALENDAR_SUMMARY)) calendarID = calendarListEntry.getId();
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        Lo.g("Utworzono kalendarz " + CALENDAR_SUMMARY + ". ID: " + calendarID);
        return calendarID;
    }

    private void export() throws IOException, InterruptedException {
        Calendar service = GoogleConnection.getCalendarService();
        String calendarID = createAndGetCalendarID(service);


        for(DynamicLabel dl : Manager.getInstance().fav_list) {
            String releasedDate;
            JSONObject js = BaseConnection.makeRequest(
                    BaseConnection.ID_URL,
                    dl.getID(),
                    BaseConnection.ALL_TYPE,
                    BaseConnection.PAGE_NONE,
                    BaseConnection.PAGE_NONE);

            if(dl.getType().equals("series")) {
                ArrayList<JSONObject> listOfSeasons = Manager.getInstance().getListOfSeasons(js);
                for(JSONObject j : listOfSeasons) {
                    if(j.getString("Response").equals("False")) {
                        Lo.g("Nie ma tego sezonu w bazie.");
                    } else {
                        JSONArray jarray = j.getJSONArray("Episodes");
                        int actualSeason = Integer.parseInt(j.getString("Season"));
                        for (Object j2 : jarray) {
                            JSONObject j2o = (JSONObject) j2;
                            Event event = createEvent(j2o,js.getString("Title"),actualSeason,SERIES);
                            service.events().insert(calendarID, event).execute();
                        }
                    }
                }

            } else {
                releasedDate = js.getString("Released");
                Lo.g("Released: "  + releasedDate);
                if(releasedDate.equals("N/A")) {
                    Lo.g("Brak daty przy filmie. nie zostanie dodany.");
                } else {
                    Event event = createEvent(js, "", 0, MOVIE);
                    service.events().insert(calendarID, event).execute();
                }

            }
        }
        Lo.g("Done");
    }

    private Event createEvent(JSONObject js, String titleIfSeries, int seasonIfSeries, final String type) {
        Date date;
        EventDateTime start = null;


        com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();
        switch (type) {
            case MOVIE:
                date = new Date(js.getString("Released"));
                Lo.g("Obiekt data: " + date.toString());
                start = new EventDateTime().setDateTime(new DateTime(date));
                event.setSummary(js.getString("Title") + " " + js.getString("Year")).setStart(start).setEnd(start);
                break;
            case SERIES:
                try {
                    String myDate = js.getString("Released");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    date = simpleDateFormat.parse(myDate);
                    Lo.g("Obiekt data: " + date.toString());
                    start = new EventDateTime().setDateTime(new DateTime(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                event.setSummary(titleIfSeries + " - " + js.getString("Title") + " S"+seasonIfSeries+"E"+js.getString("Episode")).setStart(start).setEnd(start);
                break;
            default:
                Lo.g("Bad type.");
                break;
        }
        Lo.g("Event summary: " + event.getSummary());
        return event;
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

    private ArrayList<DynamicLabel> searchBaby() {
        ArrayList<DynamicLabel> searchResultList = new ArrayList<>();

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
                        searchResultList.add(
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
        return searchResultList;
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


