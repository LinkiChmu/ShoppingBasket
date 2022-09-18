import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Basket implements Serializable {
    private String[] products;
    private double[] prices;
    private Map<Integer, Integer> purchase = new LinkedHashMap<>();
    private static final long serialVersionUID = 29L;

    public Basket(String[] products, double[] prices) {
        this.products = products;
        this.prices = prices;
    }

    /**
     * Method writes all the Basket object's fields to the text file.
     */
    public void saveTxt(File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String product : products) {
                bw.write(product + " ");
            }
            bw.write("\n");

            Arrays.stream(prices)
                    .boxed()
                    .map(price -> price + " ")
                    .forEach(str -> {
                        try {
                            bw.write(str);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            bw.write("\n");

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
     * Method stores the object Basket into the binary file by serialization.
     */
    protected void saveBin(File file) {
        try (ObjectOutputStream objOut = new ObjectOutputStream(
                new DataOutputStream(new FileOutputStream(file)))) {
            objOut.writeObject(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Restores object Basket from the binary file by deserialization;
     * displays the restored shopping cart.
     */
    protected static Basket loadFromBinFile(File file) {
        Basket basket = null;
        try (ObjectInputStream objIn = new ObjectInputStream(
                new DataInputStream(new FileInputStream(file)))) {
            basket = (Basket) objIn.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        basket.printCart();
        System.out.println();
        return basket;
    }

    /**
     * Restores the shopping list from a text file;
     * displays the restored cart.
     */
    public static Basket loadFromTxtFile(File file) {
        Basket basket = null;
        // read the products array
        try (BufferedReader buf = new BufferedReader(new FileReader(file))) {
            String str = buf.readLine();
            String[] readProducts = str.split("(?U)\\W+");
            // make a new object
            basket = new Basket(readProducts, new double[readProducts.length]);
            // read the prices array
            String[] readPrices = buf.readLine().split(" ");
            for (int i = 0; i < readPrices.length; i++) {
                basket.prices[i] = Double.parseDouble(readPrices[i]);
            }
            // read the map of purchases
            String s;
            while ((s = buf.readLine()) != null) {
                String[] read = s.split("(?U)\\W+");
                basket.purchase.put(Integer.parseInt(read[0]), Integer.parseInt(read[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        basket.printCart();
        System.out.println();
        return basket;
    }

    /**
     * Adds a certain quantity of product to the cart;
     * if user adds the same product to the cart several times, it must be summed up.
     */
    public void addToCart(int productNum, int amount) {
        if (purchase.containsKey(productNum)) {
            int quantity = purchase.get(productNum) + amount;
            purchase.put(productNum, quantity);
        } else {
            purchase.put(productNum, amount);
        }
    }

    /**
     * Displays all purchases, their total cost and quantity (currency decimal format: 0.00).
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
