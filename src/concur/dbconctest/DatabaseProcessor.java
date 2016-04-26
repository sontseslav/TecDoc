package concur.dbconctest;

import java.sql.*;
import java.util.Random;

/**
 * Created by coder on 08.04.16.
 */
public class DatabaseProcessor {
    private Connection conn;
    private final String INPUT_TABLE = "input";
    private final String OUTPUT_TABLE = "output";

    public DatabaseProcessor(Connection conn){
        this.conn = conn;
    }

    public void fillTable(){
        if (isTableExists(INPUT_TABLE)) return;
        long startTime = System.currentTimeMillis();
        try {
            try (Statement st = conn.createStatement()) {
                conn.setAutoCommit(false);
                String queryCreateTable = "CREATE TABLE " + INPUT_TABLE + " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "val1 INT NOT NULL, val2 INT NOT NULL)";
                st.execute(queryCreateTable);
                String queryInsertValues = "INSERT INTO "+INPUT_TABLE+" VALUES (?,?,?)";
                try(PreparedStatement prepStat = conn.prepareStatement(queryInsertValues)){
                    int rowsToSet = (int)Math.pow(10,7);
                    int rowsSet;
                    Random rand = new Random();
                    for(rowsSet=0;rowsSet<rowsToSet;rowsSet++){
                        prepStat.setNull(1,Types.INTEGER);
                        prepStat.setInt(2,rand.nextInt(483647));
                        prepStat.setInt(3,rand.nextInt(483647));
                        prepStat.executeUpdate();
                        if((rowsSet % 5000) == 0){
                            System.out.println(rowsSet);
                            conn.commit();
                        }
                    }
                    if (rowsSet != rowsToSet){
                        System.out.printf("An error occurred during execution:%n" +
                                "rows to set - %d%n" +
                                "rows have been set - %d%n",rowsToSet,rowsSet);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                conn.rollback();
            } finally {
                conn.commit();
                conn.setAutoCommit(true);
                System.out.printf("Elapsed time: %d%n", System.currentTimeMillis() - startTime);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean isTableExists(String table){
        try(Statement st = conn.createStatement()){
            String queryTableExists = "SELECT COUNT(*) " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = '"+conn.getCatalog()+"' " +
                    "AND table_name = '"+table+"'";
            try(ResultSet rs = st.executeQuery(queryTableExists)){
                rs.next();
                return (rs.getInt("COUNT(*)") > 0);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true;//never be executed
    }

    public void summarize(){
        long startTime = System.currentTimeMillis();
        try (Statement st = conn.createStatement()) {
            if (isTableExists(OUTPUT_TABLE)) {
                st.execute("DROP TABLE " + OUTPUT_TABLE);
            }
            String queryCreateTable = "CREATE TABLE " + OUTPUT_TABLE + " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "SUM BIGINT NOT NULL, is_palindrome bool NOT NULL)";
            st.execute(queryCreateTable);
            String queryInsertValues = "INSERT INTO " + OUTPUT_TABLE + " (SUM, is_palindrome) VALUES (?,?)";
            ResultSet rs = st.executeQuery("SELECT val1, val2 FROM " + INPUT_TABLE);
            ParallelSummarise parallelSummarise = new ParallelSummarise(queryInsertValues, rs, conn);
            parallelSummarise.exec();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.printf("Elapsed time: %d%n", System.currentTimeMillis() - startTime);
        }
    }
}
