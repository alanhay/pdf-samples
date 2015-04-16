package uk.co.certait.pdf.splitter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.pdfbox.util.Splitter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnitParamsRunner.class)
public class PdfSplitterTest {

    private PdfSplitter pdfSplitter;

    @Mock
    private Splitter splitter;

    @Mock
    private PDFMergerUtility merger;

    @Before
    public void setUp() {
        pdfSplitter = new PdfSplitter();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSplitDocuments() throws IOException {
        PDDocument document = new PDDocument();
        
        PDDocument [] pages = {new PDDocument(), new PDDocument(), new PDDocument(), new PDDocument(), new PDDocument(), new PDDocument()};
        
        //mock splitter
        when(splitter.split(document)).thenReturn(Arrays.asList(pages));

        Map<String, List<Integer>> metaData = new HashMap<>();
        metaData.put("section_a", Arrays.asList(1, 5, 6));
        metaData.put("section_b", Arrays.asList(3, 4, 5, 6));
        metaData.put("section_c", Arrays.asList(6));
        
        
        Map<String, PDDocument> documents = pdfSplitter.splitDocument(document, splitter, merger, metaData);
        assertEquals(3, documents.size());
        
        //cannot test with actual page but assume covered by below
        verify(merger, times(1)).appendDocument(any(PDDocument.class), eq(pages[0])); 
        verify(merger, times(0)).appendDocument(any(PDDocument.class), eq(pages[1])); 
        verify(merger, times(1)).appendDocument(any(PDDocument.class), eq(pages[2])); 
        verify(merger, times(1)).appendDocument(any(PDDocument.class), eq(pages[3])); 
        verify(merger, times(2)).appendDocument(any(PDDocument.class), eq(pages[4])); 
        verify(merger, times(3)).appendDocument(any(PDDocument.class), eq(pages[5])); 
    }

    @Test
    @Parameters
    public void testExtractDocumentSplitMetaData(PDDocument document, final String[] pageText, Map<String, List<Integer>> expected) throws IOException {

        pdfSplitter = new PdfSplitter() {
            @Override
            protected String getTextForPage(PDDocument document, int pageNumber) throws IOException {
                return pageText[pageNumber - 1];
            }
        };

        assertEquals(expected, pdfSplitter.extractDocumentSplitMetaData(document));
    }

    @Test
    @Parameters
    public void testExtractMetaDataString(String input, String expected) {
        assertEquals(expected, pdfSplitter.extractMetaDataString(input));
    }

    @Test
    @Parameters
    public void testExtractSectionIdentifiers(String input, String[] expected) {
        assertArrayEquals(expected, pdfSplitter.extractSectionIdentifiers(input));
    }

    @SuppressWarnings("unused")
    private Object[] parametersForTestExtractDocumentSplitMetaData() {
        return new Object[] { new Object[] { createMockDocument(10), createPageContent(10, "section_a", "section_b"), createResultMap(10, "section_a", "section_b") } };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForTestExtractMetaDataString() {
        return new Object[] { new Object[] { "xyzyzyzyzyzyz" + PdfSplitter.SPLIT_STRING_IDENTIFIER + "[a,b]bbbnmbnmbnmbnmbnmbnmbnmbnmb", PdfSplitter.SPLIT_STRING_IDENTIFIER + "[a,b]" },
                new Object[] { "hjklahjkhjkhjkhjkh", null } };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForTestExtractSectionIdentifiers() {
        return new Object[] { new Object[] { "[a]", new String[] { "a" } }, new Object[] { "[a,b]", new String[] { "a", "b" } },
                new Object[] { "[section1, section2]", new String[] { "section1", "section2" } },
                new Object[] { "[section1, section2,   section 3]", new String[] { "section1", "section2", "section3" } } };
    }

    private String[] createPageContent(int numberOfPages, String... identifiers) {

        String[] pageContent = new String[numberOfPages];

        for(int i = 0; i < numberOfPages; ++i) {
            StringBuilder builder = new StringBuilder();
            builder.append(PdfSplitter.SPLIT_STRING_IDENTIFIER);
            builder.append("[");

            int index = 0;

            for(String s : identifiers) {
                builder.append(s);
                builder.append(index < identifiers.length ? "," : "");
                ++index;
            }

            builder.append("]");
            builder.append(RandomStringUtils.randomAlphabetic(100));

            pageContent[i] = builder.toString();
        }

        return pageContent;
    }

    private Map<String, List<Integer>> createResultMap(int pageCount, String... identifiers) {
        Map<String, List<Integer>> map = new HashMap<>();

        for(String s : identifiers) {
            map.put(s, new ArrayList<Integer>());

            for(int i = 1; i <= pageCount; ++i) {
                map.get(s).add(i);
            }
        }

        return map;
    }

    private PDDocument createMockDocument(final int numberOfPages) {
        PDDocument document = new PDDocument() {
            public int getNumberOfPages() {
                return numberOfPages;
            }
        };

        return document;
    }
}
