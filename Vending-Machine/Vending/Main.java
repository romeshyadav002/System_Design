import java.util.HashMap;
import java.util.Map;

public class Main {
    public static class Product {
        String name;
        double price;

        Product(String name, double Price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return this.name;
        }

        public double getPrice() {
            return this.price;
        }
    }

    public static class Inventory {
        Map<Product, Integer> products;

        public Inventory() {
            products = new HashMap<>();
        }

        public void addProduct(Product product, int qty) {
            products.put(product, qty);
        }

        public void removeProduct(Product product) {
            products.remove(product);
        }

        public void updateQuantity(Product product, int qty) {
            products.put(product, qty);
        }

        public int getQuantity(Product product) {
            return products.get(product);
        }

        public boolean isAvailable(Product product) {
            return products.containsKey(product) && products.get(product) > 0;
        }
    }

    public static enum Coin {
        PENNY(0.001),
        NICKEL(0.05),
        DIME(0.1),
        QUARTER(0.25);

        public double value;

        Coin(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    public static enum Note {
        ONE(1),
        FIVE(5),
        TEN(10),
        TWENTY(20);

        private final int value;

        Note(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static interface VendingMachineState {
        void selectProduct(Product product);

        void insertCoin(Coin coin);

        void insertNote(Note node);

        void dispenseProduct();

        void returnChange();
    }

    public static class IdleState implements VendingMachineState {
        private final VendingMachine vendingMachine;

        public IdleState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Product product) {
            if (vendingMachine.getInventory().isAvailable(product)) {
                System.out.println("Product selected: " + product.getName());
                vendingMachine.setSelectedProduct(product);
                vendingMachine.setState(vendingMachine.getReadyState());
            } else {
                System.out.println("Product not available: " + product.getName());
            }
        }

        @Override
        public void insertCoin(Coin coin) {
            System.out.println("Please select a product first.");
        }

        @Override
        public void insertNote(Note note) {
            System.out.println("Please select a product first.");
        }

        @Override
        public void dispenseProduct() {
            System.out.println("Please select a product and make payment.");
        }

        @Override
        public void returnChange() {
            System.out.println("No change to return.");
        }
    }

    public static class ReadyState implements VendingMachineState {
        private final VendingMachine vendingMachine;

        public ReadyState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Product product) {
            System.out.println("Product already selected. Please make payment.");
        }

        @Override
        public void insertCoin(Coin coin) {
            vendingMachine.addCoin(coin);
            System.out.println("Coin inserted: " + coin);
            checkPaymentStatus();
        }

        @Override
        public void insertNote(Note note) {
            vendingMachine.addNote(note);
            System.out.println("Note inserted: " + note);
            checkPaymentStatus();
        }

        @Override
        public void dispenseProduct() {
            System.out.println("Please make payment first.");
        }

        @Override
        public void returnChange() {
            System.out.println("Please make payment first.");
        }

        private void checkPaymentStatus() {
            if (vendingMachine.getTotalPayment() >= vendingMachine.getSelectedProduct().getPrice()) {
                vendingMachine.setState(vendingMachine.getDispenseState());
            }
        }
    }

    public static class ReturnChangeState implements VendingMachineState {
        private final VendingMachine vendingMachine;

        public ReturnChangeState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Product product) {
            System.out.println("Please collect the change first.");
        }

        @Override
        public void insertCoin(Coin coin) {
            System.out.println("Please collect the change first.");
        }

        @Override
        public void insertNote(Note note) {
            System.out.println("Please collect the change first.");
        }

        @Override
        public void dispenseProduct() {
            System.out.println("Product already dispensed. Please collect the change.");
        }

        @Override
        public void returnChange() {
            double change = vendingMachine.getTotalPayment() - vendingMachine.getSelectedProduct().getPrice();
            if (change > 0) {
                System.out.println("Change returned: $" + change);
            } else {
                System.out.println("No change to return.");
            }

            vendingMachine.resetPayment();
            vendingMachine.resetSelectedProduct();
            vendingMachine.setState(vendingMachine.getIdleState());
        }
    }

    public static class DispenseState implements VendingMachineState {
        private final VendingMachine vendingMachine;

        public DispenseState(VendingMachine vendingMachine) {
            this.vendingMachine = vendingMachine;
        }

        @Override
        public void selectProduct(Product product) {
            System.out.println("Product already selected. Please collect the dispensed product.");
        }

        @Override
        public void insertCoin(Coin coin) {
            System.out.println("Payment already made. Please collect the dispensed product.");
        }

        @Override
        public void insertNote(Note note) {
            System.out.println("Payment already made. Please collect the dispensed product.");
        }

        @Override
        public void dispenseProduct() {
            Product product = vendingMachine.getSelectedProduct();
            vendingMachine.getInventory().updateQuantity(product,
                    vendingMachine.getInventory().getQuantity(product) - 1);
            System.out.println("Product dispensed: " + product.getName());
            vendingMachine.setState(vendingMachine.getReturnChangeState()); // Change the state to ReturnChangeState
        }

        @Override
        public void returnChange() {
            System.out.println("Please collect the dispensed product first.");
        }
    }

    public static class VendingMachine {
        Inventory inventory;
        VendingMachineState idleState;
        VendingMachineState readyState;
        VendingMachineState dispenseState;
        VendingMachineState returnChangeState;
        VendingMachineState currentState;
        Product selectedProduct;
        double totalPayment;

        public VendingMachine() {
            inventory = new Inventory();
            idleState = new IdleState(this);
            readyState = new ReadyState(this);
            dispenseState = new DispenseState(this);
            returnChangeState = new ReturnChangeState(this);
            currentState = idleState;
            selectedProduct = null;
            totalPayment = 0.0;

        }

        public Product addProduct(String name, double price, int qty) {
            Product product = new Product(name, price);
            inventory.addProduct(product, qty);
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

        public void dispenseProduct() {
            currentState.dispenseProduct();
        }

        public void returnChange() {
            currentState.returnChange();
        }

        void setState(VendingMachineState state) {
            currentState = state;
        }

        Inventory getInventory() {
            return inventory;
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

        Product getSelectedProduct() {
            return selectedProduct;
        }

        void setSelectedProduct(Product product) {
            selectedProduct = product;
        }

        void resetSelectedProduct() {
            selectedProduct = null;
        }

        double getTotalPayment() {
            return totalPayment;
        }

        void addCoin(Coin coin) {
            totalPayment += coin.getValue();
        }

        void addNote(Note note) {
            totalPayment += note.getValue();
        }

        void resetPayment() {
            totalPayment = 0.0;
        }
    }

    public static void main(String[] args) {
        VendingMachine vendingMachine = new VendingMachine();

        Product coke = vendingMachine.addProduct("Coke", 1.5, 3);
        Product pepsi = vendingMachine.addProduct("Pepsi", 1.5, 2);
        Product water = vendingMachine.addProduct("Water", 1.0, 5);

        vendingMachine.selectProduct(coke);

        vendingMachine.insertCoin(Coin.QUARTER);
        vendingMachine.insertCoin(Coin.QUARTER);
        vendingMachine.insertCoin(Coin.QUARTER);
        vendingMachine.insertCoin(Coin.QUARTER);

        vendingMachine.insertNote(Note.FIVE);

        // Dispense the product
        vendingMachine.dispenseProduct();

        // Return change
        vendingMachine.returnChange();

    }
}
