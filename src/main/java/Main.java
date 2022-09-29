import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * 1. Enter arrays of products and prices and the path to the file for storing basket;
 * 2. Create an object of a product bin or restore previous purchases from the file;
 * 3. Show a list of products available for purchase;
 * 4. Scan product number and its quantity from console input;
 * 5. Store the purchase and the client log, creating the ClientLog if it doesn't exist;
 * 6. Display all purchases, their total cost and quantity
 */
public class Main {
    public static void main(String[] args) {
        String[] products = {"Молоко", "Хлеб", "Яблоки", "Сыр"};
        double[] prices = {100.00, 75.00, 110.00, 800.50};
        String path = "basket.json";

        File basketSource = new File(path);
        Basket basket;
        if (basketSource.exists()) {
            basket = loadBasket(path, basketSource);
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
        }

        saveBasket(basket, path, basketSource);
        File log = new File("log.csv");
        if (!(log.exists())) {
            new ClientLog(log);
        }
        ClientLog.exportAsCSV(log);

        basket.printCart();
    }

    /**
     * Loads the basket from the JSON, binary or text file
     */
    public static Basket loadBasket(String path, File file) {
        String[] splitPath = path.split("\\.");
        String extension = splitPath[1];
        if ("json".equals(extension)) {
            return Basket.loadFromJsonFile(file);
        } else if ("bin".equals(extension)) {
            return Basket.loadFromBinFile(file);
        } else {
            return Basket.loadFromTxtFile(file);
        }
    }

    /**
     * Writes(adds) purchases to the JSON, binary or text file
     */
    public static void saveBasket(Basket basket, String path, File file) {
        String[] splitPath = path.split("\\.");
        String extension = splitPath[1];
        if ("json".equals(extension)) {
            basket.saveJson(file);
        } else if ("bin".equals(extension)) {
            basket.saveBin(file);
        } else {
            basket.saveTxt(file);
        }
    }
}
