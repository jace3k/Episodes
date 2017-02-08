import java.util.ArrayList;

public class Season {
    public ArrayList<Episode> episodes;
    private int number;

    public Season(int n) {
        episodes = new ArrayList<>();
        number = n;
    }

    public void addEpisode(Episode name) {
        episodes.add(name);
    }

    public int getNumber() {
        return this.number;
    }
}
