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
 * 1. Entered arrays of products and prices, load settings from the configure file;
 * 2. Create an object of a product bin and restore previous purchases from the JSON or text file if configured;
 * 3. Show a list of products available for purchase;
 * 4. Scan product number and its quantity from console input;
 * 5. Add the purchase to cart and customer history, writing the first line into the ClientLog if it doesn't exist;
 * 6. Write the shopping cart into the text or JSON file and the customer history into the CSV file if configured;
 * 7. Display all purchases, their total cost and quantity.
 */
public class Main {

    public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String[] products = {"Молоко", "Хлеб", "Яблоки", "Сыр"};
        double[] prices = {100.00, 75.00, 110.00, 800.50};
        Config config = new Config();
        config.loadSettings("shop.xml");
        ClientLog clientLog = new ClientLog();
        if (!(new File(config.saveLogPath).exists())) {
            clientLog.firstLog(config.saveLogPath);
        }


        Basket basket = config.loadBasket();
        if (basket == null) {
            basket = new Basket(products, prices);
        } else {
            basket.printCart();
        }

        basket.printProducts();

        Scanner scanner = new Scanner(System.in);
        int productNum;
        int productCount;

        while (true) {
            System.out.println("Введите через пробел номер товара и количество или введите 'end'");
            String input = scanner.nextLine();
            if ("end".equals(input)) {
                break;
            }
            String[] parts = input.trim().split(" ");
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

            clientLog.log(productNum, productCount);
        }
        config.saveBasket(basket);
        config.saveLog(clientLog);
        basket.printCart();
    }

}