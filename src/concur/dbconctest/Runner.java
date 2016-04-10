package concur.dbconctest;

import java.sql.Connection;
/**
 * Created by coder on 08.04.16.
 */
public class Runner {
    public static void main(String[] args) {
        Connection conn = Connector.connFabric();
        DatabaseProcessor dp = new DatabaseProcessor(conn);
        dp.fillTable();
        dp.summarize();
        Connector.connClose();
    }
}
