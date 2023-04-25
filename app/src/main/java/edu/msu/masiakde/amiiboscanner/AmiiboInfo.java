package edu.msu.masiakde.amiiboscanner;


import java.io.Serializable;

public final class AmiiboInfo implements Serializable {
    public class Amiibo implements Serializable  {
        public class Release implements Serializable  {
            public String au;
            public String eu;
            public String jp;
            public String na;
        }

        public String amiiboSeries;
        public String character;
        public String gameSeries;
        public String head;
        public String image;
        public String name;
        public String tail;
        public String type;
        public Release release;
    }

    public Amiibo[] amiibo;

    public String getName() {
        return amiibo[0].character;
    }

    public String getImageURL() { return amiibo[0].image; }

    public String getSeries() { return amiibo[0].gameSeries; }
}