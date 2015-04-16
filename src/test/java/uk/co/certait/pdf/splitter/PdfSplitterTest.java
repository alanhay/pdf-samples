package uk.co.certait.pdf.splitter;

import static org.junit.Assert.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.certait.pdf.splitter.PdfSplitter;

@RunWith(JUnitParamsRunner.class)
public class PdfSplitterTest {

    private PdfSplitter splitter;

    @Before
    public void setUp() {
        splitter = new PdfSplitter();
    }

    @Test
    public void testExtractSplitMetaData() {
        assertTrue(false);
    }

    @Test
    @Parameters
    public void testExtractSplitString(String input, String expected) {
        assertEquals(expected, splitter.extractSplitString(input));
    }

    @Test
    @Parameters
    public void testExtractSectionIdentifiers(String input, String[] expected) {
        assertArrayEquals(expected, splitter.extractSectionIdentifiers(input));
    }

    @SuppressWarnings("unused")
    private Object[] parametersForTestExtractSplitString() {
        return new Object[] { new Object[] { "xyzyzyzyzyzyz" + PdfSplitter.SPLIT_STRING_IDENTIFIER + "[a,b]bbbnmbnmbnmbnmbnmbnmbnmbnmb", PdfSplitter.SPLIT_STRING_IDENTIFIER + "[a,b]" },
                new Object[] { "hjklahjkhjkhjkhjkh", null } };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForTestExtractSectionIdentifiers() {
        return new Object[] { new Object[] { "[a]", new String[] { "a" } }, new Object[] { "[a,b]", new String[] { "a", "b" } },
                new Object[] { "[section1, section2]", new String[] { "section1", "section2" } },
                new Object[] { "[section1, section2,   section 3]", new String[] { "section1", "section2", "section3" } } };
    }

}
