import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HttpUtil {
    private static final String USER_URL = "https://jsonplaceholder.typicode.com/users";
    private static final String USER_POSTS_URL = "posts";
    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";
    private static final String COMMENTS_URL = "comments";
    private static final String TODOS_URL = "todos";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final String RELATIVE_PATH = "./src/main/resources/out";

    public static User postUser(User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type", "application/json; charset=UTF-8")
                .build();
        final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User putUser(int id, User user) throws IOException, InterruptedException {
//        user.setId(id);
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", USER_URL, id)))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-type", "application/json; charset=UTF-8")
                .build();
        final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), User.class);
    }

    public static int deleteUser(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", USER_URL, id)))
                .DELETE()
                .header("Content-type", "application/json; charset=UTF-8")
                .build();
        final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static List<User> getAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), new TypeToken<List<User>>() {
        }.getType());
    }

    public static User getUserById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", USER_URL, id)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User getUserByUsername(String userName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        List<User> users = GSON.fromJson(response.body(), new TypeToken<List<User>>() {
        }.getType());
        return users.stream()
                .filter(user -> user.getUsername().contains(userName))
                .findFirst()
                .orElseThrow();
    }

    public static int getLastPostId(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", USER_URL, userId, USER_POSTS_URL)))
                .GET().build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        List<Post> posts = GSON.fromJson(response.body(), new TypeToken<List<Post>>() {
        }.getType());
        return posts.stream()
                .max(Comparator.comparingInt(Post::getId))
                .get()
                .getId();
    }

    public static void writeCommentsToFile(HttpResponse<String> response, String name) {
        File comments = new File(name);
        if (!comments.exists()) {
            comments.getParentFile().mkdirs();
            try {
                comments.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(comments)) {
            outputStream.write(response.body().getBytes());
        } catch (IOException e) {
            System.err.println("Exception!!!" + e.getMessage());
        }
    }

    public static List<Comment> getLastPostCommentsByUserId(int userId) throws IOException, InterruptedException {
        int maxUserId = getLastPostId(userId);
        String fileName = String.format("%s/user-%d-post-%d-comments.json", RELATIVE_PATH, userId, maxUserId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", POSTS_URL, maxUserId, COMMENTS_URL)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        writeCommentsToFile(response, fileName);
        return GSON.fromJson(response.body(), new TypeToken<List<Comment>>() {
        }.getType());
    }

    public static List<Todo> getOpenTodos(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", USER_URL, userId, TODOS_URL)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        final List<Todo> todos = GSON.fromJson(response.body(), new TypeToken<List<Todo>>() {
        }.getType());
        return todos.stream()
                .filter((todo) -> !todo.isCompleted())
                .collect(Collectors.toList());
    }
}
