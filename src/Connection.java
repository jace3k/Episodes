import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Connection {
    private JButton left;
    private JButton right;
    private JLabel page;
    private JTextField findArea;
    private DefaultListModel<String> listModel;
    JList<String> resultList;
    private JSONObject r;
    private JSONObject rd;
    private int pageCounter = 1;
    private double count;
    private JTabbedPane tabbedPane;
    // favourites
    private JList<String> favList;
    private DefaultListModel<String> favListModel;

    //
    private JFrame temp;

    // details
    private JLabel detailName;
    ArrayList<Shows> showsList;
    Shows actualShow;
    JComboBox<String> detailSeason;
    DefaultListModel<String> episodesListModel;
    JLabel detailPlot;

    public static void main(String[] args) throws IOException {
        Connection c = new Connection();
        c.setGui();
    }
    // whatToDo - 0 to szukanie, -1 to movie, >=1 to series i jednocześnie który sezon
    private JSONObject getResponseFromServer(String quest, int whatToDo) throws IOException {
        String getURL = "";
        if(whatToDo==0) getURL = "http://www.omdbapi.com/?s=" + quest;
        if(whatToDo==(-1)) getURL = "http://www.omdbapi.com/?i=" + quest;
        if(whatToDo>=1)  getURL = "http://www.omdbapi.com/?i=" + quest + "&Season=" + whatToDo;
        getURL = getURL.replaceAll(" ","%20");
        URL obj = new URL(getURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return new JSONObject(response.toString());
        } else {
            System.out.println("GET request not worked");
            return null;
        }
    }

    public void setGui() {
        JFrame frame = new JFrame("Movies & TV Series");
        JButton search = new JButton("Search");
        findArea = new JTextField();
        // tworzenie kart /////////////////////////////////////////////
        tabbedPane = new JTabbedPane();
        JPanel card1 = new JPanel();
        card1.setLayout(new BorderLayout());
        JPanel card2 = new JPanel();
        card2.setLayout(new BorderLayout());

        tabbedPane.addTab("Search",card1);
        tabbedPane.addTab("Favourites",card2);
        ///////////////////////////////////////////////////////////////

        // karta 1 ////////////////////////////////////////////////////
        JPanel mainPanel = new JPanel(new GridLayout(1,2));

        listModel = new DefaultListModel<>();
        left = new JButton("<<");
        right = new JButton(">>");
        page = new JLabel("1/1");

        search.addActionListener(new Swinger());
        left.addActionListener(new LeftListener());
        left.setEnabled(false);
        right.addActionListener(new RightListener());
        right.setEnabled(false);

        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        resultList.setLayoutOrientation(JList.VERTICAL_WRAP);
        resultList.setVisibleRowCount(-1);
        resultList.setFixedCellWidth(-1);
        resultList.addMouseListener(new ResultListener());

        JScrollPane sp = new JScrollPane(resultList);

        JPanel paneltop = new JPanel(new BorderLayout());
        paneltop.add(BorderLayout.EAST, search);
        paneltop.add(BorderLayout.CENTER,findArea);
        paneltop.add(BorderLayout.WEST,new JLabel("Search: "));

        JPanel panelbot = new JPanel(new GridLayout(1,1));
        panelbot.add(sp);

        JPanel panelfloor = new JPanel();
        panelfloor.add(BorderLayout.EAST,left);
        panelfloor.add(BorderLayout.CENTER,page);
        panelfloor.add(BorderLayout.WEST,right);
        card1.add(BorderLayout.NORTH,paneltop);
        card1.add(BorderLayout.CENTER,panelbot);
        card1.add(BorderLayout.SOUTH,panelfloor);
        ////////////////////////////////////////////////////////////////

        // karta 2 /////////////////////////////////////////////////////
        favListModel = new DefaultListModel<>();
        favList = new JList<>(favListModel);
        favList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        favList.setLayoutOrientation(JList.VERTICAL_WRAP);
        favList.setVisibleRowCount(-1);
        favList.setFixedCellWidth(-1);
        JButton favDelete = new JButton("Delete");
        JButton favExport = new JButton("Export");
        favList.addMouseListener(new ResultListener());
        card2.add(BorderLayout.CENTER,favList);
        JPanel card2Panel = new JPanel(new GridLayout(1,2));
        card2Panel.add(favDelete);
        card2Panel.add(favExport);
        card2.add(BorderLayout.SOUTH,card2Panel);

        // lewa strona ////////////////////////////////////////////////
        mainPanel.add(tabbedPane);
        ///////////////////////////////////////////////////////////////

        // prawa strona ///////////////////////////////////////////////
        JPanel detailPanel = new JPanel(new BorderLayout());
        JLabel detailLabel = new JLabel("Details",SwingConstants.CENTER);
        detailPanel.add(BorderLayout.NORTH,detailLabel);




        JPanel infoPanel = new JPanel(new GridLayout(2,1)); /// 1
        JPanel threePointPanel = new JPanel(new GridLayout(3,1));
        detailName = new JLabel("<tu bedzie nazwa serialu/filmu>");
        threePointPanel.add(detailName); // 1
        detailPlot = new JLabel("<tu bedzie fabula>");
        threePointPanel.add(detailPlot); // 2
        JButton addToFav = new JButton("Add to Favourites");
        addToFav.addActionListener(new FavListener());
        threePointPanel.add(addToFav); // 3

        JPanel detailSeasonPanel = new JPanel(new GridLayout(1,2));
        detailSeason = new JComboBox<>();
        detailSeason.addActionListener(new DetailSeasonListener());
        detailSeasonPanel.add(new JLabel("Season: "));
        detailSeasonPanel.add(detailSeason);
        detailPanel.add(BorderLayout.SOUTH,detailSeasonPanel);



        episodesListModel = new DefaultListModel<>();
        JList<String> episodesList = new JList<>(episodesListModel);
        episodesListModel.addElement("S1E1 Blablabla");
        episodesListModel.addElement("S1E2 Bubuabaubua");
        JScrollPane episodesScroll = new JScrollPane(episodesList);
        infoPanel.add(threePointPanel);
        infoPanel.add(episodesScroll);
        detailPanel.add(BorderLayout.CENTER,infoPanel);


        mainPanel.add(detailPanel);



        // inicjacja ramki ////////////////////////////////////////////
        frame.getRootPane().setDefaultButton(search);
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void loading() {
        temp = new JFrame("Loading");

        ImageIcon loading = new ImageIcon(getClass().getResource("animation.gif"));
        temp.add(new JLabel("loading... ", loading, JLabel.CENTER));
        temp.setBackground(Color.white);
        temp.setResizable(false);
        temp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        temp.setSize(400,100);
        temp.setLocationRelativeTo(null);
        temp.setVisible(true);
    }

    public void loadingStop() {
        temp.setVisible(false);
    }

    public ArrayList<Shows> getSearchResults(JSONObject j) {
        ArrayList<Shows> showList = new ArrayList<>();
        if(!(j==null) && (j.getString("Response").equals("True"))) {
            JSONArray searched = j.getJSONArray("Search");
            JSONObject[] jsonTab = new JSONObject[10];
            for (int i = 0; i < searched.length(); i++) {
                jsonTab[i] = searched.getJSONObject(i);
                if(jsonTab[i].getString("Type").equals("series")) {
                    Series series = new Series(
                        jsonTab[i].getString("Title"),
                        jsonTab[i].getString("Year"),
                        jsonTab[i].getString("Type"),
                        jsonTab[i].getString("imdbID"),
                        "There is series plot.."
                    );
                    showList.add(series);
                } else {
                    Movie movie = new Movie(
                        jsonTab[i].getString("Title"),
                        jsonTab[i].getString("Year"),
                        jsonTab[i].getString("Type"),
                        jsonTab[i].getString("imdbID"),
                        "There is movie plot.."
                    );
                    showList.add(movie);
                }
            }
        } else {
            assert j != null;
            showList.add(
                    new Movie(j.getString("Error"),"","errorType","errorId","errorPlot")
            );
        }
        return showList;
    }

    public Shows getShowResults(JSONObject j) {
        if(!(j==null) && (j.getString("Response").equals("True"))) {
            if(j.getString("Type").equals("series")) {
                Series series = new Series(
                    j.getString("Title"),
                    j.getString("Year"),
                    j.getString("Type"),
                    j.getString("imdbID"),
                    j.getString("Plot")
                );
                if(!j.getString("totalSeasons").equals("N/A")) series.setTotalSeasons(Integer.parseInt(j.getString("totalSeasons")));
                else series.setTotalSeasons(0);
                return series;
            } else {
                return new Movie(
                        j.getString("Title"),
                        j.getString("Year"),
                        j.getString("Type"),
                        j.getString("imdbID"),
                        j.getString("Plot")
                );
            }
        } else return new Movie(j.getString("Error"),"","error","N/A","ErrorPlot");
    }

    public void showResultsX(ArrayList<Shows> showList) {
        listModel.removeAllElements();
        for(Shows s : showList) {
            listModel.addElement(s.present());
        }
    }

    class LeftListener extends SwingWorker<Void, Void> implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new LeftListener().execute();
        }
        public Void doInBackground() {
            loading();
            return null;
        }
        public void done() {
            pageCounter--;
            if(pageCounter==1) left.setEnabled(false);
            right.setEnabled(true);
            page.setText(String.valueOf(pageCounter)+"/"+(int)count);
            try {
                r = getResponseFromServer(findArea.getText() + "&page=" + pageCounter, 0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            showsList = getSearchResults(r);
            showResultsX(showsList);
            loadingStop();
        }
    }

    class RightListener extends SwingWorker<Void, Void> implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new RightListener().execute();
        }
        @Override
        public Void doInBackground() {
            loading();
            return null;
        }
        @Override
        public void done() {
            pageCounter++;
            if(pageCounter==count) right.setEnabled(false);
            left.setEnabled(true);
            page.setText(String.valueOf(pageCounter)+"/"+(int)count);
            try {
                r = getResponseFromServer(findArea.getText() + "&page=" + pageCounter, 0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            showsList = getSearchResults(r);
            showResultsX(showsList);
            episodesListModel.removeAllElements();
            detailSeason.setEnabled(false);
            loadingStop();
        }
    }

    class Swinger extends SwingWorker<Void, Void> implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            new Swinger().execute();
        }
        @Override
        public Void doInBackground() {
            loading();
            return null;
        }
        @Override
        protected void done() {
            int totalRes = 0;
            try {
                r = getResponseFromServer(findArea.getText(), 0);
                System.out.println("Response from server zadziałał.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            showsList = getSearchResults(r);
            showResultsX(showsList);
            if(!(r.getString("Response").equals("False"))) totalRes = Integer.parseInt(r.getString("totalResults"));
            count = Math.ceil((double) totalRes/10);
            pageCounter = 1;
            page.setText(String.valueOf(pageCounter)+"/"+(int)count);
            if(count>1) right.setEnabled(true);
            left.setEnabled(false);
            if(count==1) {
                right.setEnabled(false);
            }
            episodesListModel.removeAllElements();
            detailSeason.setEnabled(false);
            loadingStop();
        }
    }

    class ResultListener extends SwingWorker<Void, Void> implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(!resultList.isSelectionEmpty()) new ResultListener().execute();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        protected Void doInBackground() throws Exception {
            loading();
            return null;
        }

        @Override
        protected void done() {
            // klikniecie na film. pobrac jego indeks - który to film i wyświetlić szczegóły
            try {
                rd = getResponseFromServer(showsList.get(resultList.getSelectedIndex()).getId(), -1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Shows shows = getShowResults(rd);
            detailName.setText(shows.getTitle()+ " " + shows.getYear());
            //fabuła
            detailPlot.setText(shows.getPlot());
            // odczyt sezonów i epków jak to serial
            if(shows.getType().equals("series")) {
                Series se = (Series) shows;
                int total = se.getTotalSeasons();
                if(total>=1){
                    // dodawanie sezonów
                    detailSeason.setEnabled(true);
                    detailSeason.removeAllItems();
                    for(int i = 1; i<=total; i++) {
                        try {
                            JSONObject rds = getResponseFromServer(se.getId(), i);
                            Season s = new Season(i);
                            assert rds != null;
                            JSONArray jsonEpisodes = rds.getJSONArray("Episodes");
                            // dodawanie episodów
                            for(int j=1; j<=jsonEpisodes.length(); j++) {
                                JSONObject ep = jsonEpisodes.getJSONObject(j-1);
                                s.episodes.add(new Episode(ep.getString("Title"),ep.getString("Released"),i,j));
                            }
                            se.seasons.add(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        detailSeason.addItem(i+"");
                    }
                    episodesListModel.removeAllElements();
                    for(Episode x : se.seasons.get(detailSeason.getSelectedIndex()).episodes) {
                        episodesListModel.addElement(x.getName());
                    }
                } else {
                    episodesListModel.removeAllElements();
                    episodesListModel.addElement("N/A");
                }
                actualShow = se;
            } else {
                actualShow = shows;
                detailSeason.setEnabled(false);
                episodesListModel.removeAllElements();
            }

            loadingStop();
        }
    }

    class DetailSeasonListener extends SwingWorker<Void, Void> implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            new DetailSeasonListener().execute();
        }

        @Override
        protected Void doInBackground() throws Exception {
            //loading();
            return null;
        }

        @Override
        protected void done() {
            Series se = (Series) actualShow;
            episodesListModel.removeAllElements();
            for(Episode x : se.seasons.get(detailSeason.getSelectedIndex()).episodes) {
                episodesListModel.addElement(x.getName());
            }
            //loadingStop();
        }
    }

    class FavListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            favListModel.addElement(resultList.getSelectedValue());
        }
    }
}