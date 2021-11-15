package carsharing;

import carsharing.impementation.Database;
import carsharing.impementation.DbManagerService;
import carsharing.interfaces.DatabaseInterface;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseInterface database = new Database(_getDatabaseFilePath(args));
            DbManagerService dbManager = new DbManagerService(database);
            dbManager.start();
        } catch (RuntimeException ex) {
            System.err.printf("Error: %s\n", ex.getMessage());
            System.exit(1);
        }
    }

    private static String _getDatabaseFilePath(String[] args) {

        String databaseFileName = "carsharing";
        if (args.length > 1 && args[0].equals("-databaseFileName")) {
            databaseFileName = args[1];
        }
        String databaseDirPath = "./";
        return databaseDirPath + databaseFileName;
    }
}
