public abstract class Shows {
    protected String title;
    protected String year;
    protected String type;
    protected String id;
    protected String plot;

    public String getTitle() {
        return this.title;
    }

    public String getYear() {
        return this.year;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getPlot() {
        return this.plot;
    }

    public String present() {
        return this.title + " " + this.year;
    }

}
