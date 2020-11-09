package com.lucas.omnia.utils;

import android.util.Xml;

import com.lucas.omnia.models.Law;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LawsXmlParser {
    private static final String ns = null;

    public List<Law> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRecords(parser);
        } finally {
            in.close();
        }
    }

    private List<Law> readRecords(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Law> laws = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "srw:searchRetrieveResponse");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("srw:records")) {
            } else if (name.equals("srw:record")) {
            } else if (name.equals("srw:recordData")) {
            } else if (name.equals("srw_dc:dc")) {
                laws.add(readLaw(parser));
            } else {
                skip(parser);
            }
        }
        return laws;
    }

    // Parses the contents of an entry. If it encounters a title, description, or link tag, hands
    // them
    // off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Law readLaw(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "srw_dc:dc");
        String urn = null;
        String locale = null;
        String authority = null;
        String title = null;
        String description = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("urn")) {
                urn = readUrn(parser);
            } else if (name.equals("localidade")) {
                locale = readLocale(parser);
            } else if (name.equals("autoridade")) {
                authority = readAuthority(parser);
            } else if (name.equals("dc:title")) {
                title = readTitle(parser);
            } else if (name.equals("dc:description")) {
                description = readDescription(parser);
            } else {
                skip(parser);
            }
        }
        return new Law(urn, locale, authority, title, description);
    }

    // Processes urn tags in the feed.
    private String readUrn(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "urn");
        String urn = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "urn");
        return urn;
    }

    // Processes locale tags in the feed.
    private String readLocale(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "localidade");
        String locale = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "localidade");
        return locale;
    }

    // Processes authority tags in the feed.
    private String readAuthority(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "autoridade");
        String authority = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "autoridade");
        return authority;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "dc:title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "dc:title");
        return title;
    }

    // Processes description tags in the feed.
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "dc:description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "dc:description");
        return description;
    }

    // For the tags, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
