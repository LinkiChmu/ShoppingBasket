import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * 1. Enter arrays of products and prices, read configure file;
 * 2. Create an object of a product bin and restore previous purchases from the JSON or text file if configured;
 * 3. Show a list of products available for purchase;
 * 4. Scan product number and its quantity from console input;
 * 5. Add the purchase to cart and customer history, writing the first line into the ClientLog if it doesn't exist;
 * 6. Write the shopping cart into the text or JSON file and the customer history into the CSV file if configured;
 * 7. Display all purchases, their total cost and quantity.
 */
public class Main {

    private static String loadBasketEnabled;
    private static String loadBasketPath;
    private static String loadBasketFormat;

    private static String saveBasketEnabled;
    private static String saveBasketPath;
    private static String saveBasketFormat;

    private static String saveLogEnabled;
    private static String saveLogPath;


    public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String[] products = {"Молоко", "Хлеб", "Яблоки", "Сыр"};
        double[] prices = {100.00, 75.00, 110.00, 800.50};
        loadSettings("shop.xml");

        Basket basket;
        if (loadBasketEnabled.equals("true")) {
            if (loadBasketFormat.equals("json")) {
                basket = Basket.loadFromJsonFile(new File(loadBasketPath));
            } else {
                basket = Basket.loadFromTxtFile(new File(loadBasketPath));
            }
        } else {
            basket = new Basket(products, prices);
        }

        StringBuilder sb1 = new StringBuilder("Список товаров, доступных для покупки: \n");
        DecimalFormat dfm = new DecimalFormat("0.00");
        for (int i = 0; i < products.length; i++) {
            sb1.append(i + 1);
            sb1.append(". ");
            sb1.append(products[i]);
            sb1.append(" ");
            sb1.append(dfm.format(prices[i]));
            sb1.append(" руб/шт\n");
        }
        System.out.println(sb1);

        Scanner scanner = new Scanner(System.in);
        int productNum;
        int productCount;
        ClientLog clientLog = new ClientLog();

        while (true) {
            System.out.println("Введите через пробел номер товара и количество или введите 'end'");
            String input = scanner.nextLine();
            if ("end".equals(input)) {
                break;
            }
            String[] parts = input.split("(?U)\\W+");
            if (parts.length != 2) {
                System.out.println("Некорректный ввод данных");
                continue;
            }
            try {
                productNum = Integer.parseInt(parts[0]) - 1;
                productCount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Следует вводить только числа: сначала - номер товара, затем - пробел и количество");
                continue;
            }
            if (productNum < 0 || productNum > products.length - 1) {
                System.out.println("Проверьте введенные числа: номер товара");
                continue;
            }
            basket.addToCart(productNum, productCount);

            if (!(new File(saveLogPath).exists())) {
                clientLog.firstLog(saveLogPath);
            }
            clientLog.log(productNum, productCount);
        }

        if (saveBasketEnabled.equals("true")) {
            if (saveBasketFormat.equals("json")) {
                basket.saveJson(new File(saveBasketPath));
            } else {
                basket.saveTxt(new File(saveBasketPath));
            }
        }

        if (saveLogEnabled.equals("true")) {
            clientLog.exportAsCSV(new File(saveLogPath));
        }
        basket.printCart();
    }

    private static void loadSettings(String xmlFile) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
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
}