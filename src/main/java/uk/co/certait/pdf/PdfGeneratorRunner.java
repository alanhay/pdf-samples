package uk.co.certait.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import uk.co.certait.pdf.generator.PdfGenerator;


public class PdfGeneratorRunner {
    
    public static void main(String[] args) throws Exception {
        PdfGenerator generator = new PdfGenerator();
        ByteArrayOutputStream baos = generator.generatePdf(113000, true);
        
        FileOutputStream fos = new FileOutputStream(new File("c:/test-pdf/sample.pdf"));
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();
    }
}
