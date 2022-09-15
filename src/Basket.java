import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Basket {
    private final String[] products;
    private final double[] prices;
    private Map<Integer, Integer> purchase = new HashMap<>();
    private static File textFile;

    public Basket(String[] products, double[] prices) {
        this.products = products;
        this.prices = prices;
        textFile = new File("basket.txt");
    }

    /**
     * Method writes the purchases to the text file.
     *
     * @throws IOException
     */
    private void saveTxt() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(textFile))) {
            purchase.entrySet().stream()
                    .map(entry -> entry.toString() + "\n")
                    .forEach(str -> {
                        try {
                            bw.write(str);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Restore the shopping list from a text file in which it was previously saved.
     */
    protected static Basket loadFromTxtFile(Basket basket) {
        if (textFile.exists()) {
            try (BufferedReader buf = new BufferedReader(new FileReader(textFile))) {
                String s;
                while ((s = buf.readLine()) != null) {
                    String[] read = s.split("(?U)\\W+");
                    basket.purchase.put(Integer.parseInt(read[0]), Integer.parseInt(read[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return basket;
    }

    /**
     * Add a certain quantity of product to the shopping cart.
      *
     * @param productNum
     * @param amount
     * @throws IOException
     */
    public void addToCart(int productNum, int amount) throws IOException {
        if (purchase.containsKey(productNum)) {
            int quantity = purchase.get(productNum) + amount;
            purchase.put(productNum, quantity);
        } else {
            purchase.put(productNum, amount);
        }
        saveTxt();
    }

    /**
     * Display all purchases, their total cost and quantity (currency decimal format: 0.00).
     */
    public void printCart() {
        StringBuilder sb = new StringBuilder("Ваша корзина:\n");
        double totalSum = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        for (Map.Entry<Integer, Integer> entry : purchase.entrySet()) {
            int productNum = entry.getKey();
            int quantity = entry.getValue();
            if (quantity != 0) {
                double price = prices[productNum];
                double sum = quantity * price;
                sb.append(products[productNum]);
                sb.append(" ");
                sb.append(quantity);
                sb.append(" шт ");
                sb.append(df.format(price));
                sb.append(" руб/шт ");
                sb.append(df.format(sum));
                sb.append(" руб в сумме\n");
                totalSum += sum;
            }
        }
        sb.append("Итого: ");
        sb.append(df.format(totalSum));
        sb.append(" руб");
        System.out.println(sb);
    }
}
