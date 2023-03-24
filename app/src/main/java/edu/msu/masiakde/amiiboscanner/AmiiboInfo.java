package edu.msu.masiakde.amiiboscanner;


public final class AmiiboInfo {
    public class Amiibo {
        public class Release {
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
}