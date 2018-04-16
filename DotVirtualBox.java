import java.nio.file.Path;
import java.io.File;
import java.io.IOException;

//File Reading\Writing
import java.io.BufferedWriter;
import java.io.FileWriter;
//XML
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DotVirtualBox
{

	private Path path;
	private BufferedWriter bw;
	private String defaultMachineFolder;

	public DotVirtualBox(Path path, String output)
	{
		this.path = path;
		gatherData(output);
	}
	public String getDefaultMachineFolder()
	{
		return defaultMachineFolder;
	}
	public void gatherData(String output)
	{
		File dotVirtualBoxFile = path.toFile();
		if (dotVirtualBoxFile.exists())
		{
			try
			{
				bw = new BufferedWriter(new FileWriter(output + "/DotVirtualBox.txt"));
				// Check VirtualBox.xml
				File virtualBoxXML = new File(path.toString() + "/VirtualBox.xml");
				if (virtualBoxXML.exists() && virtualBoxXML.isFile())
				{
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(virtualBoxXML);
					doc.getDocumentElement().normalize();

					Element root = doc.getDocumentElement();
					if (root.hasChildNodes())
					{
						//Grab default machine folder
						bw.write("SystemProperties\n");
						NodeList systemProp = doc.getElementsByTagName("SystemProperties");
						defaultMachineFolder =  ((Element)systemProp.item(0)).getAttribute("defaultMachineFolder");
						bw.write("\tdefaultMachineFolder: " + defaultMachineFolder + "\n");
						

						//Parse ExtraData Items
						bw.write("ExtraData\n");
						NodeList extraData = doc.getElementsByTagName("ExtraDataItem");
						Node node;
						for (int n = 0; n < extraData.getLength(); n++)
						{
							node = extraData.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								String[] values;
								switch (eElement.getAttribute("name"))
								{
									case "GUI/GroupDefinitions/":
										bw.write("\t" + eElement.getAttribute("name") + ": \n");
									    values = eElement.getAttribute("value").split(",");
										for (String value: values)
										{
											bw.write("\t\t" + value + "\n");
										}
										break;
									case "GUI/RecentListCD":
										bw.write("\t" + eElement.getAttribute("name") + ": \n");
									    values = eElement.getAttribute("value").split(",");
										for (String value: values)
										{
											bw.write("\t\t" + value + "\n");
										}
										break;
									case "GUI/RecentListHD":
										bw.write("\t" + eElement.getAttribute("name") + ": \n");
									    values = eElement.getAttribute("value").split(",");
										for (String value: values)
										{
											bw.write("\t\t" + value + "\n");
										}
										break;
									case "GUI/UpdateDate":
										bw.write("\t" + eElement.getAttribute("name") + ": \n");
									    values = eElement.getAttribute("value").split(", ");
										bw.write("\t\t");
										for (String value: values)
										{
											bw.write(value + " ");
										}
										bw.newLine();
										break;
									//default: bw.write("\t" + eElement.getAttribute("name") + ": " + eElement.getAttribute("value") + "\n");
								}
							}
						}

						//Parse Machine Registry
						bw.write("Machine Registry\n");
						NodeList machines = doc.getElementsByTagName("MachineEntry");
						for (int n = 0; n < machines.getLength(); n++)
						{
							node = machines.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								bw.write("\t" + eElement.getAttribute("src") + "\n");
							}
						}

						//Parse Netservice Registry
						bw.write("Netservice Registry\n");						
						bw.write("\tDHCP Servers\n");
						NodeList dhcpservers = doc.getElementsByTagName("DHCPServer");
						for (int n = 0; n < dhcpservers.getLength(); n++)
						{
							node = dhcpservers.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								bw.write("\t\t" + eElement.getAttribute("networkName") + "\n");
								bw.write("\t\t\t IP Address: " + eElement.getAttribute("IPAddress") + "\n");
								bw.write("\t\t\t Network Mask: " + eElement.getAttribute("networkMask") + "\n");
								bw.write("\t\t\t Lower IP: " + eElement.getAttribute("lowerIP") + "\n");
								bw.write("\t\t\t Upper IP: " + eElement.getAttribute("upperIP") + "\n");
							}
						}
						bw.write("\tNAT Networks" + "\n");
						NodeList natNetworks = doc.getElementsByTagName("NATNetwork");
						for (int n = 0; n < natNetworks.getLength(); n++)
						{
							node = natNetworks.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								bw.write("\t\t" + eElement.getAttribute("networkName") + "\n");
								bw.write("\t\t\t Network : " + eElement.getAttribute("network")  + "\n");
								bw.write("\t\t\t IPv6 Prefix: " + eElement.getAttribute("ipv6prefix")  + "\n");
							}
						}
					}
				}
				bw.close();
			}
			catch(IOException ex)
			{
				System.out.println("DotVirtualBox: IOException");
				ex.printStackTrace();
			}
			catch(SAXException ex)
			{
				System.out.println("DotVirtualBox: SAXException");
				ex.printStackTrace();
			}
			catch(ParserConfigurationException ex)
			{
				System.out.println("DotVirtualBox: ParserConfigurationException");
				ex.printStackTrace();
			}
		}
	}
}