package app;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONArray;

import data.Address;
import data.Company;
import data.Geo;
import data.User;
import create.TableGenerator;

public class Program {
    public static List<User> getUsers(String url) throws IOException, InterruptedException {
        // create a client
        HttpClient client = HttpClient.newHttpClient();

        // create a request
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(url))
                .header("accept", "application/json")
                .build();

        // use the client to send the request
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String content = response.body();
        return mapToUsers(content);
    }

    public static List<User> mapToUsers(String content) {
        List<User> users = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(content);

        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonUser = jsonArray.getJSONObject(i);
            int id = jsonUser.getInt("id");
            String name = jsonUser.getString("name");
            String userName = jsonUser.getString("username");
            String email = jsonUser.getString("email");

            JSONObject jsonAddress = jsonUser.getJSONObject("address");
            String street = jsonAddress.getString("street");
            String suite = jsonAddress.getString("suite");
            String city = jsonAddress.getString("city");
            String zipcode = jsonAddress.getString("zipcode");

            JSONObject jsonGeo = jsonAddress.getJSONObject("geo");
            BigDecimal lat = jsonGeo.getBigDecimal("lat");
            BigDecimal lng = jsonGeo.getBigDecimal("lng");

            String phone = jsonUser.getString("phone");
            String website = jsonUser.getString("website");

            JSONObject jsonCompany = jsonUser.getJSONObject("company");
            String nameCompany = jsonCompany.getString("name");
            String catchPhrase = jsonCompany.getString("catchPhrase");
            String bs = jsonCompany.getString("bs");

            Geo geo = new Geo(lat, lng);
            Address address = new Address(street, suite, city, zipcode, geo);
            Company company = new Company(nameCompany, catchPhrase, bs);
            User user = new User(id, name, userName, email, address, phone, website, company);
            users.add(user);
        }
        return users;
    }

    public static void printMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose your option: ");
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("This is simple command line program.");
        for (int i = 0; i < args.length; i++) {
            if(args[i].contains("-h") || args[i].contains("--help")|| args[i].contains("/h")|| args[i].contains("/?"))
            {
                System.out.println("Your command line arguments are:\n" +
                        " 1. Display whole table.\n" +
                        " 2. Display sorted table by id DESC.\n" +
                        " 3. Display the table for selected user IDs\n" +
                        " 4. Exit.");
            }
        }
        List<User> users = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int retries = 0;

        int menu = -1;
        while(menu != 4) {
            try {
                boolean isEmptyList = users.isEmpty();

                if (isEmptyList) {
                    if (retries > 0) {
                        System.out.println("If you checked connection press any key to continue...");
                        System.in.read();
                    }
                    retries++;
                    users = users = getUsers("https://jsonplaceholder.typicode.com/users");
                }

                System.out.println("Menu");
                System.out.println("1. Display whole table.");
                System.out.println("2. Display sorted table by id DESC.");
                System.out.println("3. Display the table for selected user IDs.");
                System.out.println("4. Exit.");

                menu = scanner.nextInt();

                switch (menu) {
                    case 1:
                        System.out.println("Display whole table.");
                        display(users);
                        break;
                    case 2:
                        System.out.println("Sorted table by id DESC.");
                        displayInverted(users);
                        break;
                    case 3:
                        System.out.println("Select user IDs after a decimal point (example: 1,5),");
                        scanner.nextLine();
                        String selectedUsers = scanner.nextLine();
                        List<Integer> ids = formatInput(selectedUsers);
                        displaySelection(users, ids);
                        break;
                    case 4:
                        System.out.println("Exit.");
                        break;
                    default:
                        System.out.println("Invalid number.");
                        break;
                }
            } catch (Exception exception) {
                System.out.println(mapToResponse(exception));
                if (exception.getClass() == java.util.InputMismatchException.class)
                    scanner.next(); // zapobieganie nieskonczonej petli
            }
        }
        scanner.close();
    }

    private static void display(List<User> users) {
        TableGenerator tableGenerator = new TableGenerator();

        List<String> headersList = new ArrayList<>();
        headersList.add("Id");
        headersList.add("Name");
        headersList.add("Company");
        headersList.add("City");
        headersList.add("Location");

        List<List<String>> rowsList = new ArrayList<>();

        for (User user: users) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(user.id));
            row.add(user.name);
            row.add(user.company.name);
            row.add(user.address.city);
            row.add(user.address.geo.convertToDMSCord());

            rowsList.add(row);
        }
        System.out.println(tableGenerator.generateTable(headersList, rowsList));
    }

    private static void displayInverted(List<User> users) {
        List<User> reversed = users.stream().sorted((o1, o2) -> Integer.compare(o2.id, o1.id)).collect(Collectors.toList());
        display(reversed);
    }

    private static List<Integer> formatInput(String selectedUsers){
        List<Integer> users = new ArrayList<>();
        String[] splitedString = selectedUsers.split(",");

        for (int i=0; i< splitedString.length; i++ ) {
            String inputString = splitedString[i];
            users.add(addId(inputString));
        }
        return users;
    }

    private static Integer addId(String inputString) {
        try {
            return Integer.valueOf(inputString);
        } catch (Exception exception){
            throw new IllegalArgumentException("The value " + inputString + " is invalid. Id users should be separated by comma ',' and the value should be int.");
        }
    }

    private static void displaySelection(List<User> users, List<Integer> ids) {
        //filter by id
        List<User> usersFiltered = users.stream().filter(u -> ids.contains(u.id)).toList();
        displayInverted(usersFiltered);
    }

    private static String mapToResponse(Exception exception) {
        if (exception.getClass() == java.util.InputMismatchException.class) {
            return "Invalid input value.";
        } else if (exception.getClass() == ConnectException.class) {
            return  "Something bad happen with connection, check your address and connection with internet.";
        } else if(exception.getClass() == IllegalArgumentException.class) {
            return exception.getMessage();
        } else {
            return "Something bad happen.";
        }
    }
}