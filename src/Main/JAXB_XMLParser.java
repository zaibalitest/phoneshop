package Main;

// Note: The xsd schema viewer (in Eclipse) might report errors if you try to view your schemas with an 
// active internet connection, if so: view (and edit) your xml schemas offline please 

// This class uses the JAXB library to automatically read a xml file and 
// generate objects containing this data from pre-compiled classes

// Remember to generate the classes first from your xml schema and import them into your project which  
// might require a 'refresh' of your project directory (context menu > refresh)

// Generate the classes automatically with: Opening a command console and type:
// Path to YOUR-PROJECTROOT-IN-WORKSPACE\xjc.bat yourschemaname.xsd -d src -p yourclasspackagename


import java.io.*;
import javax.xml.bind.*;

//This is a candidate for a name change because you wont deal with a library any more in your conversion


public class JAXB_XMLParser {

	private JAXBContext jaxbContext = null;     // generate a context to work in with JAXB											   
	private Unmarshaller unmarshaller = null;   // unmarshall = genrate objects from an xml file												
	
	// This is a candidate for a name change because you wont deal with a library any more in your conversion
	private PhoneShop myPhoneShop = null;            // the main object containing all data

	public JAXB_XMLParser() {

		try {
			jaxbContext = JAXBContext.newInstance("Main");  // Package that contains outer classes
			unmarshaller = jaxbContext.createUnmarshaller();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
	
	// Instance objects and return a list with this objects in it
	public PhoneShop loadXML(InputStream fileInputStream) {

		try {
			Object xmlToolObject = unmarshaller.unmarshal(fileInputStream);

			if (myPhoneShop == null) {

				// generate the myPhoneShop object that contains all info from the xml document
				myPhoneShop = (PhoneShop) (((JAXBElement) xmlToolObject).getValue());
				// The above (Library) is a candidate for a name change because you wont deal with 
				// a library any more in your conversion
				
				return myPhoneShop; // return Library Object
			}
		} // try

		catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
