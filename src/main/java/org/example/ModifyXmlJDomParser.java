package org.example;

import org.jdom2.CDATA;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.XMLConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ModifyXmlJDomParser {

    private static final String FILENAME = "src/main/resources/invoice.xml";

    public static void main(String[] args) throws JDOMException, IOException {

        SAXBuilder sax = new SAXBuilder();

        // https://rules.sonarsource.com/java/RSPEC-2755
        // prevent xxe
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        Document doc = sax.build(new File(FILENAME));

        doc.setRootElement(doc.getRootElement().getChild("row").detach());

        Element rootNode = doc.getRootElement();

        rootNode.setName("order");



        Element orderInvoiceDetail = rootNode.getChild("orderInvoiceDetail");

        rootNode.addContent(orderInvoiceDetail.getChild("isActive").detach());

        Element restaurant = orderInvoiceDetail.getChild("restaurant");

        rootNode.addContent(orderInvoiceDetail.getChild("restaurant").detach());

        rootNode.addContent(orderInvoiceDetail.getChild("table").detach());

        rootNode.addContent(new Element("orderEntries"));

        Element orderEntries = rootNode.getChild("orderEntries");

        Element productList = rootNode.getChild("productList");

        List<Element> orderEntryList = productList.getChildren("orderEntry");

        int i = 1;

        for (Element product : orderEntryList) {
      Element orderEntry = new Element("orderEntry");
            orderEntry.addContent(new Element("id").addContent(String.valueOf(i)));
            orderEntry.addContent(product.getChild("quantity").detach());
            orderEntry.addContent(product.getChild("product").detach());
      //      orderEntry.addContent(restaurant.detach());
            orderEntries.addContent(orderEntry);
            i++;
        }



        rootNode.removeChild("total");

        rootNode.removeChild("orderInvoiceDetail");

        rootNode.removeChild("productList");


        // print to console for testing
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());

        // write to console
        xmlOutput.output(doc, System.out);

        // write to a file
       try (FileOutputStream output =
                     new FileOutputStream("orders.xml")) {
            xmlOutput.output(doc, output);
        }

    }

}