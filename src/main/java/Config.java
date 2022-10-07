import com.opencsv.CSVWriter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.catalog.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class Config {

    protected String loadBasketEnabled;
    protected String loadBasketPath;
    protected String loadBasketFormat;

    protected String saveBasketEnabled;
    protected String saveBasketPath;
    protected String saveBasketFormat;

    protected String saveLogEnabled;
    protected String saveLogPath;

    public void loadSettings(String xmlFile) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.setEntityResolver(
                CatalogManager.catalogResolver(CatalogFeatures.defaults(),
                        Paths.get(xmlFile).toAbsolutePath().toUri()));
        Document doc = builder.parse(xmlFile);

        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath path = xpFactory.newXPath();
        loadBasketEnabled = path.evaluate("/config/load/enabled/text()", doc);
        loadBasketPath = path.evaluate("/config/load/fileName/text()", doc);
        loadBasketFormat = path.evaluate("/config/load/format/text()", doc);

        saveBasketEnabled = path.evaluate("/config/save/enabled/text()", doc);
        saveBasketPath = path.evaluate("/config/save/fileName/text()", doc);
        saveBasketFormat = path.evaluate("/config/save/format/text()", doc);

        saveLogEnabled = path.evaluate("/config/log/enabled/text()", doc);
        saveLogPath = path.evaluate("/config/log/fileName/text()", doc);
    }

    public Basket loadBasket() throws IOException {
        Basket basket = null;
        if (loadBasketEnabled.equals("true")) {
            if (loadBasketFormat.equals("json")) {
                basket = Basket.loadFromJsonFile(new File(loadBasketPath));
            } else {
                basket = Basket.loadFromTxtFile(new File(loadBasketPath));
            }
        }
        return basket;
    }

    public void saveBasket(Basket basket) throws IOException {
        if (saveBasketEnabled.equals("true")) {
            if (saveBasketFormat.equals("json")) {
                basket.saveJson(new File(saveBasketPath));
            } else {
                basket.saveTxt(new File(saveBasketPath));
            }
        }
    }

    public void saveLog(ClientLog clientLog) throws IOException {
        if (saveLogEnabled.equals("true")) {
            exportAsCSV(clientLog, new File(saveLogPath));
        }
    }

    /**
     * Writes client log to the CSV file.
     */
    public void exportAsCSV(ClientLog clientLog, File txtFile) throws IOException  {
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile, true))) {
            clientLog.activityLog.stream()
                    .map(Note::toString)
                    .map(str -> str.split(","))
                    .forEach(arr -> writer.writeNext(arr, false));
        }
    }

}
