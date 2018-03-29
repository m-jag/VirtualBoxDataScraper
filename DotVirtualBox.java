import java.nio.file.Path;
import java.io.File;
import java.io.IOException;

//File Reading
import java.io.BufferedReader;
import java.io.FileReader;
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

	public DotVirtualBox(Path path)
	{
		this.path = path;
		gatherData();
	}
	public void gatherData()
	{
		File dotVirtualBoxFile = path.toFile();
		if (dotVirtualBoxFile.exists())
		{
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
					System.out.println("Root element: " + root.getNodeName());
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
								System.out.println("\t" + eElement.getAttribute("name") + ": " + eElement.getAttribute("value"));
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