public class LogNote {
    private int productNum;
    private int amount;

    public LogNote(int productNum, int amount) {
        this.productNum = productNum;
        this.amount = amount;
    }

    public void displayNote() {
        System.out.println("товар номер " + (productNum + 1) + " - " + amount + " шт.");
    }

    @Override
    public String toString() {
        return productNum + "," + amount;
    }

    public int getProductNum() {
        return productNum;
    }

    public int getAmount() {
        return amount;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
