package pac;
import java.sql.*;

/**
 * Created by coder on 06.03.16.
 */
public class Test {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String CONNECTION_URL = "jdbc:mysql://localhost/";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "12345";
    private static final String DATABASE = "tecdoc2015q1";
    private static final String TABLE = "tof_manufacturers_ua";
    private static final String characterEncoding = "utf-8";
    private static Connection connection = null;
    private static final String QUERY = "select * from "+DATABASE+"."+TABLE;

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(CONNECTION_URL + DATABASE + "?characterEncoding=utf-8&useUnicode=true",
                USERNAME, PASSWORD);

        String sqlCharset = "SET NAMES utf8 COLLATE utf8_general_ci";
        Statement st = connection.createStatement();
        st.executeQuery(sqlCharset);
        st.executeQuery("SET CHARACTER SET utf8");
        ResultSet rs = connection.createStatement().executeQuery(QUERY);
        if (rs != null) {
            System.out.println("Column Type\t\t Column Name");

            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println(rsmd.getColumnTypeName(i)+"\t\t\t"+rsmd.getColumnName(i));
            }
        }
        /*PreparedStatement ps = connection.prepareStatement("");
        ps.setShort();*/
    }
}
