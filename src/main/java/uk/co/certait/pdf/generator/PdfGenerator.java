package uk.co.certait.pdf.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.MathTool;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfGenerator {

	private static final String[] words = { "Portable", "document", "legal", "office", "discombobulated", "experienced", "word",
			"circumlocution", "perfidiousness ", "in", "random", "superabundant ", "accoutrements", "test", "Edinburgh", "city",
			"town", "address", "person", "thing", "computer", "count", "calculator" };
	
	public ByteArrayOutputStream generatePdf(int numberOfWords) throws Exception {
		String html = generateHTML("/templates/template1.vm", numberOfWords);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		exportHtml(html, baos);

		return baos;
	}

	public String generateHTML(String templateName, int numberOfWords) {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		Velocity.init(props);
		Template template = Velocity.getTemplate(templateName);

		VelocityContext context = new VelocityContext();
		context.put("paragraphs", generateData(numberOfWords));
		context.put("math", new MathTool());
		context.put("splitIdentifiers", new String [] {"a", "b", "c", "d", "a,b", "a,d", "c,d", "b,c", "b,c,d", "a,b,c,d"});
		Writer writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private List<String> generateData(int numberOfWords) {

		List<String> data = new ArrayList<>();
		
		int wordCount = 0;
		
		while(wordCount < numberOfWords) {
			for (int i = 0; i < new Random().nextInt(20) + 10; ++i) {
				StringBuilder builder = new StringBuilder();
				for (int j = 0; j < new Random().nextInt(150) + 100; ++j) {
					builder.append(words[new Random().nextInt(words.length)].toLowerCase()).append(" ");
					++ wordCount;
				}
	
				data.add(builder.toString());
			}
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
}
