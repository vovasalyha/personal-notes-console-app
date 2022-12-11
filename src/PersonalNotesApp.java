import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class PersonalNotesApp {
    private static Scanner scanner = new Scanner(System.in);
    private static String userDirectory;

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to personal notes trivial console app!");
        System.out.println("To continue you need to login to your account");
        checkUsersFile();
        while (true) {
            System.out.print("Choose action (1 - login, 2 - create account, 0 - exit):");
            var firstAction = scanner.nextLine();
            if (firstAction.equals("1")) {
                boolean isLoggedIn = login();
                if (isLoggedIn) {
                    System.out.println("Welcome, " + userDirectory);
                    noteActions();
                } else {
                    System.out.println("Wrong username or password, try again");
                }
            } else if (firstAction.equals("2")) {
                var responseMessage = createAccount();
                System.out.println(responseMessage);
            } else if (firstAction.equals("0")) {
                break;
            } else {
                System.out.println("Wrong action, try again");
            }
        }
        System.out.println("Good luck!!!");
    }

    private static void noteActions() throws IOException {
        while (true) {
            System.out.print("Choose action (1 - read notes, 2 - add note, 0 - logout):");
            var action = scanner.nextLine();
            if (action.equals("1")) {
                readNotes();
            } else if (action.equals("2")) {
                addNote();
            } else if (action.equals("0")) {
                userDirectory = null;
                System.out.println("Logged out");
                break;
            } else {
                System.out.println("Wrong action, try again");
            }
        }
    }

    private static void checkUsersFile() {
        if (!isFileExist("users")) {
            writeToFile("users", "admin=admin");
        }
    }

    private static String createAccount() throws IOException {
        System.out.print("Create username:");
        var username = scanner.nextLine();
        System.out.print("Create password:");
        var password = scanner.nextLine();
        if (username == null || username.isEmpty() || username.isBlank()) {
            return "Failed to register. Username cannot be empty.";
        }
        if (password == null || password.isEmpty() || password.isBlank()) {
            return "Failed to register. Username cannot be empty.";
        }
        boolean isUsernameAlreadyExist = getFileLines("users")
                .anyMatch(user -> user.startsWith(username));
        if (isUsernameAlreadyExist) {
            return "Failed to register. Username '" + username + "' already exists";
        } else {
            writeToFile("users", username + "=" + password);
            return "Account successfully created";
        }
    }

    private static boolean login() throws IOException {
        System.out.print("Username:");
        var username = scanner.nextLine();
        System.out.print("Password:");
        var password = scanner.nextLine();
        Optional<String> userOptional = getFileLines("users")
                .filter(line -> line.startsWith(username))
                .findFirst();
        if (userOptional.isPresent() && userOptional.get().split("=")[1].equals(password)) {
            userDirectory = username;
            return true;
        }
        return false;
    }

    private static void readNotes() throws IOException {
        printFileContent(userDirectory);
    }

    private static void printFileContent(String fileName) throws IOException {
        if (isFileExist(fileName)) {
            getFileLines(fileName)
                    .forEach(System.out::println);
        } else {
            System.out.println("You do not have any note yet");
        }
    }

    private static Stream<String> getFileLines(String fileName) throws IOException {
        return Files.lines(Paths.get("/app/files/" + fileName + ".txt"));
    }

    private static void addNote() {
        System.out.print("Type note content:");
        var content = scanner.nextLine();
        writeToFile(userDirectory, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")) + " | " + content);
    }

    private static void writeToFile(String fileName, String content) {
        try {
            Files.writeString(Paths.get("/app/files/" + fileName + ".txt"), content + System.lineSeparator(), UTF_8, CREATE, APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to file");
        }
    }

    private static boolean isFileExist(String fileName) {
        return Files.exists(Paths.get("/app/files/" + fileName + ".txt"));
    }
}
