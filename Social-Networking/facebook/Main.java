import java.sql.Timestamp;
import java.util.*;

public class Main {
    public static class User {

        private final String id;
        private final String email;
        private final String password;
        private final String name;
        private final String bio;
        private final Set<User> friends;
        private final Map<String, Post> postsMap;
        private final List<FriendRequest> friendRequests;
        private final List<Notification> notifications;

        public User(String name, String email, String password, String bio) {
            this.id = UUID.randomUUID().toString();
            this.email = email;
            this.name = name;
            this.password = password;
            this.bio = bio;
            this.friendRequests = new ArrayList<>();
            this.notifications = new ArrayList<>();
            this.friends = new HashSet<>();
            this.postsMap = new HashMap<>();
        }

        public void addFriend(User user) {
            friends.add(user);
        }

        public boolean isFriend(User user) {
            return friends.contains(user);
        }

        public void addPost(Post post) {
            postsMap.put(post.getId(), post);
        }

        public Post getPost(String postId) {
            return postsMap.get(postId);
        }

        public List<Post> getPosts() {
            return new ArrayList<>(postsMap.values());
        }

        public void receiveRequest(FriendRequest req) {
            friendRequests.add(req);
        }

        public FriendRequest getRequestFrom(User user) {
            for (FriendRequest r : friendRequests) {
                if (r.getFrom().equals(user) && r.getStatus() == FriendRequestStatus.PENDING)
                    return r;
            }
            return null;
        }

        public void addNotification(Notification notification) {
            notifications.add(notification);
        }

        public List<Notification> getNotifications() {
            return notifications;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getBio() {
            return bio;
        }

        public List<User> getFriends() {
            return new ArrayList<>(friends);
        }
    }

    public enum FriendRequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public static class FriendRequest {
        private final User to;
        private final User from;
        private FriendRequestStatus status = FriendRequestStatus.PENDING;

        public FriendRequest(User from, User to) {
            this.from = from;
            this.to = to;
        }

        public void accept() {
            this.status = FriendRequestStatus.ACCEPTED;
        }

        public void reject() {
            this.status = FriendRequestStatus.REJECTED;
        }

        public User getFrom() {
            return from;
        }

        public User getTo() {
            return to;
        }

        public FriendRequestStatus getStatus() {
            return status;
        }
    }

    public static class Post {
        private final String id;
        private final User author;
        private final String content;
        private final Timestamp timestamp;
        private final Map<String, Like> userIdLikeMap;
        private final List<Comment> comments;

        public Post(User author, String content) {
            this.id = UUID.randomUUID().toString();
            this.author = author;
            this.content = content;
            this.timestamp = new Timestamp(System.currentTimeMillis());
            this.userIdLikeMap = new HashMap<>();
            this.comments = new ArrayList<>();
        }

        public void like(User user) {
            if (!userIdLikeMap.containsKey(user.getId())) {
                userIdLikeMap.put(user.getId(), new Like(user));
            } else {
                userIdLikeMap.remove(user.getId()); // Unlike
            }
        }

        public void comment(User user, String text) {
            comments.add(new Comment(user, text));
        }

        public int getLikeCount() {
            return userIdLikeMap.size();
        }

        public List<Like> getLikes() {
            return new ArrayList<>(userIdLikeMap.values());
        }

        public int getCommentCount() {
            return comments.size();
        }

        public List<Comment> getComments() {
            return comments;
        }

        public String getId() {
            return id;
        }

        public User getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }
    }

    public static class Like {
        private final User user;
        private final Timestamp timestamp;

        public Like(User user) {
            this.user = user;
            this.timestamp = new Timestamp(System.currentTimeMillis());
        }
    }

    public static class Comment {
        private final String id;
        private final User user;
        private final String text;
        private final Timestamp timestamp;

        public Comment(User user, String text) {
            this.id = UUID.randomUUID().toString();
            this.user = user;
            this.text = text;
            this.timestamp = new Timestamp(System.currentTimeMillis());
        }

        public String getId() {
            return id;
        }

        public User getUser() {
            return user;
        }

        public String getText() {
            return text;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }
    }

