import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * 1. Create an object of a product bin or restore it from the binary file
 * 2. Show a list of products available for purchase;
 * 3. Scan product number and its quantity from console input;
 * 4. Store the purchase;
 * 5. Display all purchases, their total cost and quantity
 */
public class Main {
    public static void main(String[] args) {
        String[] products = {"Молоко", "Хлеб", "Яблоки", "Сыр"};
        double[] prices = {100.00, 75.00, 110.00, 800.50};

  //      File txtFile = new File("basket.txt");
        File binFile = new File("basket.bin");
        Basket basket;
        if (binFile.exists()) {
            basket = Basket.loadFromBinFile(binFile);
        } else {
            basket = new Basket(products, prices);
        }

        StringBuilder sb1 = new StringBuilder("Список товаров для покупки: \n");
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

   //     basket.saveTxt(txtFile);
        basket.saveBin(binFile);

        basket.printCart();
    }
}
