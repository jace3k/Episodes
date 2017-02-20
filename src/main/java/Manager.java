import java.util.ArrayList;

/**
 * Created by Jacek on 19.02.2017.
 */
public class Manager {
    private static Manager ourInstance = new Manager();
    public ArrayList<String> episodesListed;

    public static Manager getInstance() {
        return ourInstance;
    }

    public String activeID;


    private Manager() {
    }
}
