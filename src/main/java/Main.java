import java.io.IOException;
import java.net.URI;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        User user = createSomeUser();

        User createdUser = HttpUtil.postUser(user);
        System.out.println("createdUser = " + createdUser);

        User updatedUser = HttpUtil.putUser(7, user);
        System.out.println("updatedUser = " + updatedUser);
//
        int deleteUser = HttpUtil.deleteUser(8);
        System.out.println("deleteUser.statusCode = " + deleteUser);

        List<User> allUsers = HttpUtil.getAllUsers();
        System.out.println("allUsers = " + allUsers);

        User getUserById = HttpUtil.getUserById(7);
        System.out.println("getUserById = " + getUserById);

        User getUserByUsername = HttpUtil.getUserByUsername("Samantha");
        System.out.println("getUserByUsername = " + getUserByUsername);

//        int maxId = HttpUtil.maxId(5);
//        System.out.println("maxId = " + maxId);

        List<Comment> comments = HttpUtil.getComments(5);
        System.out.println("comments = " + comments);

        List<Todo> todos = HttpUtil.getOpenTodos(5);
        System.out.println("todos = " + todos);
    }

    private static User createSomeUser() {
        User user = new User();
        user.setName("Alex");
        user.setUsername("Al");
        user.setEmail("alex123@gmail.com");
        user.setAddress(new Address("Anna Frank", "Apt. 100", "Kharkiv", "33822",
                new Geo((float) 49.9832, (float) 36.2462)));
        user.setPhone("063-123-45-67");
        user.setWebsite("www.alexcompany.com.ua");
        user.setCompany(new Company("AlexCo", "THE TASK WILL BE COMPLETED",
                "aggregate real-time technologies"));
        return user;
    }
}
