import java.util.HashMap;
import java.util.Random;

public class URLShortener {
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 6; // Length of the short URL code
    private HashMap<String, String> shortToLongMap;
    private HashMap<String, String> longToShortMap;

    public URLShortener() {
        shortToLongMap = new HashMap<>();
        longToShortMap = new HashMap<>();
    }

    // Method to generate a random short URL code (base-62 encoding)
    private String generateShortUrl() {
        StringBuilder shortUrl = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int index = random.nextInt(CHAR_SET.length());
            // System.out.println("random "+ random +" index "+index);
            shortUrl.append(CHAR_SET.charAt(index));
        }
        return shortUrl.toString();
    }

    // Method to shorten a long URL
    public String shortenUrl(String longUrl) {
        // Check if the long URL is already shortened
        if (longToShortMap.containsKey(longUrl)) {
            return "short.ly/" + longToShortMap.get(longUrl);
        }

        // Generate a new short URL
        String shortUrlCode = generateShortUrl();
        while (shortToLongMap.containsKey(shortUrlCode)) {
            // Handle collision by generating a new short URL code
            shortUrlCode = generateShortUrl();
        }

        // Store the mappings
        shortToLongMap.put(shortUrlCode, longUrl);
        longToShortMap.put(longUrl, shortUrlCode);

        return "short.ly/" + shortUrlCode;
    }

    // Method to retrieve the original long URL from a short URL
    public String getLongUrl(String shortUrl) {
        String shortUrlCode = shortUrl.replace("short.ly/", "");
        return shortToLongMap.getOrDefault(shortUrlCode, "URL not found!");
    }

    // Main method for testing the URL Shortener
    public static void main(String[] args) {
        URLShortener urlShortener = new URLShortener();

        // Test cases
        String longUrl1 = "https://www.example.com/some/very/long/url";
        String shortUrl1 = urlShortener.shortenUrl(longUrl1);
        System.out.println("Shortened URL: " + shortUrl1);
        System.out.println("Retrieved Long URL: " + urlShortener.getLongUrl(shortUrl1));

        String longUrl2 = "https://www.anotherexample.com/different/path";
        String shortUrl2 = urlShortener.shortenUrl(longUrl2);
        System.out.println("Shortened URL: " + shortUrl2);
        System.out.println("Retrieved Long URL: " + urlShortener.getLongUrl(shortUrl2));

        // Testing duplicate long URL shortening
        System.out.println("Shortened URL (duplicate): " + urlShortener.shortenUrl(longUrl1));
    }
}
