
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class DataBaseConnection {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String CREATE = "jdbc:derby:playerdata;create=true";
    public static final String OPEN = "jdbc:derby:playerdata";
    public static final String SQL_STATEMENT = "select * from highscores";

    private static Connection connection;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        init();
        Random rand = new Random();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 3; i++) {
            addNewData("name" + i, now, rand.nextInt(10), rand.nextInt(10));
            //getDataFromBase(null);
        }
    }

    public static void init() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        try {
            connection = DriverManager.getConnection(CREATE);
            connection.createStatement().execute("create table highscores(id varchar(40), login varchar(20), date varchar(40), score INTEGER, time INTEGER )");
        } catch (Exception ex) {
            connection = DriverManager.getConnection(OPEN);
        }
    }

    public static boolean addNewData(String data1, LocalDateTime data2, int data3, int data4) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss:SSS z");
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Use Madrid's time zone to format the date in
            df.setTimeZone(TimeZone.getTimeZone("Среднее время по Гринвичу"));
            System.out.println(df.getTimeZone().getDisplayName());

            System.out.println("Date and time in UTC-0: " + df.format(date));
            connection.createStatement().execute("insert into highscores values" + "('" + UUID.randomUUID() + "','" + data1 + "','" + df.format(date) + "'," + data3 + "," + data4 + ")");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Data> getDataFromBase(String timeZone) throws SQLException, ParseException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SQL_STATEMENT);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        //System.out.println("\n\n");
        ArrayList<Data> table = new ArrayList<>();
        Data dummy = new Data();
        for (int i = 1; i < columnCount + 1; i++) {
            //System.out.print(resultSetMetaData.getColumnName(i) + "\t");
        }
        while (resultSet.next()) {
            dummy.id = resultSet.getString(1);
            dummy.login = resultSet.getString(2);
            dummy.date = resultSet.getString(3);
            if (timeZone != null) {

                Date trueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummy.date);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //df.setTimeZone(TimeZone.getTimeZone("Среднее время по Гринвичу"));
                //System.out.println("Time Zone = " + timeZone);
                //System.out.println("Table Time = " + dummy.date);
                df.setTimeZone(TimeZone.getTimeZone(timeZone));
                dummy.date = df.format(trueDate);
                //System.out.println("New Time = " + dummy.date);
            }
            dummy.score = Integer.parseInt(resultSet.getString(4));
            dummy.time = Integer.parseInt(resultSet.getString(5));
            table.add(dummy);
            dummy = new Data();

            //System.out.print("\n");
            for (int i = 1; i < columnCount + 1; i++) {
                //System.out.print(resultSet.getString(i) + "\t");
            }
        }
        table.sort(new Data());
        //System.out.println("\n\n\n\nData:");
        for (int i = 0; i < table.size(); i++) {
            //System.out.println(table.get(i).toString());
        }
        return table;
    }

    public static String getAllData() {
        try {
            ArrayList<Data> all = getDataFromBase(null);
            String data = "";
            for (int i = 0; i < all.size(); i++) {
                data += all.get(i).toString() + "\n";
            }
            return data;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getDataForClient(String timeZone) {
        try {
            ArrayList<Data> top = getDataFromBase(timeZone);
            while (top.size() > 10) {
                top.remove(10);
            }
            String str = "";
            for(int i = 0; i < top.size(); i++) {
                str += top.get(i).getPrettyString()+"\b";
            }
            if (str.equals("")) {
                return null;
            }
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

class Data implements Comparator<Data> {
    String id;
    String login;
    String date;
    int score;
    int time;

    @Override
    public int compare(Data f, Data s) {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f.date);
            Date dateTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s.date);
            //System.out.println("started comparing");
            if (dateTime.equals(dateTime2)) {
                //System.out.println("Date equals");
                if (f.time == s.time) {
                    //System.out.println("time equals");
                    if (f.score > s.score) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    //System.out.println("Comparing times: " + (f.time > s.time) + "    " + f.time + " " + s.time);
                    if (f.time > s.time) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            } else {
                //System.out.println("Comparing dates: " + dateTime.compareTo(dateTime2));
                return -1 * dateTime.compareTo(dateTime2);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    @Override
    public String toString() {
        return id + "\t" + login + "\t" + date + "\t" + score + "\t" + time;
    }

    public String getPrettyString() {
        //System.out.println((String.format("%8s", login) + " | " + date + " | " + String.format("%7s", score) + " | " + String.format("%8s", time)).length());
        return String.format("%8s", login) + " | " + date + " | " + String.format("%8s", score) + " | " + String.format("%12s", time);
    }
}
