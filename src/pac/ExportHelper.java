package pac;

/**
 Author: Leonid Yaremchuk
 Date: 2012-09-10
 Company: http://leonid.pro/
 */

import java.sql.*;

public class ExportHelper {
/*
    private static final String dbDriver = "transbase.jdbc.Driver";
    private static final String dbUrl = "jdbc:transbase://192.168.1.106/";
    private static final String dbDatabase = "TECDOC_CD_2_2013";
    private static final String dbUser = "tecdoc";
    private static final String dbPassword = "tcd_error_0";
    private Connection connection = null;
    private Connection mysqlConnection = null;
    private static final int ukraineCode = 210;
    private static final int russianId = 16;

    public ExportHelper(Connection connection) {
        try {
            this.mysqlConnection = connection;
            Class.forName(dbDriver);
            this.connection = DriverManager.getConnection(dbUrl + dbDatabase, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void printSysTable() {
        Statement st;
        try {
            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT * FROM systable");
            while (result.next()) {
                String tableName = result.getString(1);

                // Just TecDoc
                if (tableName.indexOf("TOF_") != -1) {
                    System.out.println(tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportManufacturers() {

        final String tableName = "TOF_MANUFACTURERS";
        final String mysqlTable = "tof_manufacturers";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id int(11) PRIMARY KEY, " +
                "passenger_car TINYINT, " +
                "commercial_vehicle TINYINT, " +
                "axle TINYINT, " +
                "engine TINYINT, " +
                "engine_type TINYINT, " +
                "code VARCHAR(20), " +
                "brand VARCHAR(100), " +
                "number SMALLINT" +
                ")";


        Statement st;
        Statement mysqlSt;
        try {

            System.out.println("Export manufacturers");

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT MFA_ID, MFA_PC_MFC, MFA_CV_MFC," +
                    " MFA_AXL_MFC, MFA_ENG_MFC, MFA_ENG_TYP, MFA_MFC_CODE, MFA_BRAND," +
                    " MFA_MF_NR FROM " + tableName + " WHERE " +
                    " MFA_CV_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1 OR" +
                    " MFA_PC_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1");

            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);
            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            exportTableData(result, numberOfColumns, mysqlTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exportTableData(ResultSet result, int numberOfColumns, String table) {
        try {
            int count = 0;
            int counter = 0;
            result.setFetchSize(2000);
            while (true) {
                String sql = "INSERT INTO " + table + " VALUES";
                while (count != 2000 && result.next()) {

                    if (count != 0) {
                        sql += ',';
                    }
                    sql += "(";
                    for (int i = 1; i <= numberOfColumns; i++) {
                        if (result.getObject(i) == null) {
                            sql += "NULL";
                        } else {
                            sql += "'" + cleanString(result.getString(i)) + "'";
                        }
                        if (i != numberOfColumns) {
                            sql += ", ";
                        } else {
                            sql += " ";
                        }
                    }
                    sql += ")";
                    count++;
                    counter++;
                }
                if (count > 0) {
                    Statement st = mysqlConnection.createStatement();
                    st.executeUpdate(sql);
                    st.close();
                    System.out.println(counter);
                    count = 0;
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void exportModels() {

        final String tableName = "TOF_MODELS";
        final String tableCountry = "TOF_COUNTRY_DESIGNATIONS";
        final String tableDescriptions = "TOF_DES_TEXTS";
        final String mysqlTable = "tof_models";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id INT(11) PRIMARY KEY, " +
                "manufacturer_id int(11), " +
                "description_id int(11), " +
                "start_date int(6), " +
                "end_date int(6), " +
                "passenger_car TINYINT, " +
                "commercial_vehicle TINYINT, " +
                "axle TINYINT, " +
                "description VARCHAR(255)" +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (manufacturer_id)"
        };


        Statement st;
        Statement mysqlSt;
        try {

            System.out.println("Export models");

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT MOD_ID, MOD_MFA_ID, MOD_CDS_ID," +
                    " MOD_PCON_START, MOD_PCON_END, MOD_PC, MOD_CV, MOD_AXL, TEX_TEXT " +
                    " FROM " + tableName + ", " + tableCountry + ", " + tableDescriptions + " WHERE" +
                    " (MOD_PC_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1 OR" +
                    " MOD_CV_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1) AND" +
                    " CDS_LNG_ID = " + russianId + " AND CDS_TEX_ID = TEX_ID AND MOD_CDS_ID = CDS_ID" +
                    " AND  CDS_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1");

            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportTypes() {

        final String tableName = "TOF_TYPES";
        final String tableCountry = "TOF_COUNTRY_DESIGNATIONS";
        final String tableDescriptions = "TOF_DES_TEXTS";
        final String mysqlTable = "tof_types";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id int(11) PRIMARY KEY," +
                "model_id int(11)," +
                "start_date int(6)," +
                "end_date int(6)," +
                "description varchar(100)," +
                "capacity float(5,1), " +
                "capacity_hp_from int(5)," +
                "capacity_kw_from int(5)" +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (model_id)"
        };

        Statement st;
        Statement mysqlSt;
        try {

            System.out.println("Export types");

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT TYP_ID, TYP_MOD_ID,  TYP_PCON_START, TYP_PCON_END, TEX_TEXT, TYP_LITRES, TYP_HP_FROM, TYP_KW_FROM" +
                    " FROM " + tableName + ", " + tableCountry + ", " + tableDescriptions + " WHERE" +
                    " (TYP_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1 OR" +
                    " TYP_LA_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1) AND" +
                    " CDS_LNG_ID = " + russianId + " AND CDS_TEX_ID = TEX_ID AND TYP_CDS_ID = CDS_ID" +
                    " AND  CDS_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1");

            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportArticles() {

        final String tableName = "TOF_ARTICLES";
        final String tableCountry = "TOF_DESIGNATIONS";
        final String tableDescriptions = "TOF_DES_TEXTS";
        final String mysqlTable = "tof_articles_new";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id int(11), " +
                "article_nr VARCHAR(80), " +
                "supplier_id int(11), " +
                "description VARCHAR(1024), " +
                "PRIMARY KEY (id)" +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (supplier_id)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (article_nr)"
        };


        Statement st;
        Statement mysqlSt;
        try {


            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT ART_ID, ART_ARTICLE_NR, ART_SUP_ID, TEX_TEXT" +
                    " FROM " + tableName + ", " + tableCountry + ", " + tableDescriptions + " " +
                    " WHERE ART_CTM SUBRANGE (" + ukraineCode + " CAST INTEGER) = 1 AND " +
                    " DES_LNG_ID = " + russianId + " AND DES_TEX_ID = TEX_ID AND " +
                    " ART_COMPLETE_DES_ID = DES_ID");
            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            exportTableData(result, numberOfColumns, mysqlTable);
            result.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportSuppliers() {

        final String tableName = "TOF_SUPPLIERS";
        final String mysqlTable = "tof_suppliers";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id int(11), " +
                "brand VARCHAR(100), " +
                "alias VARCHAR(100), " +
                "supplier_nr int(11), " +
                "PRIMARY KEY (id)" +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (brand)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (supplier_nr)"
        };


        Statement st;
        Statement mysqlSt;
        try {


            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT DISTINCT SUP_ID, SUP_BRAND, SUP_BRAND, SUP_SUPPLIER_NR" +
                    " FROM " + tableName);
            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            exportTableData(result, numberOfColumns, mysqlTable);
            result.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportArticlesLookup() {
        final String tableName = "TOF_ART_LOOKUP";
        final String mysqlTable = "tof_articles_lookup_new";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "article_id int(11), " +
                "search varchar(105), " +
                "display varchar(105), " +
                "article_type smallint(11), " +
                "brand_id int(11) " +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (article_id)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (search)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (article_type)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (brand_id)"
        };

        Statement st;
        Statement mysqlSt;
        try {
            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT ARL_ART_ID, ARL_SEARCH_NUMBER, ARL_DISPLAY_NR, ARL_KIND, ARL_BRA_ID" +
                    " FROM " + tableName + " ORDER BY ARL_ART_ID");

            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);
            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
            result.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportSearchTree() {
        final String tableName = "TOF_SEARCH_TREE";
        final String mysqlTable = "tof_search_tree";
        final String tableCountry = "TOF_DESIGNATIONS";
        final String tableDescriptions = "TOF_DES_TEXTS";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id INT(11), " +
                "parent_id INT(11), " +
                "type SMALLINT(2), " +
                "level SMALLINT(2), " +
                "node_number INT(11), " +
                "sort INT(11), " +
                "text VARCHAR(255), " +
                "PRIMARY KEY (id)" +
                ")";
        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (level)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (sort)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (type)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (parent_id)"
        };

        Statement st;
        Statement mysqlSt;
        try {
            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT DISTINCT STR_ID, STR_ID_PARENT, STR_TYPE, STR_LEVEL, STR_SORT, STR_NODE_NR, TEX_TEXT" +
                    " FROM " + tableName + ", " + tableCountry + ", " + tableDescriptions + " " +
                    " WHERE DES_LNG_ID = " + russianId + " AND DES_TEX_ID = TEX_ID AND " +
                    " DES_ID=STR_DES_ID");
            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);
            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
            result.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportLinkGenericArticleSearchTree() {
        final String tableName = "TOF_LINK_GA_STR";
        final String mysqlTable = "tof_link_generic_article_search_tree";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "search_tree_id INT(11), " +
                "generic_article_id INT(11)" +
                ")";
        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (search_tree_id)",
                "ALTER TABLE " + mysqlTable + " ADD INDEX (generic_article_id)"
        };

        Statement st;
        Statement mysqlSt;
        try {
            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT DISTINCT LGS_STR_ID, LGS_GA_ID" +
                    " FROM " + tableName);
            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);
            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
            result.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportCriteria() {

        final String tableName = "TOF_CRITERIA";
        final String tableCountry = "TOF_DESIGNATIONS";
        final String tableDescriptions = "TOF_DES_TEXTS";
        final String mysqlTable = "tof_criteria";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "id int(11) PRIMARY KEY," +
                "description varchar(255)," +
                "unit int(11)," +
                "type varchar(6)," +
                "is_interval int(5)," +
                "successor int(11) " +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (id)"
        };

        Statement st;
        Statement mysqlSt;
        try {

            System.out.println("Export Criteria");

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT CRI_ID, TEX_TEXT, CRI_UNIT_DES_ID, CRI_TYPE, CRI_IS_INTERVAL, CRI_SUCCESSOR" +
                    " FROM " + tableName + ", " + tableCountry + ", " + tableDescriptions + " WHERE" +
                    " DES_LNG_ID = " + russianId + " AND DES_TEX_ID = TEX_ID AND CRI_SHORT_DES_ID = DES_ID");
            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportArticlesAttributes() {

        final String tableName = "TOF_ARTICLE_INFO";
        final String tableCountry = "TOF_TEXT_MODULES";
        final String tableDescriptions = "TOF_TEXT_MODULE_TEXTS";
        final String mysqlTable = "tof_article_info";

        final String sqlDropTable = "DROP TABLE IF EXISTS " + mysqlTable;
        final String sqlCreateTable = " CREATE TABLE IF NOT EXISTS " + mysqlTable + " (" +
                "article_id int(11)," +
                "sort int(11)," +
                "description TEXT" +
                ")";

        final String[] sqlIndexes = {
                "ALTER TABLE " + mysqlTable + " ADD INDEX (article_id)"
        };

        Statement st;
        Statement mysqlSt;
        try {

            System.out.println("Export Info");

            st = connection.createStatement();
            ResultSet result = st.executeQuery("SELECT AIN_ART_ID, AIN_SORT, TMT_TEXT" +
                    " FROM " + tableName + ", " + tableCountry + ", " + tableDescriptions + " WHERE" +
                    " TMO_LNG_ID = " + russianId + " AND TMO_TMT_ID = TMT_ID AND AIN_TMO_ID = TMO_ID");
            ResultSetMetaData metaResult = result.getMetaData();
            int numberOfColumns = metaResult.getColumnCount();

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlDropTable);

            mysqlSt = mysqlConnection.createStatement();
            mysqlSt.executeUpdate(sqlCreateTable);

            for (String sql : sqlIndexes) {
                mysqlSt = mysqlConnection.createStatement();
                mysqlSt.executeUpdate(sql);
            }

            exportTableData(result, numberOfColumns, mysqlTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
}