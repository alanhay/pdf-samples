package uk.co.certait.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;

import uk.co.certait.pdf.splitter.PdfSplitter;

public class PdfSplitRunner {

    public static void main(String[] args) throws IOException, COSVisitorException {

        PdfSplitter splitter = new PdfSplitter();
        Map<String, PDDocument> documents = splitter.splitPdf("c:/test-pdf/sample.pdf");

        for(String key : documents.keySet()) {
            FileOutputStream fos = new FileOutputStream(new File("c:/test-pdf/sample_part_" + key + ".pdf"));
            documents.get(key).save(fos);
            documents.get(key).close();
            fos.flush();
            fos.close();
        }
    }
}
