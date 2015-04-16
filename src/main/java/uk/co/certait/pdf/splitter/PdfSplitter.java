package uk.co.certait.pdf.splitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;

public class PdfSplitter {

    protected static final String SPLIT_STRING_IDENTIFIER = "document_split_parts";

    Pattern pattern;

    public PdfSplitter() {
        pattern = Pattern.compile(SPLIT_STRING_IDENTIFIER + "\\[.*?\\]");
    }

    public Map<String, PDDocument> splitPdf(String path) throws IOException, COSVisitorException {

        PDDocument document = PDDocument.load(path);
        Map<String, List<Integer>> metaData = extractDocumentSplitMetaData(document);
        Map<String, PDDocument> documents = splitDocument(document, new Splitter(), new PDFMergerUtility(), metaData);
        document.close();

        return documents;
    }

    protected Map<String, PDDocument> splitDocument(PDDocument document, Splitter splitter, PDFMergerUtility merger, Map<String, List<Integer>> metaData) throws IOException {

        Map<String, PDDocument> splitDocuments = new HashMap<>();
        int pageIndex = 1;

        for(PDDocument page : splitter.split(document)) {
            for(String key : metaData.keySet()) {
                if(metaData.get(key).contains(pageIndex)) {
                    if(!splitDocuments.containsKey(key)) {
                        splitDocuments.put(key, new PDDocument());
                    }

                    merger.appendDocument(splitDocuments.get(key), page);
                }
            }

            ++pageIndex;
            page.close();
        }

        return splitDocuments;
    }

    protected Map<String, List<Integer>> extractDocumentSplitMetaData(PDDocument document) throws IOException {

        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

        for(int i = 1; i <= document.getNumberOfPages(); ++i) {
            String text = getTextForPage(document, i);
            String metaData = extractMetaDataString(text);

            if(metaData != null) {
                for(String sectionIdentifier : extractSectionIdentifiers(metaData)) {
                    if(!map.containsKey(sectionIdentifier)) {
                        map.put(sectionIdentifier, new ArrayList<Integer>());
                    }

                    map.get(sectionIdentifier).add(i);
                }
            }
        }

        return map;
    }

    protected String getTextForPage(PDDocument document, int pageNumber) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(pageNumber);
        stripper.setEndPage(pageNumber);

        return stripper.getText(document);
    }

    protected String extractMetaDataString(String string) {

        Matcher matcher = pattern.matcher(string);
        String part = null;

        if(matcher.find()) {
            part = matcher.group(0);
        }

        return part;
    }

    protected String[] extractSectionIdentifiers(String string) {
        return StringUtils.deleteWhitespace(string.substring(string.indexOf("[") + 1, string.lastIndexOf("]"))).split(",");
    }
}
