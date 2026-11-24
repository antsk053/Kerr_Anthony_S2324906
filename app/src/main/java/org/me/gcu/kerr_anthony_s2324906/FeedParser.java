package org.me.gcu.kerr_anthony_s2324906;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class FeedParser {

    public ArrayList<Situation> parse(String xmlText, String category) {
        ArrayList<Situation> situations = new ArrayList<>();
        Situation current = null;
        boolean inComment = false;
        boolean inFrom = false;
        boolean inTo = false;
        String delaysType = "";
        int lanesWorking = -1;
        int lanesRestricted = -1;
        double fromLat = 0;
        double fromLong = 0;
        double toLat = 0;
        double toLong = 0;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlText));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tag = xpp.getName();
                    if (tag.equals("situation")) {
                        current = new Situation();
                        current.setCategory(category);
                        delaysType = "";
                        lanesWorking = -1;
                        lanesRestricted = -1;
                        fromLat = 0;
                        fromLong = 0;
                        toLat = 0;
                        toLong = 0;
                    } else if (tag.equals("comment")) {
                        inComment = true;
                    } else if (tag.equals("overallStartTime")) {
                        current.setStartTime(xpp.nextText().trim());
                    } else if (tag.equals("overallEndTime")) {
                        current.setEndTime(xpp.nextText().trim());
                    } else if (tag.equals("tpegDirection")) {
                        current.setDirection(xpp.nextText().trim());
                    } else if (tag.equals("situationRecord")) {
                        current.setId(xpp.getAttributeValue(null, "id"));
                        current.setRecordType(xpp.getAttributeValue(null, "xsi:type"));
                    } else if (tag.equals("roadOrCarriagewayOrLaneManagementType") || tag.equals("abnormalTrafficType") || tag.equals("roadMaintenanceType")) {
                        current.setDetailType(xpp.nextText().trim());
                    } else if (tag.equals("delaysType")) {
                        delaysType = xpp.nextText().trim();
                    } else if (tag.equals("numberOfOperationalLanes")) {
                        lanesWorking = Integer.parseInt(xpp.nextText().trim());
                    } else if (tag.equals("numberOfLanesRestricted")) {
                        lanesRestricted = Integer.parseInt(xpp.nextText().trim());
                    } else if (tag.equals("from")) {
                        inFrom = true;
                    } else if (tag.equals("to")) {
                        inTo = true;
                    } else if (tag.equals("latitude")) {
                        double lat = Double.parseDouble(xpp.nextText().trim());
                        if (inFrom) {
                            fromLat = lat;
                        } else if (inTo) {
                            toLat = lat;
                        }
                    } else if (tag.equals("longitude")) {
                        double lon = Double.parseDouble(xpp.nextText().trim());
                        if (inFrom) {
                            fromLong = lon;
                        } else if (inTo) {
                            toLong = lon;
                        }
                    } else if (tag.equals("value") && inComment) {
                        current.setComment(xpp.nextText().trim());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tag = xpp.getName();
                    if (tag.equals("situation")) {
                        current.setImpact(new Impact(delaysType, lanesWorking, lanesRestricted));
                        current.setLocation(new RoadLocation(fromLat, fromLong, toLat, toLong));
                        current.setRoad(extractRoad(current.getComment()));
                        situations.add(current);
                    } else if (tag.equals("from")) {
                        inFrom = false;
                    } else if (tag.equals("to")) {
                        inTo = false;
                    } else if (tag.equals("comment")) {
                        inComment = false;
                    }
                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException | NumberFormatException e) {
            Log.e("FeedParser", "Parse error", e);
        }
        return situations;
    }

    // Further parsing: finds the first word that looks like a road name (A9, M8, B744 ...)
    private String extractRoad(String comment) {
        if (comment == null) {
            return "Unknown";
        }
        String[] words = comment.split(" ");
        for (String word : words) {
            word = word.replaceAll("[.,;:]+$", "");
            if (word.length() > 1
                    && (word.charAt(0) == 'A' || word.charAt(0) == 'M' || word.charAt(0) == 'B')
                    && Character.isDigit(word.charAt(1))) {
                return word;
            }
        }
        return "Unknown";
    }
}