import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DataBaseConnection {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String CREATE = "jdbc:derby:playerdata;create=true";
    public static final String OPEN = "jdbc:derby:playerdata";
    public static final String SQL_STATEMENT = "select * from highscores";

    private static Connection connection;

    /**
     * Установка связи с БД.
     * @throws ClassNotFoundException - появляется при отсутствии драйвера для работы с Derby.
     * @throws SQLException - появляется при невозможности подключиться к БД.
     */
    public static void init() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        try {
            connection = DriverManager.getConnection(CREATE);
            connection.createStatement().execute("create table highscores(id varchar(40), login varchar(20), " +
                    "date varchar(40), score INTEGER, time INTEGER )");
        } catch (Exception ex) {
            connection = DriverManager.getConnection(OPEN);
        }
    }

    /**
     * Добавление новых данных в БД.
     * @param name - имя игрока.
     * @param score - счет игрока.
     * @param time - длительность игры.
     */
    public static void addNewData(String name, int score, int time) {
        try {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            df.setTimeZone(TimeZone.getTimeZone("Среднее время по Гринвичу"));
            System.out.println(df.getTimeZone().getDisplayName());

            System.out.println("[DB] Date and time in UTC-0: " + df.format(date));
            connection.createStatement().execute("insert into highscores values" + "('" + UUID.randomUUID() +
                    "','" + name + "','" + df.format(date) + "'," + score + "," + time + ")");
            System.out.println("[DB] Data successfully added to database <playerdata>");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Метод, возвращающий все данные из БД.
     * @param timeZone - временная зона игрока.
     * @return - список всех записей в БД.
     * @throws SQLException - прокидывается при ошибке подключения к БД.
     * @throws ParseException - прокидывается при ошибке в парсе данных.
     */
    public static ArrayList<Data> getDataFromBase(String timeZone) throws SQLException, ParseException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SQL_STATEMENT);
        ArrayList<Data> table = new ArrayList<>();
        Data dummy = new Data();

        while (resultSet.next()) {
            dummy.id = resultSet.getString(1);
            dummy.login = resultSet.getString(2);
            dummy.date = resultSet.getString(3);
            if (timeZone != null) {

                Date trueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dummy.date);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone(timeZone));
                dummy.date = df.format(trueDate);
            }
            dummy.score = Integer.parseInt(resultSet.getString(4));
            dummy.time = Integer.parseInt(resultSet.getString(5));
            table.add(dummy);
            dummy = new Data();
        }
        table.sort(new Data());
        return table;
    }

    /**
     * Метод, возвращающий все записи мз БД.
     * @return - строка со всеми данными БД.
     */
    public static String getAllData() {
        try {
            ArrayList<Data> all = getDataFromBase(null);
            StringBuilder data = new StringBuilder();
            for (Data value : all) {
                data.append(value.toString()).append("\n");
            }
            return data.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Метод, возвращающий лишь последние 10 отсортированных записей из БД.
     * @param timeZone - временная зона игрока.
     * @return - строка топом записей из БД.
     */
    public static String getDataForClient(String timeZone) {
        try {
            ArrayList<Data> top = getDataFromBase(timeZone);
            while (top.size() > 10) {
                top.remove(10);
            }
            StringBuilder str = new StringBuilder();
            for (Data data : top) {
                str.append(data.getPrettyString()).append("\b");
            }
            if (str.toString().equals("")) {
                return null;
            }
            return str.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

/**
 * Класс для работы с данными из БД.
 */
class Data implements Comparator<Data> {
    String id;
    String login;
    String date;
    int score;
    int time;

    /**
     * Метод для сортировки класса.
     * @param f - первый объект.
     * @param s - второй объект.
     * @return - результат сравнения.
     */
    @Override
    public int compare(Data f, Data s) {
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(f.date);
            Date dateTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s.date);
            // Сравниваем по времени.
            if (dateTime.equals(dateTime2)) {
                // Сравниваем по времени игры.
                if (f.time == s.time) {
                    // Сравниваем по очкам.
                    if (f.score > s.score) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    // Сравниваем по времени игры.
                    if (f.time > s.time) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            } else {
                return -1 * dateTime.compareTo(dateTime2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Переопределенный метод перевода инстанса класса в строку.
     * @return - строка.
     */
    @Override
    public String toString() {
        return id + "\t" + login + "\t" + date + "\t" + score + "\t" + time;
    }

    /**
     * Метод, возвращающий упрощенный вид строки инстанца класса.
     * @return - строка с меньшим количеством полей класса.
     */
    public String getPrettyString() {
        return String.format("%8s", login) + " | " + date + " | " + String.format("%8s", score) + " | " + String.format("%12s", time);
    }
}
