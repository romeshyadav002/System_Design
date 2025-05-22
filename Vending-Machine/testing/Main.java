import java.util.HashMap;

public class Main {
    public static class Product {
        String name;
        double price;

        Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public double getPrice() {
            return this.price;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class Inventory {
        HashMap<Product, Integer> products;

        Inventory() {
            products = new HashMap<>();
        }

        public void addProduct(Product product, int qty) {
            products.put(product, qty);
        }

        public boolean isAvailable(Product product) {
            return products.containsKey(product) && products.get(product) > 0;
        }

        public int getQuantity(Product product) {
            return products.get(product);
        }

        public void updateQuantity(Product product, int qty) {
            products.put(product, qty);
        }
    }

    public static enum Coin {
        ONE(1),
        Two(2),
        FIVE(5),
        TEN(10);

        public double value;

        Coin(double value) {
            this.value = value;
        }

        public double getValue() {
            return this.value;
        }
    }

    public static enum Note {
        TWENTY(1),
        FIFTY(2),
        HUNDRED(5);

        public double value;

        Note(double value) {
            this.value = value;
        }

        public double getValue() {
            return this.value;
        }
    }

    public static interface VendingMachineState {
        public void selectProduct(Product product);

        public void insertCoin(Coin coin);

        public void insertNote(Note note);

        public void dispenseProduct();

        public void returnChange();
    }

    public static class IdleState implements VendingMachineState {
        VendingMachine vendingMachine;

        public IdleState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Main.Product product) {
            if (vendingMachine.getInventory().isAvailable(product)) {
                System.out.println("Product selected: " + product.getName());
                vendingMachine.setSelectedProduct(product);
                vendingMachine.setState(vendingMachine.getReadyState());
            } else {
                System.out.println("Product not available: " + product.getName());
            }
        }

        @Override
        public void insertCoin(Main.Coin coin) {
            System.out.println("Not relevant");
        }

        @Override
        public void insertNote(Main.Note note) {
            System.out.println("Not relevant");
        }

        @Override
        public void dispenseProduct() {
            System.out.println("Not relevant");
        }

        @Override
        public void returnChange() {
            System.out.println("Not relevant");
        }

    }

    public static class ReadyState implements VendingMachineState {
        VendingMachine vendingMachine;

        public ReadyState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Main.Product product) {
            System.out.println("Not relevant");
        }

        @Override
        public void insertCoin(Main.Coin coin) {
            vendingMachine.addCoin(coin);
            checkPaymentStatus();
        }

        @Override
        public void insertNote(Main.Note note) {
            vendingMachine.addNote(note);
            checkPaymentStatus();
        }

        @Override
        public void dispenseProduct() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'dispenseProduct'");
        }

        @Override
        public void returnChange() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'returnChange'");
        }

        public void checkPaymentStatus() {
            if (vendingMachine.totalPayment >= vendingMachine.getSelectedProduct().getPrice()) {
                vendingMachine.setState(vendingMachine.getDispenseState());
            }
        }

    }

    public static class DispenseState implements VendingMachineState {
        VendingMachine vendingMachine;

        public DispenseState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Main.Product product) {
            System.out.println("Not relevant");
        }

        @Override
        public void insertCoin(Main.Coin coin) {
            System.out.println("Not relevant");
        }

        @Override
        public void insertNote(Main.Note note) {
            System.out.println("Not relevant");
        }

        @Override
        public void dispenseProduct() {
            Product product = vendingMachine.getSelectedProduct();
            vendingMachine.getInventory().updateQuantity(product,
                    vendingMachine.getInventory().getQuantity(product) - 1);
            System.out.println("Product dispensed: " + product.getName());
            vendingMachine.setState(vendingMachine.getReturnChangeState());
        }

        @Override
        public void returnChange() {
            System.out.println("Not relevant");
        }

    }

    public static class ReturnChangeState implements VendingMachineState {
        VendingMachine vendingMachine;

        public ReturnChangeState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Main.Product product) {

            System.out.println("Not relevant");
        }

        @Override
        public void insertCoin(Main.Coin coin) {
            System.out.println("Not relevant");
        }

        @Override
        public void insertNote(Main.Note note) {
            System.out.println("Not relevant");
        }

        @Override
        public void dispenseProduct() {
            System.out.println("Not relevant");
        }

        @Override
        public void returnChange() {
            Product product = vendingMachine.getSelectedProduct();
            double change = vendingMachine.getTotalPayment() - product.getPrice();
            if (change > 0) {
                System.out.println("returned amount " + change);
            } else {
                System.out.println("No change");
            }
            vendingMachine.resetPayment();
            vendingMachine.resetSelectedProduct();
            vendingMachine.setState(vendingMachine.getIdleState());
        }

    }

    public static class VendingMachine {
        VendingMachineState currentState;
        Inventory inventory;
        Product selectedProduct;
        double totalPayment;
        VendingMachineState idleState;
        VendingMachineState dispenseState;
        VendingMachineState readyState;
        VendingMachineState returnChangeState;

        VendingMachine() {
            idleState = new IdleState(this);
            readyState = new ReadyState(this);
            dispenseState = new DispenseState(this);
            returnChangeState = new ReturnChangeState(this);
            currentState = idleState;
            inventory = new Inventory();
            selectedProduct = null;
            totalPayment = 0;
        }

        public Product addProduct(String name, double price, int qty) {
            Product product = new Product(name, price);
            this.inventory.addProduct(product, qty);
            return product;
        }

        public void selectProduct(Product product) {
            currentState.selectProduct(product);
        }

        public void insertCoin(Coin coin) {
            currentState.insertCoin(coin);
        }

        public void insertNote(Note note) {
            currentState.insertNote(note);
        }

        public void setSelectedProduct(Product product) {
            selectedProduct = product;
        }

        public Product getSelectedProduct() {
            return this.selectedProduct;
        }

        public void setState(VendingMachineState state) {
            currentState = state;
        }

        public Inventory getInventory() {
            return this.inventory;
        }

        public void addCoin(Coin coin) {
            totalPayment = totalPayment + coin.getValue();
        }

        public void addNote(Note note) {
            totalPayment = totalPayment + note.getValue();
        }

        public double getTotalPayment() {
            return this.totalPayment;
        }

        public void resetPayment() {
            this.totalPayment = 0;
        }

        public void resetSelectedProduct() {
            this.selectedProduct = null;
        }

        VendingMachineState getIdleState() {
            return idleState;
        }

        VendingMachineState getReadyState() {
            return readyState;
        }

        VendingMachineState getDispenseState() {
            return dispenseState;
        }

        VendingMachineState getReturnChangeState() {
            return returnChangeState;
        }
        public void dispenseProduct() {
            currentState.dispenseProduct();
        }
        public void returnChange() {
            currentState.returnChange();
        }


    }

    public static void main(String[] args) {
        VendingMachine vendingMachine = new VendingMachine();

        Product coke = vendingMachine.addProduct("Coke", 1.5, 3);
        Product pepsi = vendingMachine.addProduct("Pepsi", 1.5, 2);
        Product water = vendingMachine.addProduct("Water", 1.0, 5);

        vendingMachine.selectProduct(coke);

        vendingMachine.insertCoin(Coin.ONE);
        vendingMachine.insertCoin(Coin.Two);
        vendingMachine.insertCoin(Coin.FIVE);

        vendingMachine.insertNote(Note.TWENTY);

        // Dispense the product
        vendingMachine.dispenseProduct();

        // Return change
        vendingMachine.returnChange();

    }
}
