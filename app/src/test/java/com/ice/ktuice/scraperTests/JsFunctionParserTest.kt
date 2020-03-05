package com.ice.ktuice.scraperTests

import com.ice.ktuice.al.services.scrapers.base.ktuScraperService.util.JsFunctionParser
import junit.framework.TestCase.assertEquals
import org.junit.Test

class JsFunctionParserTest {
    @Test
    fun testJsFunctionNameParsing(){
        val parser = JsFunctionParser("infivert(4738669,'2019P6141401','IicVnqt30l1SXOTrHHdj6g')")
        assertEquals("infivert", parser.getName())
        assertEquals("4738669", parser.getArgument(0))
        assertEquals("2019P6141401", parser.getArgument(1))
        assertEquals("IicVnqt30l1SXOTrHHdj6g", parser.getArgument(2))
    }

    @Test
    fun testLiteralArguments(){
        val parser = JsFunctionParser("infivert(4738669,'2019P6141401','IicVnqt30l1SXOTrHHdj6g')")
        assertEquals("4738669", parser.getArgument(0))
        assertEquals("2019P6141401", parser.getArgument(1))
        assertEquals("IicVnqt30l1SXOTrHHdj6g", parser.getArgument(2))
    }

    @Test
    fun testExpressionArgumentsStringAdd(){
        val parser = JsFunctionParser("infivert(624236,'2019P6140881','IicVnqt30l'+'0-t7fCRw1YUw')")
        assertEquals("624236", parser.getArgument(0))
        assertEquals("2019P6140881", parser.getArgument(1))
        assertEquals("IicVnqt30l0-t7fCRw1YUw", parser.getArgument(2))
    }
}