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
						NodeList children = doc.getDocumentElement().getChildNodes();
						Node node;
						for (int n = 0; n < children.getLength(); n++)
						{
							node = children.item(n);
							if (!node.hasChildNodes())
								System.out.println("leaf child");
							else
								System.out.println("child");
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