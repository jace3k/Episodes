import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;


/**
 * Created by Jacek on 20.02.2017.
 */
public class DynamicLabel extends BorderPane {
    private String id;
    private String type;

    public DynamicLabel(int number, String name, String id, String type, String year) {
        super();
        this.id = id;
        this.type = type;
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
        setOnMouseReleased(event -> setOpacity(0.8));
        setOnMouseEntered(event -> setOpacity(0.8));
        setOnMouseExited(event -> setOpacity(1));
    }

    public DynamicLabel(String errorMsg) {
        super();
        setMinHeight(50);
        setStyle("-fx-background-color: brown;");
        setCenter(new Label(errorMsg));
    }

    public String getID() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    class MouseListener implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            setOpacity(0.5);
            Manager.getInstance().activeID = id;
            Manager.getInstance().createNewModalWindow(event, "movieLayout.fxml");
        }
    }
}
