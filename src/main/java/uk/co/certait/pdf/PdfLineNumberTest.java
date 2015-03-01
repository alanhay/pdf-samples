package uk.co.certait.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

public class PdfLineNumberTest {

	private static final String[] words = { "Portable", "document", "legal", "office", "discombobulated", "experienced", "word",
			"circumlocution", "perfidiousness ", "in", "random", "superabundant ", "accoutrements " };

	public PdfLineNumberTest() throws Exception {
		String html = generateHTML("/templates/template1.vm");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		exportHtml(html, baos);

		FileOutputStream out = new FileOutputStream(new File("./sample1.pdf"));
		out.write(baos.toByteArray());
		out.flush();
		out.close();

		stampLineNumbers(baos.toByteArray());
	}

	public static void main(String[] args) throws Exception {
		new PdfLineNumberTest();
		System.exit(0);
	}

	public String generateHTML(String templateName) {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		Velocity.init(props);
		Template template = Velocity.getTemplate(templateName);

		VelocityContext context = new VelocityContext();
		context.put("paragraphs", generateData());
		Writer writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private List<String> generateData() {

		List<String> data = new ArrayList<>();

		for (int i = 0; i < new Random().nextInt(20) + 10; ++i) {
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < new Random().nextInt(150) + 100; ++j) {
				//builder.append(RandomStringUtils.randomAlphabetic(new Random().nextInt(10) + 1).toLowerCase()).append(" ");
				builder.append(words[new Random().nextInt(words.length)].toLowerCase()).append(" ");
			}

			data.add(builder.toString());
		}

		return data;
	}

	private void exportHtml(String html, OutputStream out) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(html.replaceAll("&nbsp;", "").getBytes()));

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(doc, null);

		renderer.layout();
		renderer.createPDF(out);
		out.flush();
		out.close();
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
