import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;

//File Reading\Writing
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class DefaultMachineFolder
{
	String path;

	public DefaultMachineFolder(String pathToDefaultMachineFolder, String outputFolder)
	{
		ArrayList<Path> machineFolders = getMachineFolders(pathToDefaultMachineFolder);
		machineFolders.forEach(machineFolder -> parseMachineFolder(machineFolder, outputFolder));
	}

	public ArrayList<Path> getMachineFolders(String pathToDefaultMachineFolder)
	{
		ArrayList<Path> machineFolders = new ArrayList<Path>();

        try
        {
            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) throws IOException {
                    return Files.isDirectory(entry) && Files.isDirectory((new File(entry.toString() + "/Logs")).toPath());
                }
            };
            Files.newDirectoryStream((new File(pathToDefaultMachineFolder)).toPath(), filter)
            .forEach((path) -> {
                machineFolders.add(path);
            });
        }
        catch(IOException ie)
        {
            System.out.println("DefaultMachineFolder: IOException");
            ie.printStackTrace();
        }

    	return machineFolders;
	}

	public void parseMachineFolder(Path machineFolder, String outputFolder)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + "/Report on " + machineFolder.getFileName() + ".txt"));
			ArrayList<Path> logFiles = new ArrayList<Path>();
			Files.newDirectoryStream((new File(machineFolder.toString() + "/Logs")).toPath())
			.forEach((path) ->
			{
				parseLogFile(bw, path);
			});
			bw.close();
		}
		catch(IOException ex)
		{
			System.out.println("MachineFolder: IOException");
			ex.printStackTrace();
		}
	}

	public void parseLogFile(BufferedWriter bw, Path logFile)
	{
		String hostData = "";
		String vmData = "";

		try
		{
			if (logFile.getFileName().toString().contains("VBox.log"))
			{
				bw.write(logFile.getFileName() + "\n");
				BufferedReader br = new BufferedReader(new FileReader(logFile.toString()));
				String line;
				while ((line = br.readLine()) != null)
				{
					if (!line.contains("Guest Log"))
					{
						//Parse host data
						if (line.contains("OS Product:"))
						{
							bw.write("\tOS Product: " + line.split("OS Product: ")[1] + "\n");
						}
						else if (line.contains("OS Release:"))
						{
							bw.write("\tOS Release: " + line.split("OS Release: ")[1] + "\n");
						}
						else if (line.contains("OS Service Pack:") && line.split("OS Service Pack: ").length > 1)
						{
							bw.write("\tOS Service Pack: " + line.split("OS Service Pack: ")[1] + "\n");
						}
						else if (line.contains("Host RAM:"))
						{
							bw.write("\tHost RAM: " + line.split("Host RAM: ")[1].split(", ")[0] + "\n");
						}
						else if (line.contains("Executable:"))
						{
							bw.write("\tExecutable: " + line.split("Executable: ")[1] + "\n");
						}
						else if (line.contains("Package type:"))
						{
							bw.write("\tPackage type: " + line.split("Package type: ")[1] + "\n");
						}
					}
					if (line.contains(" Name") && line.contains("<string>"))
					{
						bw.write("\tVM Name: " + line.split("Name              <string>  = ")[1].split("\\p{Punct}")[1] + "\n");
					}
					else if (line.contains("NumCPUs           <integer> = "))
					{
						bw.write("\tNumCPUs: " + line.split("NumCPUs           <integer> = ")[1].split("\\p{Punct}")[1] + "\n");
					}
					else if (line.contains("MAC"))
					{
						String macAddress = "";
						for(String set: line.split("<bytes>   = ")[1].split("\\p{Punct}")[1].split(" "))
						{
							macAddress += set + "-";
						}
						macAddress = macAddress.substring(0, macAddress.length() - 1);
						bw.write("\tMAC: " + macAddress + "\n");
					}
					else if (line.contains("DNS#"))
					{
						bw.write("\tDNS #" + line.split("DNS#")[1] +  "\n");
					}
				}
				br.close();
			}
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("DefaultMachineFolder: FileNotFoundException");
			ex.printStackTrace();
		}
		catch(IOException ex)
		{
			System.out.println("DefaultMachineFolder: IOException");
			ex.printStackTrace();
		}
	}
}