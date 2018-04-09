import java.nio.file.Path;
import java.io.File;
import java.io.IOException;

//File Reading\Writing
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
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

	public DotVirtualBox(Path path, String output)
	{
		this.path = path;
		gatherData(output);
	}
	public void gatherData(String output)
	{
		File dotVirtualBoxFile = path.toFile();
		if (dotVirtualBoxFile.exists())
		{
			bw = new BufferedWriter(new FileWriter(output + "/" + dotVirtualBoxFile.getName())
			try
			{
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
						System.out.println("SystemProperties");
						NodeList systemProp = doc.getElementsByTagName("SystemProperties");
						System.out.println("\tdefaultMachineFolder: " + ((Element)systemProp.item(0)).getAttribute("defaultMachineFolder"));
						

						//Parse ExtraData Items
						System.out.println("ExtraData");
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
										System.out.println("\t" + eElement.getAttribute("name") + ": ");
									    values = eElement.getAttribute("value").split(",");
										for (String value: values)
										{
											System.out.println("\t\t" + value);
										}
										break;
									case "GUI/RecentListCD":
										System.out.println("\t" + eElement.getAttribute("name") + ": ");
									    values = eElement.getAttribute("value").split(",");
										for (String value: values)
										{
											System.out.println("\t\t" + value);
										}
										break;
									case "GUI/RecentListHD":
										System.out.println("\t" + eElement.getAttribute("name") + ": ");
									    values = eElement.getAttribute("value").split(",");
										for (String value: values)
										{
											System.out.println("\t\t" + value);
										}
										break;
									case "GUI/UpdateDate":
										System.out.println("\t" + eElement.getAttribute("name") + ": ");
									    values = eElement.getAttribute("value").split(", ");
										System.out.print("\t\t");
										for (String value: values)
										{
											System.out.print(value + " ");
										}
										System.out.println();
										break;
									//default: System.out.println("\t" + eElement.getAttribute("name") + ": " + eElement.getAttribute("value"));
								}
							}
						}

						//Parse Machine Registry
						System.out.println("Machine Registry");
						NodeList machines = doc.getElementsByTagName("MachineEntry");
						for (int n = 0; n < machines.getLength(); n++)
						{
							node = machines.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								System.out.println("\t" + eElement.getAttribute("src"));
							}
						}

						//Parse Netservice Registry
						System.out.println("Netservice Registry");						
						System.out.println("\tDHCP Servers");
						NodeList dhcpservers = doc.getElementsByTagName("DHCPServer");
						for (int n = 0; n < dhcpservers.getLength(); n++)
						{
							node = dhcpservers.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								System.out.println("\t\t" + eElement.getAttribute("networkName"));
								System.out.println("\t\t\t IP Address: " + eElement.getAttribute("IPAddress"));
								System.out.println("\t\t\t Network Mask: " + eElement.getAttribute("networkMask"));
								System.out.println("\t\t\t Lower IP: " + eElement.getAttribute("lowerIP"));
								System.out.println("\t\t\t Upper IP: " + eElement.getAttribute("upperIP"));
							}
						}
						System.out.println("\tNAT Networks");
						NodeList natNetworks = doc.getElementsByTagName("NATNetwork");
						for (int n = 0; n < natNetworks.getLength(); n++)
						{
							node = natNetworks.item(n);
							if (node.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) node;
								System.out.println("\t\t" + eElement.getAttribute("networkName"));
								System.out.println("\t\t\t Network : " + eElement.getAttribute("network"));
								System.out.println("\t\t\t IPv6 Prefix: " + eElement.getAttribute("ipv6prefix"));
							}
						}
					}
				}
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