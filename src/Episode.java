
public class Episode {
    private String title;
    private String released;
    private int episode;
    private int season;

    public Episode(String t, String r, int s, int e) {
        title = t;
        released = r;
        season = s;
        episode = e;
    }

    public String getTitle() {
        return this.title;
    }

    public String getReleased() {
        return this.released;
    }

    public int getEpisode() {
        return this.episode;
    }

    public int getSeason() {
        return this.season;
    }

    public String getName() {
        return "S"+season+"E"+episode+": "+title+" "+released;
    }
}
