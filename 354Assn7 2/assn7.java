import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class assn7 {
    private static Connection con;
    private static String space = "  ";
    private static String currentUser = "";

    public static void main(String[] args) throws InterruptedException {
        PreparedStatement pstmt = null;
        ResultSet rs;
        String sSQL = "select * from helpdesk"; // the table was created by helpdesk
        String temp = "";

        String sUsername = "HiddenForPrivacy";
        String sPassphrase = "HiddenForPrivacy";
        // ^^^ modify these 2 lines before compiling this program
        // please replace the username with your SFU Computing ID
        // please get the passphrase from table 'dbo.helpdesk' of your course database
        // (using SSMS or Azure Data Studio)

        String sSQLServerString = "jdbc:sqlserver://cypress.csil.sfu.ca;" +
                "encrypt=true;trustServerCertificate=true;loginTimeout=90;";

        try {
            con = DriverManager.getConnection(sSQLServerString, sUsername, sPassphrase);
        } catch (SQLException se) {
            System.out.println("\n\nFail to connect to CSIL SQL Server; exit now.\n\n");

            se.printStackTrace(System.err);
            System.err.println("SQLState: " +
                    ((SQLException) se).getSQLState());

            System.err.println("Error Code: " +
                    ((SQLException) se).getErrorCode());

            System.err.println("Message: " + se.getMessage());

            return;
        }

        try {
            pstmt = con.prepareStatement(sSQL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                temp = rs.getString("username"); // the table has a field 'username'
            }
            rs.close();
            System.out.println("\nSuccessfully connected to CSIL SQL Server!\n\n");
        } catch (SQLException se) {
            System.out.println("\nSQL Exception occurred, the state : " +
                    se.getSQLState() + "\nMessage:\n" + se.getMessage() + "\n");
            return;
        }
        welcome();
    }

    public static void welcome() {
        Scanner s = new Scanner(System.in);
        boolean finished = false;
        while (finished == false) {
            timeDelay(5000);
            System.out.println("------------------------------------");
            System.out.println("Current user is: " + currentUser);
            System.out.println("Please input one of the following numbers to select the function you desire:");
            System.out.println("1. Login");
            System.out.println("2. Search Business");
            System.out.println("3. Search Users");
            System.out.println("4. Review Business");
            System.out.println("5. Exit");
            String userInput = s.nextLine();
            if (userInput.equals("1")) {
                login();
            } else if (userInput.equals("2")) {
                searchBusiness();
            } else if (userInput.equals("3")) {
                searchUser();
            } else if (userInput.equals("4")) {
                reviewBusiness();
            } else if (userInput.equals("5")) {
                finished = true;
            } else {
                System.out.println("ERROR : Please input a valid selection!");
                System.out.println();
            }
        }
    }

    public static void login() {
        Scanner s = new Scanner(System.in);
        PreparedStatement pstmt = null;
        ResultSet rs;
        String sSQL = "";
        String temp = "";

        try {
            System.out.println("\n -----------------");
            System.out.println("You selected Login!");
            System.out.println("Please enter a valid userID");
            String userID = s.nextLine();

            sSQL = "SELECT user_id FROM user_yelp WHERE user_id = '".concat(userID).concat("'");
            pstmt = con.prepareStatement(sSQL);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("USER: " + rs.getString(1) + " is now controlling the program.");
                currentUser = userID;
            } else {
                System.out.println("Please enter a valid user id: " + userID + " is not valid.");
                currentUser = "";
            }

            rs.close();
        } catch (SQLException se) {
            System.out.println("\nSQL Exception occurred, the state : " +
                    se.getSQLState() + "\nMessage:\n" + se.getMessage() + "\n");
            return;
        }
    }

    public static void searchBusiness() {
        // Search Business
        Scanner s = new Scanner(System.in);
        PreparedStatement pstmt = null;
        ResultSet rs;
        String sSQL = "SELECT business_id, name, address, city, stars FROM business "; // the table was created by
                                                                                       // helpdesk
        String temp = "";
        boolean where = false;
        boolean andFilter = false;

        if (currentUser == "") {
            System.out.println("Please login to use other functionality.");
        } else {
            try {
                System.out.println("\n -----------------");
                System.out.println("You selected Search Business!");
                System.out.println("Would you like to apply minimum number of stars filter? Y or N");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    System.out.println("Whats the minimum number of stars you would like? (1-5)");
                    temp = s.nextLine();
                    sSQL = sSQL.concat("WHERE ");
                    sSQL = sSQL.concat("stars >= '" + temp + "'");
                    where = true;
                    andFilter = true;
                    System.out.println(sSQL);
                }

                System.out.println("Would you like to apply a city filter? Y or N");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    if (where == false) {
                        sSQL = sSQL.concat("WHERE ");
                        where = true;
                    }
                    if (andFilter == true) {
                        sSQL = sSQL.concat(" AND ");
                    }
                    System.out.println("Whats city would you like to filter?:3");
                    temp = s.nextLine();
                    sSQL = sSQL.concat("city = '" + temp + "' ");
                    System.out.println(sSQL);
                    andFilter = true;
                }

                System.out.println("Would you like to apply a name filter? Y or N");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    if (where == false) {
                        sSQL = sSQL.concat("WHERE ");
                        where = true;
                    }
                    if (andFilter == true) {
                        sSQL = sSQL.concat(" AND ");
                    }
                    System.out.println("Whats name would you like to filter?:3");
                    temp = s.nextLine();
                    sSQL = sSQL.concat("name LIKE'%" + temp + "%' ");
                    System.out.println(sSQL);
                }

                System.out.println("Would you like to add a ordering? (Y OR N)");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    String inCase = sSQL;
                    sSQL = sSQL.concat(" ORDER BY ");
                    System.out.println("What would you like to order by? stars, name, or city");
                    temp = s.nextLine();
                    System.out.println("TEMP IS " + temp);
                    if (temp.equalsIgnoreCase("stars")) {
                        System.out.println("stars");
                        sSQL = sSQL.concat(" stars");
                    } else if (temp.equalsIgnoreCase("name")) {
                        System.out.println("name");
                        sSQL = sSQL.concat(" name");
                    } else if (temp.equalsIgnoreCase("city")) {
                        System.out.println("city");
                        sSQL = sSQL.concat(" city");
                    } else {
                        sSQL = inCase;
                    }
                }

                System.out.println(sSQL);
                pstmt = con.prepareStatement(sSQL);
                rs = pstmt.executeQuery();
                List<String> resultList = new ArrayList<>();

                while (rs.next()) {
                    String p = rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4)
                            + " " + rs.getString(5);
                    resultList.add(p);
                    System.out.println(resultList.size() + ": " + p);
                }
                if (resultList.isEmpty()) {
                    System.out.println("Search was empty. Please try again.");
                    return;
                }

                System.out.println("Would you like to review a business? (Y OR N)");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("N")) {
                    return;
                }
                reviewBusiness();
                rs.close();
            } catch (SQLException se) {
                System.out.println("\nSQL Exception occurred, the state : " +
                        se.getSQLState() + "\nMessage:\n" + se.getMessage() + "\n");
                return;
            }
        }
    }

    public static void searchUser() {
        // Search Business
        Scanner s = new Scanner(System.in);
        PreparedStatement pstmt = null;
        ResultSet rs;
        // each user: id, name, review count, useful, funny, cool, average stars, and
        // the date
        String sSQL = "SELECT user_id, name, review_count, useful, funny, cool, average_stars, yelping_since FROM user_yelp ";
        String temp = "";
        boolean where = false;
        boolean andFilter = false;

        if (currentUser == "") {
            System.out.println("Please login to use other functionality.");
        } else {
            try {
                System.out.println("\n -----------------");
                System.out.println("You selected Search User!");
                System.out.println("Would you like a name filter? (Y OR N)");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    System.out.println("Whats the name you would like to filter?");
                    temp = s.nextLine();
                    sSQL = sSQL.concat("WHERE ");
                    sSQL = sSQL.concat("name LIKE '%" + temp + "%' ");
                    where = true;
                    andFilter = true;
                    System.out.println(sSQL);
                }
                System.out.println("Would you like a minimum review count filter? (Y OR N)");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    if (where == false) {
                        sSQL = sSQL.concat("WHERE ");
                        where = true;
                    }
                    if (andFilter == true) {
                        sSQL = sSQL.concat(" AND ");
                    }
                    System.out.println("Whats the minimum number of review counts you would like?");
                    temp = s.nextLine();
                    sSQL = sSQL.concat("review_count >='" + temp + "' ");
                    System.out.println(sSQL);
                }

                System.out.println("Would you like a average stars filter? (Y OR N)");
                temp = s.nextLine();
                if (temp.equalsIgnoreCase("Y")) {
                    if (where == false) {
                        sSQL = sSQL.concat("WHERE ");
                        where = true;
                    }
                    if (andFilter == true) {
                        sSQL = sSQL.concat(" AND ");
                    }
                    System.out.println("Whats the minimum number of average stars you would like?");
                    temp = s.nextLine();
                    sSQL = sSQL.concat("average_stars >='" + temp + "' ");
                    System.out.println(sSQL);
                }

                sSQL = sSQL.concat(" ORDER BY name");

                System.out.println(sSQL);
                pstmt = con.prepareStatement(sSQL);
                rs = pstmt.executeQuery();
                List<String[]> resultList = new ArrayList<>();
                while (rs.next()) {
                    String p = rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4)
                            + " " + rs.getString(5) + " " + rs.getString(6) + " " + rs.getString(7) + " "
                            + rs.getString(8);
                    String[] newArray = new String[7];
                    newArray[0] = rs.getString(1);
                    newArray[1] = rs.getString(2);
                    newArray[2] = rs.getString(3);
                    newArray[3] = rs.getString(4);
                    newArray[4] = rs.getString(5);
                    newArray[5] = rs.getString(6);
                    newArray[6] = rs.getString(7);
                    resultList.add(newArray);
                    System.out.println(resultList.size() + ": " + p);
                }
                if (resultList.isEmpty()) {
                    System.out.println("Search was empty. Please try again.");
                    return;
                }
                System.out.println("Would you like to make a friend with one of the people in the list?");
                temp = s.nextLine();
                if (!temp.equalsIgnoreCase("Y")) {
                    return;
                }
                System.out.println("Please select a index value as displayed.");
                temp = s.nextLine();
                try {
                    double d = Double.parseDouble(temp);
                } catch (NumberFormatException nfe) {
                    return;
                }

                if ((Integer.parseInt(temp) - 1) >= resultList.size()) {
                    return;
                }
                String[] newArray = resultList.get(Integer.parseInt(temp) - 1);
                if (newArray[0] == currentUser) {
                    System.out.println("Cannot friend yourself :) " + newArray[0] + " " + currentUser);
                    return;
                }

                // Create a friendship submission
                sSQL = "INSERT INTO friendship (user_id, friend) VALUES ('" + currentUser + "', '" + newArray[0]
                        + "'); ";
                System.out.println(sSQL);
                pstmt = con.prepareStatement(sSQL);
                pstmt.executeUpdate();
                rs.close();
            } catch (SQLException se) {
                System.out.println("\nSQL Exception occurred, the state : " +
                        se.getSQLState() + "\nMessage:\n" + se.getMessage() + "\n");
                return;
            }
        }
    }

    public static void reviewBusiness() {
        // Search Business
        Scanner s = new Scanner(System.in);
        PreparedStatement pstmt = null;
        ResultSet rs;
        // each user: id, name, review count, useful, funny, cool, average stars, and
        // the date
        String sSQL = "SELECT user_id, name, review_count, useful, funny, cool, average_stars, yelping_since FROM user_yelp ";
        String temp = "";
        boolean where = false;
        boolean andFilter = false;

        if (currentUser == "") {
            System.out.println("Please login to use other functionality.");
        } else {
            try {
                System.out.println("\n -----------------");
                System.out.println("You selected Review Business!");
                System.out.println("Please input a valid business id you would like to select.");
                temp = s.nextLine();

                sSQL = "SELECT business_id FROM business WHERE business_id = '" + temp + "'";
                pstmt = con.prepareStatement(sSQL);
                rs = pstmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("A valid business was not submitted. Try again.");
                    return;
                }
                String business_id = temp;
                String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                String review_id = "";
                Random r = new Random();
                for (int i = 0; i < 22; i++) {
                    char p = (CHARACTERS.charAt(r.nextInt(CHARACTERS.length())));
                    review_id = review_id.concat(String.valueOf(p));
                }

                System.out.println("Please input the amount of stars you would like to give! (1-5)");
                temp = s.nextLine();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                sSQL = "INSERT INTO review (review_id, user_id, business_id, stars, useful, funny, cool, date) VALUES ('"
                        + review_id + "', '" + currentUser + "', '" + business_id + "', '" + temp
                        + "', '0', '0', '0', '" + dateFormat.format(date) + "');";
                System.out.println(sSQL);

                pstmt = con.prepareStatement(sSQL);
                pstmt.executeUpdate();
            } catch (SQLException se) {
                System.out.println("\nSQL Exception occurred, the state : " +
                        se.getSQLState() + "\nMessage:\n" + se.getMessage() + "\n");
                return;
            }
        }
    }

    public static void timeDelay(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }
}
