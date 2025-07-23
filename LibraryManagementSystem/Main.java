package LibraryManagementSystem;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static class Book {
        private final String isbn, name, author;

        Book(String isbn, String name, String author) {
            this.isbn = isbn;
            this.name = name;
            this.author = author;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitle() {
            return name;
        }

        public String getAuthor() {
            return author;
        }
    }

    public static class BookCopy {
        private final String id;
        private final Book book;
        private boolean isAvailable = true;

        BookCopy(Book book) {
            this.id = UUID.randomUUID().toString();
            this.book = book;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public synchronized void markIssued() {
            if (!isAvailable)
                throw new IllegalStateException("Already issued");
            isAvailable = false;
        }

        public synchronized void markReturned() {
            isAvailable = true;
        }

        public Book getBook() {
            return book;
        }

        public String getId() {
            return id;
        }
    }

    public static class Loan {
        String id;
        BookCopy copy;
        Member member;
        LocalDate bookDate;
        LocalDate dueDate;
        boolean isActive = true;
        int MAX_BOOKING_DATE = 14;

        public Loan(BookCopy copy, Member member) {
            this.id = UUID.randomUUID().toString();
            this.copy = copy;
            this.member = member;
            this.bookDate = LocalDate.now();
            this.dueDate = this.bookDate.plusDays(MAX_BOOKING_DATE);
        }

        public void closeLoan() {
            this.isActive = false;
            member.removeLoan(this);
            copy.markReturned();
        }

        public String getId() {
            return id;
        }
    }

    public static class Member {
        String id, name, email;
        int MAX_BOOKS = 5;
        List<Loan> list;

        Member(String name, String email) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.email = email;
            list = new ArrayList<>();
        }

        public boolean canBorrow() {
            return list.size() < MAX_BOOKS;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void addLoan(Loan loan) {
            list.add(loan);
        }

        public void removeLoan(Loan loan) {
            list.remove(loan);
        }
    }

    public static class Catalog {
        private final Map<String, List<BookCopy>> byTitle = new HashMap<>();
        private final Map<String, List<BookCopy>> byAuthor = new HashMap<>();
        private final Map<String, List<BookCopy>> byIsbn = new HashMap<>();

        public synchronized void add(BookCopy copy) {
            Book book = copy.getBook();
            byTitle.computeIfAbsent(book.getTitle(), k -> new ArrayList<>()).add(copy);
            byAuthor.computeIfAbsent(book.getAuthor(), k -> new ArrayList<>()).add(copy);
            byIsbn.computeIfAbsent(book.getIsbn(), k -> new ArrayList<>()).add(copy);
        }

        public synchronized List<BookCopy> getBookCopiesByTitle(String title) {
            return byTitle.getOrDefault(title, List.of());
        }

        public synchronized List<Book> searchByTitle(String title) {
            return byTitle.getOrDefault(title, List.of()).stream()
                    .map(BookCopy::getBook)
                    .distinct()
                    .toList();
        }

        public synchronized List<Book> searchByAuthor(String author) {
            return byAuthor.getOrDefault(author, List.of()).stream()
                    .map(BookCopy::getBook)
                    .distinct()
                    .toList();
        }

        public synchronized List<Book> searchByIsbn(String isbn) {
            return byIsbn.getOrDefault(isbn, List.of()).stream()
                    .map(BookCopy::getBook)
                    .distinct()
                    .toList();
        }

        public synchronized long countAvailableCopies(String title) {
            return getBookCopiesByTitle(title).stream().filter(BookCopy::isAvailable).count();
        }
    }

    public static class LibraryManagementSystem {
        private static LibraryManagementSystem instance;
        private final Catalog catalog;
        private final Map<String, Member> members;
        private final Map<String, Loan> loans;

        private LibraryManagementSystem() {
            catalog = new Catalog();
            members = new ConcurrentHashMap<>();
            loans = new ConcurrentHashMap<>();
        }

        public static synchronized LibraryManagementSystem getInstance() {
            if (instance == null) {
                instance = new LibraryManagementSystem();
            }
            return instance;
        }

        public void addBook(Book book, int copies) {
            for (int i = 0; i < copies; i++) {
                BookCopy copy = new BookCopy(book);
                catalog.add(copy);
            }
            long available = catalog.countAvailableCopies(book.getTitle());
            System.out.println("Book added: " + book.getTitle() + " | Total available copies: " + available);
        }

        public Member registerMember(String name, String contactInfo) {
            Member member = new Member(name, contactInfo);
            members.put(member.getId(), member);
            return member;
        }

        public synchronized Loan borrowBook(String memberId, String title) {
            Member member = members.get(memberId);
            if (member == null || !member.canBorrow()) throw new RuntimeException("Not allowed to borrow");

            long availableCopies = catalog.countAvailableCopies(title);
            if (availableCopies == 0) throw new RuntimeException("No available copies to borrow");

            List<BookCopy> copies = catalog.getBookCopiesByTitle(title);
            for (BookCopy copy : copies) {
                if (copy.isAvailable()) {
                    copy.markIssued();
                    Loan loan = new Loan(copy, member);
                    member.addLoan(loan);
                    loans.put(loan.getId(), loan);
                    System.out.println("Book borrowed: " + copy.getBook().getTitle() + " by " + member.getName());
                    return loan;
                }
            }

            throw new RuntimeException("No available copy");
        }

        public synchronized void returnBook(String loanId) {
            Loan loan = loans.get(loanId);
            if (loan != null) loan.closeLoan();
        }

        public List<Book> searchByTitle(String title) {
            return catalog.searchByTitle(title);
        }

        public List<Book> searchByAuthor(String author) {
            return catalog.searchByAuthor(author);
        }
    }

    public static void main(String[] args) {
        LibraryManagementSystem libraryManagementSystem = LibraryManagementSystem.getInstance();

        // Add books to the catalog with copies
        libraryManagementSystem.addBook(new Book("ISBN1", "Book 1", "Author 1"), 10);
        libraryManagementSystem.addBook(new Book("ISBN2", "Book 2", "Author 1"), 15);
        libraryManagementSystem.addBook(new Book("ISBN3", "Book 3", "Author 3"), 5);

        // Register members
        Member member1 = libraryManagementSystem.registerMember("John Doe", "john@example.com");
        Member member2 = libraryManagementSystem.registerMember("Jane Smith", "jane@example.com");

        // Borrow books
        Loan loan1 = libraryManagementSystem.borrowBook(member1.getId(), "Book 1");
        Loan loan2 = libraryManagementSystem.borrowBook(member2.getId(), "Book 2");

        // Return books
        libraryManagementSystem.returnBook(loan1.getId());

        // Search books
        List<Book> searchResults = libraryManagementSystem.searchByAuthor("Author 1");
        System.out.println("Search Results:");
        for (Book book : searchResults) {
            System.out.println(book.getTitle() + " by " + book.getAuthor());
        }
    }
}
