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

        long start = System.currentTimeMillis();

        PDDocument document = PDDocument.load(path);
        Map<String, List<Integer>> metaData = extractSplitMetaData(document);
        Map<String, PDDocument> splitDocuments = new HashMap<>();
        PDFMergerUtility merger = new PDFMergerUtility();

        int pageIndex = 1;

        for(PDDocument page : new Splitter().split(document)) {
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
        
        document.close();
        
        System.out.println("Processing Time document (" + pageIndex + " pages): " + (System.currentTimeMillis() - start));

        return splitDocuments;
    }

    protected Map<String, List<Integer>> extractSplitMetaData(PDDocument document) throws IOException {

        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

        for(int i = 1; i <= document.getNumberOfPages(); ++i) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(i);
            stripper.setEndPage(i);

            String splitString = extractSplitString(stripper.getText(document));

            if(splitString != null) {
                for(String sectionIdentifier : extractSectionIdentifiers(splitString)) {

                    if(!map.containsKey(sectionIdentifier)) {
                        map.put(sectionIdentifier, new ArrayList<Integer>());
                    }

                    map.get(sectionIdentifier).add(i);
                }
            }
        }

        return map;
    }

    protected String extractSplitString(String string) {

        Matcher matcher = pattern.matcher(string);
        String part = null;

        if(matcher.find()) {
            part = matcher.group(0);
        }

        return part;
    }

    protected String[] extractSectionIdentifiers(String string) {

        String part = string.substring(string.indexOf("[") + 1, string.lastIndexOf("]"));

        return StringUtils.deleteWhitespace(part).split(",");
    }
}
