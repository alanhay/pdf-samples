package uk.co.certait.pdf.splitter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Test;

public class PdfSplitterIntegrationTest {

    private static final String SOURCE_PDF = "/integration-test-sample.pdf";
    private static final String[] DOCUMENT_KEYS = { "section_a", "section_b", "section_c", "section_d" };

    @Test
    public void testPdfSplit() throws IOException {
        Map<String, PDDocument> splitDocuments = new PdfSplitter().splitPdf(this.getClass().getResourceAsStream(SOURCE_PDF));

        assertEquals(4, splitDocuments.size());

        // section_a, section_b, section_c, section_d
        int[][] expectedPages = { { 6, 9, 18, 19 }, { 1, 2, 3, 5, 7, 9, 11, 13, 14, 16, 17, 18 }, { 2, 3, 4, 5, 7, 8, 10, 11, 12, 13, 14, 15, 20 }, { 3, 4, 5, 7, 8, 10, 14, 15, 20 } };
        int index = 0;

        for(String key : DOCUMENT_KEYS) {
            PDDocument document = splitDocuments.get(key);
            assertEquals(expectedPages[index].length, document.getNumberOfPages());
            ++index;
        }

        PDDocument sourceDocument = PDDocument.load(this.getClass().getResourceAsStream(SOURCE_PDF));

        for(int i = 0; i < expectedPages.length; ++i) {
            for(int j = 0; j < expectedPages[i].length; ++j) {
                PDFTextStripper sourceStripper = new PDFTextStripper();
                sourceStripper.setStartPage(expectedPages[i][j]);
                sourceStripper.setEndPage(expectedPages[i][j]);

                PDFTextStripper targetStripper = new PDFTextStripper();
                targetStripper.setStartPage(j + 1);
                targetStripper.setEndPage(j + 1);

                assertEquals(sourceStripper.getText(sourceDocument), targetStripper.getText(splitDocuments.get(DOCUMENT_KEYS[i])));
            }
        }

        sourceDocument.close();
    }
}
