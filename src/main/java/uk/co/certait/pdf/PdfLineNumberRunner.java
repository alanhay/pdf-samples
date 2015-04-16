package uk.co.certait.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.certait.pdf.generator.PdfGenerator;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

public class PdfLineNumberRunner {
	public PdfLineNumberRunner() throws Exception {

		FileOutputStream out = new FileOutputStream(new File("./sample1.pdf"));
		ByteArrayOutputStream baos = new PdfGenerator().generatePdf(80000, true);
		out.write(baos.toByteArray());
		out.flush();
		out.close();

		stampLineNumbers(baos.toByteArray());
	}

	public static void main(String[] args) throws Exception {
		new PdfLineNumberRunner();
		System.exit(0);
	}

	private void stampLineNumbers(byte[] pdfData) throws IOException, DocumentException {
		PdfReader reader = new PdfReader(pdfData);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(new File("./sample2.pdf")));

		PdfReader reader1 = new PdfReader(new FileInputStream(new File("c:/report/lineNumbers.pdf")));
		PdfWriter writer = stamper.getWriter();
		PdfImportedPage page = writer.getImportedPage(reader1, 1);

		for (int i = 1; i < reader.getNumberOfPages() + 1; ++i) {
			PdfContentByte cb = stamper.getUnderContent(i);
			cb.addTemplate(page, 1f, 0, 0, 1, 0, 0);
		}

		stamper.close();
	}
}
