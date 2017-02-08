import java.util.ArrayList;

public class Series extends Shows {
    private int totalSeasons;
    public ArrayList<Season> seasons;

    public Series(String t, String y, String ty, String i, String p) {
        title = t;
        year = y;
        type = ty;
        id = i;
        plot = p;
        seasons = new ArrayList<>();
    }

    public int getTotalSeasons() {
        return this.totalSeasons;
    }

    public void setTotalSeasons(int tr) {
        totalSeasons = tr;
    }

}
