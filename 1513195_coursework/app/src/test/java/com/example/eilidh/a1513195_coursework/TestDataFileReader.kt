package com.example.eilidh.a1513195_coursework


import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import java.io.InputStream
import java.util.Scanner

object TestDataFileReader {

    /**
     * Reads test data from a file, located at app/src/test/resources/testData.json
     * and returns the contents as a String.
     * @return The content of sitelist.json file, or null if
     */
    fun readTestDataFile(): String? {
        // Create an input stream to the file in resources/sitelist.json
        val inputStream = TestDataFileReader::class.java.classLoader.getResourceAsStream("sitelist.json")!!

        // check that the strea has been created

        // open a Scanner on the input stream until the end of the stream
        val s = Scanner(inputStream).useDelimiter("\\A")

        // if s.hasNext() returns true, some text was read from the stream,
        return if (s.hasNext()) {
            // return the text that was read from the stream
            s.next()
        } else {
            // nothing was read from the stream, so return null.
            null
        }
    }

}