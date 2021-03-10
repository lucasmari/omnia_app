package com.lucas.omnia.utils

import android.util.Xml
import com.lucas.omnia.models.Law
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

class LawsXmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(`in`: InputStream): MutableList<Law> {
        return `in`.use { `in` ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(`in`, null)
            parser.nextTag()
            readRecords(parser)
        } as MutableList<Law>
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRecords(parser: XmlPullParser): List<Law> {
        val laws: MutableList<Law> = ArrayList()
        parser.require(XmlPullParser.START_TAG, ns, "srw:searchRetrieveResponse")
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            when (parser.name) {
                "srw:records" -> {
                }
                "srw:record" -> {
                }
                "srw:recordData" -> {
                }
                "srw_dc:dc" -> {
                    laws.add(readLaw(parser))
                }
                else -> {
                    skip(parser)
                }
            }
        }
        return laws
    }

    // Parses the contents of an entry. If it encounters a title, description, or link tag, hands
    // them
    // off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readLaw(parser: XmlPullParser): Law {
        parser.require(XmlPullParser.START_TAG, ns, "srw_dc:dc")
        var urn: String? = null
        var locale: String? = null
        var authority: String? = null
        var title: String? = null
        var description: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "urn") {
                urn = readUrn(parser)
            } else if (name == "localidade") {
                locale = readLocale(parser)
            } else if (name == "autoridade") {
                authority = readAuthority(parser)
            } else if (name == "dc:title") {
                title = readTitle(parser)
            } else if (name == "dc:description") {
                description = readDescription(parser)
            } else {
                skip(parser)
            }
        }
        return Law(urn!!, locale!!, authority!!, title!!, description!!)
    }

    // Processes urn tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readUrn(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "urn")
        val urn = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "urn")
        return urn
    }

    // Processes locale tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLocale(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "localidade")
        val locale = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "localidade")
        return locale
    }

    // Processes authority tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAuthority(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "autoridade")
        val authority = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "autoridade")
        return authority
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "dc:title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "dc:title")
        return title
    }

    // Processes description tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "dc:description")
        val description = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "dc:description")
        return description
    }

    // For the tags, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    companion object {
        private val ns: String? = null
    }
}