    public enum NotificationType {
        FRIEND_REQUEST,
        FRIEND_REQUEST_ACCEPTED,
        LIKE,
        COMMENT,
        MENTION
    }

    public static class Notification {
        private final String id;
        private final User user;
        private final NotificationType type;
        private final String content;
        private final Timestamp timestamp;

        public Notification(User user, NotificationType type, String content) {
            this.id = UUID.randomUUID().toString();
            this.user = user;
            this.type = type;
            this.content = content;
            this.timestamp = new Timestamp(System.currentTimeMillis());
        }

        public String getId() {
            return id;
        }

        public String getUserId() {
            return user.getId();
        }

        public NotificationType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }
    }

    public static class SocialNetworkingService {
        private final Map<String, User> usersMap;
        private final Map<String, Post> postsMap;

        public SocialNetworkingService() {
            this.usersMap = new HashMap<>();
            this.postsMap = new HashMap<>();
        }

        public User registerUser(String name, String email, String password, String bio) {
            User user = new User(name, email, password, bio);
            usersMap.put(user.getId(), user);
            return user;
        }

        public User loginUser(String email, String password) {
            return usersMap.values().stream()
                    .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                    .findFirst().orElse(null);
        }

        public void sendFriendRequest(String fromId, String toId) {
            User from = usersMap.get(fromId);
            User to = usersMap.get(toId);
            if (from == null || to == null) throw new IllegalArgumentException("User not found");
            if (from.isFriend(to)) return;

            FriendRequest request = new FriendRequest(from, to);
            to.receiveRequest(request);
            sendNotification(to, NotificationType.FRIEND_REQUEST, "Friend request from " + from.getName());
        }

        public void acceptFriendRequest(String toId, String fromId) {
            User to = usersMap.get(toId);
            User from = usersMap.get(fromId);
            FriendRequest request = to.getRequestFrom(from);
            if (request == null) throw new IllegalArgumentException("No pending request");

            request.accept();
            to.addFriend(from);
            from.addFriend(to);
            sendNotification(from, NotificationType.FRIEND_REQUEST_ACCEPTED, to.getName() + " accepted your request.");
        }

        public Post createPost(String userId, String content) {
            User user = usersMap.get(userId);
            Post post = new Post(user, content);
            user.addPost(post);
            postsMap.put(post.getId(), post);
            return post;
        }

        public void likePost(String userId, String postId) {
            User user = usersMap.get(userId);
            Post post = postsMap.get(postId);
            post.like(user);
            sendNotification(post.getAuthor(), NotificationType.LIKE, user.getName() + " liked your post.");
        }

        public void commentOnPost(String userId, String postId, String text) {
            User user = usersMap.get(userId);
            Post post = postsMap.get(postId);
            post.comment(user, text);
            sendNotification(post.getAuthor(), NotificationType.COMMENT, user.getName() + " commented: " + text);
        }

        public List<Post> getNewsFeed(String userId) {
            User user = usersMap.get(userId);
            return user.getFriends().stream()
                    .flatMap(f -> f.getPosts().stream())
                    .sorted(Comparator.comparing(Post::getTimestamp).reversed())
                    .limit(20)
                    .toList();
        }

        private void sendNotification(User user, NotificationType type, String text) {
            Notification notification = new Notification(user, type, text);
            user.addNotification(notification);
        }

        public List<Notification> getNotifications(String userId) {
            return usersMap.get(userId).getNotifications();
        }
    }

    public static void main(String[] args) {
        SocialNetworkingService service = new SocialNetworkingService();

        User john = service.registerUser("John Doe", "john@example.com", "pass", "Developer");
        User jane = service.registerUser("Jane Smith", "jane@example.com", "pass", "Explorer");

        service.sendFriendRequest(john.getId(), jane.getId());
        service.acceptFriendRequest(jane.getId(), john.getId());

        Post post1 = service.createPost(john.getId(), "Hello World!");
        service.likePost(jane.getId(), post1.getId());
        service.commentOnPost(jane.getId(), post1.getId(), "Nice one!");

        List<Post> feed = service.getNewsFeed(jane.getId());
        System.out.println("Jane's News Feed:");
        for (Post post : feed) {
            System.out.println(post.getAuthor().getName() + ": " + post.getContent());
        }
    }
}
