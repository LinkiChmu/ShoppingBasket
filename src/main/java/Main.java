import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

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