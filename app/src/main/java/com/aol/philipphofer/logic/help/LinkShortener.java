package com.aol.philipphofer.logic.help;

public class LinkShortener {

    // only use for numeric ids ([0-9]*)
    public static String getLink(String id) {
        String link = id;

        link = link.replace("000000000", "h");
        link = link.replace("00000000", "g");
        link = link.replace("0000000", "f");
        link = link.replace("000000", "e");
        link = link.replace("00000", "d");
        link = link.replace("0000", "c");
        link = link.replace("000", "b");
        link = link.replace("00", "a");

        link = link.replace("01", "i");
        link = link.replace("02", "j");
        link = link.replace("03", "k");
        link = link.replace("04", "l");
        link = link.replace("05", "m");
        link = link.replace("06", "n");
        link = link.replace("07", "o");
        link = link.replace("08", "p");
        link = link.replace("09", "q");

        return link;
    }

    public static String getId(String link) {
        String id = link;

        id = id.replace("a", "00");
        id = id.replace("b", "000");
        id = id.replace("c", "0000");
        id = id.replace("d", "00000");
        id = id.replace("e", "000000");
        id = id.replace("f", "0000000");
        id = id.replace("g", "00000000");
        id = id.replace("h", "000000000");

        id = id.replace("i", "01");
        id = id.replace("j", "02");
        id = id.replace("k", "03");
        id = id.replace("l", "04");
        id = id.replace("m", "05");
        id = id.replace("n", "06");
        id = id.replace("o", "07");
        id = id.replace("p", "08");
        id = id.replace("q", "09");

        return id;
    }
}
