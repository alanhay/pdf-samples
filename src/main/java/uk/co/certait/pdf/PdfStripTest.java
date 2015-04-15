package uk.co.certait.pdf;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PdfStripTest{

	public static void main(String[] args) throws IOException {
		PDDocument document = PDDocument.load("./sample1.pdf");
		long start = System.currentTimeMillis();
		Pattern pattern = Pattern.compile("document_split_parts\\[.*?\\]");

		for(int i = 1; i <= document.getNumberOfPages(); ++ i) {
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(i);
			stripper.setEndPage(i);
			String text = stripper.getText(document);
			//System.out.println(text);
			Matcher matcher = pattern.matcher(text);
			
			while(matcher.find()) {
				System.out.println(i + ".\t" + matcher.group(0));
			}
		}
		
		System.out.println("\n\nProcessing Time: " + (System.currentTimeMillis() - start));
		
		System.exit(0);
	}
}
