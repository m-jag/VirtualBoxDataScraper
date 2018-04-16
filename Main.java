import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.regex.Pattern;
/**
* @author Matthew Jagodzinski
* @version 1.0
**/
public class Main implements Runnable
{
    public static void main(String[] args)
    {
        new Main().run();
    }

    public void run()
    {
        File outputFolder;
        int analysisNum = 1;
        while ( (outputFolder = new File(System.getProperty("user.dir") + "/Virtual Box Analysis #" + analysisNum)).exists())
        {
            analysisNum++;
        }
        outputFolder.mkdir();
        String outputStr = outputFolder.toString();
        getUserPaths(false).forEach((path) -> parseDotVirtualBox(path, outputStr));
    }

    /**
     * Returns an ArrayList of paths to the user directories of non-default
     * users at the specified path.
     *
     * @param drive_path  pass the path to the users folder as a string
     * @return            an arraylist of paths to the user folders from the specified drive
     */
    
    public ArrayList<Path> getUserPaths(Path drive_path, boolean include_default_users)
    {
        ArrayList<Path> userPaths = new ArrayList<Path>();
        final ArrayList<String> defaultUsers = new ArrayList<String>();
        //Build default user lists
        defaultUsers.add("All Users");
        defaultUsers.add("Default");
        defaultUsers.add("Default User");
        defaultUsers.add("Default.migrated");
        defaultUsers.add("Public");

        try
        {
            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) throws IOException {
                    return Files.isDirectory(entry) && (include_default_users || !defaultUsers.contains(entry.getFileName().toString()));
                }
            };
            Files.newDirectoryStream(drive_path, filter)
            .forEach((path) -> {
                userPaths.add(path);
            });
        }
        catch(IOException ie)
        {
            System.out.println("IOException");
            ie.printStackTrace();
        }
        return userPaths;
    }

    public ArrayList<Path> getUserPaths(boolean include_default_users)
    {
        ArrayList<Path> userPaths = new ArrayList<Path>();
        File[] roots = File.listRoots();
        for (File root: roots)
        {
            File userpath = new File(root.toString() + "Users");
            if (userpath.exists() && userpath.isDirectory())
            {
                userPaths.addAll(getUserPaths(userpath.toPath(), include_default_users));
            }
        }
        return userPaths;
    }

    public void parseDotVirtualBox(Path userpath, String outputPath)
    {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] path = userpath.toString().split(pattern);
        File outputFolder;
        int analysisNum = 1;

        outputFolder = new File(outputPath.toString() + "/Analysis Of " +  path[0].split(":")[0] + " " + path[2]);
        while (outputFolder.exists())
        {
            analysisNum++;
            outputFolder = new File(outputPath.toString() + "/Analysis Of " +  path[0].split(":")[0] + " " + path[2] + " " + analysisNum);
        }
        outputFolder.mkdir();

        System.out.println("Parsing : " +  outputFolder.toString());
        File dotVirtualBox = new File(userpath.toString() + "/.VirtualBox");
        System.out.println("\tVBox : " +  dotVirtualBox.toString());
        if (dotVirtualBox.exists())
        {
            DotVirtualBox dvb = new DotVirtualBox(dotVirtualBox.toPath(), outputFolder.toString());
            parseDefaultMachineFolder(dvb.getDefaultMachineFolder(), outputFolder.toString());
        }
        else
        {
            System.out.println("\t.VirtualBox doesn't exist");
        }
    }

    public void parseDefaultMachineFolder(String defaultMachineFolder, String outputPath)
    {
        DefaultMachineFolder dmf = new DefaultMachineFolder(defaultMachineFolder, outputPath);
    }
}