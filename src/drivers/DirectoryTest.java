package drivers;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.Directory;

/**
 *
 * @author Group B
 */
public class DirectoryTest extends Driver
{

    /**
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        // Always the handle of a directory.
        String myHandle = "dir";

        //Change 0.0.0.0 to a more specific ip address range or
        //specific ip address.
        Directory dir = new Directory(myHandle, "0.0.0.0");

        try
        {
            dir.begin();

            while (true)
            {
                System.out.println("Options:");
                System.out.println("1. Show connections");
                System.out.println("2. Remove connections");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        displayConnectionList(dir);
                        break;
                    case "2":
                        dir.removeConnections();
                        break;
                    default:
                        System.err.println("Invalid option.");
                }
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(DirectoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Display list of current connection handle strings for a given directory.
     * @param me Directory to be searched.
     */
    private static void displayConnectionList(Directory me)
    {
        if (!me.hasConnections())
        {
            System.out.println("\n* No portals connected *\n");
            return;
        }

        System.out.println(
                String.format(
                        "Connected portal handles\n%s\n",
                        String.join(", ", me.getConnectionHandles())
                )
        );
    }

    /**
     * Gets string from user.
     * @return Entered string.
     */
    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }
}
