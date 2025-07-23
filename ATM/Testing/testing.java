package ATM.Testing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class testing {
    public static class Account {
        private final int  accountNo;
        private int balance;
        private final Card card;

        public Account(int accountNo, int balance, Card card) {
            this.accountNo = accountNo;
            this.balance = balance;
            this.card = card;
        }

        public int getAccountNo() {
            return this.accountNo;
        }

        public int getBalance() {
            return this.balance;
        }

        public Card getCard() {
            return this.card;
        }

        public void debit(int amt) {
            if (amt <= this.balance) {
                this.balance = this.balance - amt;
            } else {
                System.out.println("Insufficient Amount in your account");
            }
        }

        public void credit(int amt) {
            this.balance = this.balance + amt;
        }

    }

    public static class Card {
        private int cardNo;
        private int pin;

        Card(int cardNo, int pin) {
            this.cardNo = cardNo;
            this.pin = pin;
        }

        public int getCardNo() {
            return this.cardNo;
        }

        public int getPin() {
            return this.pin;
        }
    }

    public static abstract class Transaction {
        protected final String transactionId;
        protected final Account account;
        protected final int amount;

        public Transaction(String transactionId, Account account, int amount) {
            this.transactionId = transactionId;
            this.account = account;
            this.amount = amount;
        }

        public abstract void execute();
    }

    public static class WithdrawalTransaction extends Transaction {
        public WithdrawalTransaction(String transactionId, Account account, int amount) {
            super(transactionId, account, amount);
        }

        @Override
        public void execute() {
            account.debit(amount);
        }
    }

    public static class DepositTransaction extends Transaction {
        public DepositTransaction(String transactionId, Account account, int amount) {
            super(transactionId, account, amount);
        }

        @Override
        public void execute() {
            account.credit(amount);
        }
    }

    public static class BankingService {
        private Map<Integer, Account> accounts;
        private Map<Integer, Account> cards;

        public BankingService() {
            this.accounts = new HashMap<>();
            this.cards = new HashMap<>();
        }

        public void addBankAccount(Account acc) {
            this.accounts.put(acc.accountNo, acc);
            this.cards.put(acc.getCard().cardNo, acc);
        }

        public Account getAccount(int accNo){
            return this.accounts.get(accNo);
        }
        public Card getCard(int accNo) {
            return this.accounts.get(accNo).getCard();
        }

        public boolean authenticate(int cardNumber, int pin) {
            Account account = cards.get(cardNumber);
            Card card = account.getCard();
            return card != null && card.getPin() == pin;
        }

        public void processTransaction(Transaction transaction) {
            transaction.execute();
        }

    }

    public static class CashDispenser {
        private int cashAvailable;

        public CashDispenser(int initialCash) {
            this.cashAvailable = initialCash;
        }

        public synchronized void dispenseCash(int amount) {
            if (amount > cashAvailable) {
                throw new IllegalArgumentException("Insufficient cash available in the ATM.");
            }
            cashAvailable -= amount;
            System.out.println("Cash dispensed: " + amount);
        }
    }

    public static class ATM {
        private final BankingService bankingService;
        private final CashDispenser cashDispenser;
        private static final AtomicLong transactionCounter = new AtomicLong(0);

        public ATM(BankingService bankingService, CashDispenser cashDispenser) {
            this.bankingService = bankingService;
            this.cashDispenser = cashDispenser;
        }

        public void authenticateUser(Card card) {
            boolean isAuthenticated = bankingService.authenticate(card.getCardNo(), card.getPin());
            if (isAuthenticated) {
                System.out.println("Authentication successful.");
            } else {
                System.out.println("Authentication failed.");
            }
        }

        public int checkBalance(int accountNumber) {
            Account account = bankingService.getAccount(accountNumber);
            return account.getBalance();
        }

        public void withdrawCash(int accountNumber, int amount) {
            Account account = bankingService.getAccount(accountNumber);
            // Check if sufficient balance is available
            if (account != null && account.getBalance() < amount) {
                System.out.println("Error: Insufficient balance.");
                return;
            }
            Transaction transaction = new WithdrawalTransaction(generateTransactionId(), account, amount);
            bankingService.processTransaction(transaction);
            cashDispenser.dispenseCash((int) amount);
        }

        public void depositCash(int accountNumber, int amount) {
            Account account = bankingService.getAccount(accountNumber);
            Transaction transaction = new DepositTransaction(generateTransactionId(), account, amount);
            bankingService.processTransaction(transaction);
        }

        private String generateTransactionId() {
            // Generate a unique transaction ID
            long transactionNumber = transactionCounter.incrementAndGet();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            return "TXN" + timestamp + String.format("%010d", transactionNumber);
        }
    }
    public static void main(String[] args) {
        BankingService bankingService = new BankingService();
        CashDispenser cashDispenser = new CashDispenser(10000);
        ATM atm = new ATM(bankingService, cashDispenser);

        // Create sample accounts
        Card card1 = new Card(1234567890, 1234);
        Card card2 = new Card(1234567891, 1234);
        bankingService.addBankAccount(new Account(1234567890, 1000, card1));
        bankingService.addBankAccount(new Account(1234567891, 1000, card2));

        // Perform ATM operations
        
        atm.authenticateUser(card1);

        int balance = atm.checkBalance(1234567890);
        System.out.println("Account balance: " + balance);

        atm.withdrawCash(1234567890, 500);
//        atm.depositCash(1876543210, 200);

        balance = atm.checkBalance(1234567890);
        System.out.println("Updated account balance: " + balance);
    
    }
}
