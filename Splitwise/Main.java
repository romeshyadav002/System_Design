package Splitwise;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static class User {
        private final String id;
        private final String name;
        private final String email;
        private final Map<String, Double> balances;

        public User(String name, String email) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.email = email;
            this.balances = new ConcurrentHashMap<>();
        }

        public String getId() { return this.id; }
        public String getName() { return this.name; }
        public String getEmail() { return email; }
        public Map<String, Double> getBalances() { return balances; }
    }

    public static class Group {
        private final String id;
        private final String name;
        private final List<User> members;
        private final List<Expense> expenses;

        public Group(String name) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.members = new CopyOnWriteArrayList<>();
            this.expenses = new CopyOnWriteArrayList<>();
        }

        public void addMember(User user) { members.add(user); }
        public void addExpense(Expense expense) { expenses.add(expense); }

        public String getId() { return id; }
        public String getName() { return name; }
        public List<User> getMembers() { return members; }
        public List<Expense> getExpenses() { return expenses; }
    }

    public static abstract class Split {
        protected User user;
        protected double amount;

        public Split(User user) {
            this.user = user;
        }

        public abstract double getAmount();

        public void setAmount(double amount) { this.amount = amount; }

        public User getUser() { return user; }
    }

    public static class EqualSplit extends Split {
        public EqualSplit(User user) {
            super(user);
        }

        @Override
        public double getAmount() {
            return amount;
        }
    }

    public static class ExactSplit extends Split {
        public ExactSplit(User user, double amount) {
            super(user);
            this.amount = amount;
        }

        @Override
        public double getAmount() {
            return amount;
        }
    }

    public static class PercentSplit extends Split {
        private final double percent;

        public PercentSplit(User user, double percent) {
            super(user);
            this.percent = percent;
        }

        @Override
        public double getAmount() {
            return amount;
        }

        public double getPercent() {
            return percent;
        }
    }

    public static class Expense {
        private final String id;
        private final double amount;
        private final String description;
        private final User paidBy;
        private final List<Split> splits;

        public Expense(String id, double amount, String description, User paidBy) {
            this.id = id;
            this.amount = amount;
            this.description = description;
            this.paidBy = paidBy;
            this.splits = new ArrayList<>();
        }

        public void addSplit(Split split) { splits.add(split); }
        public String getId() { return id; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }
        public User getPaidBy() { return paidBy; }
        public List<Split> getSplits() { return splits; }
    }

    public static class Transaction {
        private final String id;
        private final User sender;
        private final User receiver;
        private final double amount;

        public Transaction(String id, User sender, User receiver, double amount) {
            this.id = id;
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
        }
    }

    public static class SplitwiseService {
        private static SplitwiseService instance;
        private final Map<String, User> users;
        private final Map<String, Group> groups;

        private static final String TRANSACTION_ID_PREFIX = "TXN";
        private static final AtomicInteger transactionCounter = new AtomicInteger(0);

        private SplitwiseService() {
            users = new ConcurrentHashMap<>();
            groups = new ConcurrentHashMap<>();
        }

        public static synchronized SplitwiseService getInstance() {
            if (instance == null) {
                instance = new SplitwiseService();
            }
            return instance;
        }

        public void addUser(User user) {
            users.put(user.getId(), user);
        }

        public void addGroup(Group group) {
            groups.put(group.getId(), group);
        }

        public void addExpense(String groupId, Expense expense) {
            Group group = groups.get(groupId);
            if (group != null) {
                group.addExpense(expense);
                splitExpense(expense);
                updateBalances(expense);
            }
        }

        private void splitExpense(Expense expense) {
            double totalAmount = expense.getAmount();
            List<Split> splits = expense.getSplits();
            int totalSplits = splits.size();

            double splitAmount = totalAmount / totalSplits;
            for (Split split : splits) {
                if (split instanceof EqualSplit) {
                    split.setAmount(splitAmount);
                } else if (split instanceof PercentSplit percentSplit) {
                    split.setAmount(totalAmount * percentSplit.getPercent() / 100.0);
                }
            }
        }

        private void updateBalances(Expense expense) {
            for (Split split : expense.getSplits()) {
                User paidBy = expense.getPaidBy();
                User user = split.getUser();
                double amount = split.getAmount();

                if (!paidBy.equals(user)) {
                    updateBalance(paidBy, user, amount);
                    updateBalance(user, paidBy, -amount);
                }
            }
        }

        private void updateBalance(User user1, User user2, double amount) {
            String key = getBalanceKey(user1, user2);
            user1.getBalances().put(key, user1.getBalances().getOrDefault(key, 0.0) + amount);
        }

        private String getBalanceKey(User user1, User user2) {
            return user1.getId() + ":" + user2.getId();
        }

        public void settleBalance(String userId1, String userId2) {
            // Not implemented
        }

        private void createTransaction(User sender, User receiver, double amount) {
            // Not implemented
        }

        private String generateTransactionId() {
            int transactionNumber = transactionCounter.incrementAndGet();
            return TRANSACTION_ID_PREFIX + String.format("%06d", transactionNumber);
        }
    }

    public static void main(String[] args) {
        SplitwiseService splitwiseService = SplitwiseService.getInstance();

        User user1 = new User("Alice", "alice@example.com");
        User user2 = new User("Bob", "bob@example.com");
        User user3 = new User("Charlie", "charlie@example.com");

        splitwiseService.addUser(user1);
        splitwiseService.addUser(user2);
        splitwiseService.addUser(user3);

        Group group = new Group("Apartment");
        group.addMember(user1);
        group.addMember(user2);
        group.addMember(user3);

        splitwiseService.addGroup(group);

        Expense expense = new Expense("1", 300.0, "Rent", user1);
        EqualSplit equalSplit1 = new EqualSplit(user1);
        EqualSplit equalSplit2 = new EqualSplit(user2);
        PercentSplit percentSplit = new PercentSplit(user3, 20.0); // 20% of 300 = 60

        expense.addSplit(equalSplit1);
        expense.addSplit(equalSplit2);
        expense.addSplit(percentSplit);

        splitwiseService.addExpense(group.getId(), expense);

        splitwiseService.settleBalance(user1.getId(), user2.getId());
        splitwiseService.settleBalance(user1.getId(), user3.getId());

        for (User user : Arrays.asList(user1, user2, user3)) {
            System.out.println("User: " + user.getName());
            for (Map.Entry<String, Double> entry : user.getBalances().entrySet()) {
                System.out.println("  Balance with " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
