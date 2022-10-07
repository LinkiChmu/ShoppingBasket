import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Basket implements Serializable {
    private String[] products;
    private double[] prices;
    private Map<Integer, Integer> purchase;
    private static final long serialVersionUID = 29L;

    public Basket(String[] products, double[] prices) {
        this.products = products;
        this.prices = prices;
        purchase = new LinkedHashMap<>();
    }

    /**
     * Method writes the object Basket to the JSON file using Serialization.
     */
    public void saveJson(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new Gson();
            gson.toJson(this, writer);
        }
    }

    /**
     * Restores the object Basket from the JSON file;
     */
    public static Basket loadFromJsonFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Basket.class);
        }
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
        }
    }

    /**
     * Method stores the object Basket into the binary file by serialization.
     */
    public void saveBin(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new DataOutputStream(new FileOutputStream(file)))) {
            out.writeObject(this);
        }
    }

    /**
     * Restores object Basket from the binary file by deserialization;
     */
    public static Basket loadFromBinFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objIn = new ObjectInputStream(
                new DataInputStream(new FileInputStream(file)))) {
            return (Basket) objIn.readObject();
        }
    }

    /**
     * Restores the shopping list from a text file;
     */
    public static Basket loadFromTxtFile(File file) throws IOException {
        Basket basket;
        // read the products array
        try (BufferedReader buf = new BufferedReader(new FileReader(file))) {
            String str = buf.readLine();
            String[] readProducts = str.trim().split("(?U)\\W+");
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
        }
        return basket;
    }

    /**
     * Saves note to the client log;
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
        System.out.println();
    }
}
