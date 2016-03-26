package Main;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import PhoneShop.*;

public class Reasoner {

	// The main Class Object holding the Domain knowledge

	// Generate the classes automatically with: Opening a command console and
	// type:
	// Path to YOUR-PROJECTROOT-IN-WORKSPACE\xjc.bat yourschemaname.xsd -d src
	// -p yourclasspackagename

	public PhoneShop phoneShop; //This is a candidate for a name change

	public SimpleGUI Myface;

	// The lists holding the class instances of all domain entities
	public List thePhoneShopList = new ArrayList(); //This is a candidate for a name change
	public List thePhoneList = new ArrayList();    //This is a candidate for a name change
	public List thePhoneSaleList = new ArrayList(); //This is a candidate for a name change
	public List thePhoneLeaseList = new ArrayList(); //This is a candidate for a name change
	public List theCustomerList = new ArrayList();  //This is a candidate for a name change
	public List theSalesmenList = new ArrayList(); //This is a candidate for a name change
	public List theRecentThing = new ArrayList();

	// Gazetteers to store synonyms for the domain entities names
	public Vector<String> phoneShopSyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> phoneSyn = new Vector<String>();     //This is a candidate for a name change
	public Vector<String> phoneSaleSyn = new Vector<String>();   //This is a candidate for a name change
	public Vector<String> phoneLeaseSyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> customerSyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> salesmenSyn = new Vector<String>();  //This is a candidate for a name change
	public Vector<String> recentobjectsyn = new Vector<String>();

	public String questiontype = "";         // questiontype selects method to use in a query
	public List classtype = new ArrayList(); // classtype selects which class list to query
	public String attributetype = "";        // attributetype selects the attribute to check for in the query

	public Object Currentitemofinterest; // Last Object dealt with
	public Integer Currentindex;         // Last Index used

	public String tooltipstring = "";
	public String URL = "";              // URL for Wordnet site
	public String URL2 = "";             // URL for Wikipedia entry

	public Reasoner(SimpleGUI myface) {

		Myface = myface; // reference to GUI to update Tooltip-Text
		// basic constructor for the constructors sake :)
	}

	void initknowledge() { // load all the library knowledge from XML

		JAXB_XMLParser xmlHandler = new JAXB_XMLParser(); // we need an instance of our parser

		//This is a candidate for a name change
		File xmlFileToLoad = new File("PhoneShop.xml"); // we need a (CURRENT)  file (xml) to load

		// Init synonmys and typo forms in gazetteers
		phoneShopSyn.add("shop");   	//This is a candidate for a name change
		phoneShopSyn.add("place");		//This is a candidate for a name change
		phoneShopSyn.add("store");	//This is a candidate for a name change
		phoneShopSyn.add("market"); 	//This is a candidate for a name change
		phoneShopSyn.add("phoneshop");		//This is a candidate for a name change

		phoneSyn.add("phone");    //All of the following is a candidate for a name change
		phoneSyn.add("phon");
		phoneSyn.add("mobile");
		phoneSyn.add("mobile phone");
		phoneSyn.add("cell");
		phoneSyn.add("cellphone");
		phoneSyn.add("mob");
		phoneSyn.add("mobil");

		phoneSaleSyn.add("sold"); //All of the following is a candidate for a name change
		phoneSaleSyn.add("sell");
		phoneSaleSyn.add("phones sold");
		phoneSaleSyn.add("phones sell");
		phoneSaleSyn.add("phone sold");

		phoneLeaseSyn.add("phone for lease"); //All of the following is a candidate for a name change
		phoneLeaseSyn.add("lease");
		phoneLeaseSyn.add("for leasing");
		phoneLeaseSyn.add("leasing");
		phoneLeaseSyn.add("leasing plan");
		phoneLeaseSyn.add("installments");
		phoneLeaseSyn.add("installment option");
		phoneLeaseSyn.add("lend");
		phoneLeaseSyn.add("lending");
		phoneLeaseSyn.add("lending options");

		customerSyn.add("customer");   //All of the following is a candidate for a name change
		customerSyn.add("customer name");   //All of the following is a candidate for a name change
		customerSyn.add("customers");   //All of the following is a candidate for a name change

		salesmenSyn.add("salesman");  //All of the following is a candidate for a name change
		salesmenSyn.add("sales person");
		salesmenSyn.add("shop keeper");
		salesmenSyn.add("attendant");

		recentobjectsyn.add(" this");   //All of the following is a candidate for a name change
		recentobjectsyn.add(" that");
		recentobjectsyn.add(" him");
		recentobjectsyn.add(" her");	// spaces to prevent collision with "wHERe"	
		recentobjectsyn.add(" it");

		try {
			FileInputStream readThatFile = new FileInputStream(xmlFileToLoad); // initiate input stream

			phoneShop = xmlHandler.loadXML(readThatFile);

			// Fill the Lists with the objects data just generated from the xml
			thePhoneList = phoneShop.getPhones();  		//This is a candidate for a name change
			thePhoneSaleList = phoneShop.getPhonesSold(); 	//This is a candidate for a name change
			thePhoneLeaseList = phoneShop.getPhonesLeased(); 	//This is a candidate for a name change
			theCustomerList = phoneShop.getCustomers(); 	//This is a candidate for a name change
			theSalesmenList = phoneShop.getSalesmen(); 	//This is a candidate for a name change
			thePhoneShopList.add(phoneShop);             // force it to be a List, //This is a candidate for a name change

			System.out.println("List reading");
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in init");
		}
	}

	public  Vector<String> generateAnswer(String input) { // Generate an answer (String Vector)

		Vector<String> out = new Vector<String>();
		out.clear();                 // just to make sure this is a new and clean vector
		
		questiontype = "none";

		Integer Answered = 0;        // check if answer was generated

		Integer subjectcounter = 0;  // Counter to keep track of # of identified subjects (classes)
		
		// Answer Generation Idea: content = Questiontype-method(classtype class) (+optional attribute)

		// ___________________________ IMPORTANT _____________________________

		input = input.toLowerCase(); // all in lower case because thats easier to analyse
		
		// ___________________________________________________________________

		String answer = "";          // the answer we return

		// ----- Check for the kind of question (number, location, etc)------------------------------

		if (input.contains("how many")){questiontype = "amount"; input = input.replace("how many", "<b>how many</b>");} 
		if (input.contains("number of")){questiontype = "amount"; input = input.replace("number of", "<b>number of</b>");}
		if (input.contains("amount of")){questiontype = "amount"; input = input.replace("amount of", "<b>amount of</b>");} 
		if (input.contains("count")){questiontype = "amount"; input = input.replace("count", "<b>count</b>");}

		if (input.contains("what kind of")){questiontype = "list"; input = input.replace("what kind of", "<b>what kind of</b>");}
		if (input.contains("list all")){questiontype = "list"; input = input.replace("list all", "<b>list all</b>");}
		if (input.contains("diplay all")){questiontype = "list"; input = input.replace("diplay all", "<b>diplay all</b>");}

		if (input.contains("is there a")){questiontype = "checkfor"; input = input.replace("is there a", "<b>is there a</b>");}
		if (input.contains("i am searching")){questiontype = "checkfor"; input = input.replace("i am searching", "<b>i am searching</b>");}
		if (input.contains("i am looking for")){questiontype = "checkfor"; input = input.replace("i am looking for", "<b>i am looking for</b>");}
		if (input.contains("do you have")&&!input.contains("how many")){questiontype = "checkfor";input = input.replace("do you have", "<b>do you have</b>");}
		if (input.contains("i look for")){questiontype = "checkfor"; input = input.replace("i look for", "<b>i look for</b>");}
		if (input.contains("is there")){questiontype = "checkfor"; input = input.replace("is there", "<b>is there</b>");}

		if (input.contains("where") 
				|| input.contains("can't find")
				|| input.contains("can i find") 
				|| input.contains("way to"))

		{
			questiontype = "location";
			System.out.println("Find Location");
		}
		if (input.contains("can i lend") 
				|| input.contains("can i borrow")
				|| input.contains("can i get the book")
				|| input.contains("am i able to")
				|| input.contains("could i lend") 
				|| input.contains("i want to lend")
				|| input.contains("i want to borrow"))

		{
			questiontype = "intent";
			System.out.println("Find BookAvailability");
		}
		
		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("cool thank")) 			

		{
			questiontype = "farewell";
			System.out.println("farewell");
		}


		// ------- Checking the Subject of the Question --------------------------------------

		for (String aPhoneSyn : phoneSyn) {   //This is a candidate for a name change
			if (input.contains(aPhoneSyn)) {    //This is a candidate for a name change
				classtype = thePhoneList;             //This is a candidate for a name change
				input = input.replace(aPhoneSyn, "<b>" + aPhoneSyn + "</b>");
				subjectcounter = 1;
				System.out.println("Class type Phone recognised.");
			}
		}
		for (String aPhoneSaleSyn : phoneSaleSyn) {  //This is a candidate for a name change
			if (input.contains(aPhoneSaleSyn)) {   //This is a candidate for a name change
				classtype = thePhoneSaleList;            //This is a candidate for a name change
				input = input.replace(aPhoneSaleSyn, "<b>" + aPhoneSaleSyn + "</b>");
				subjectcounter = 1;
				System.out.println("Class type Phone Sale recognised.");
			}
		}
		for (String aPhoneLeaseSyn : phoneLeaseSyn) {  //This is a candidate for a name change
			if (input.contains(aPhoneLeaseSyn)) {   //This is a candidate for a name change
				classtype = thePhoneLeaseList;            //This is a candidate for a name change
				input = input.replace(aPhoneLeaseSyn, "<b>" + aPhoneLeaseSyn + "</b>");
				subjectcounter = 1;
				System.out.println("Class type Phone Lease recognised.");
			}
		}
		for (String aCustomerSyn : customerSyn) {  //This is a candidate for a name change
			if (input.contains(aCustomerSyn)) {   //This is a candidate for a name change
				classtype = theCustomerList;            //This is a candidate for a name change
				input = input.replace(aCustomerSyn, "<b>" + aCustomerSyn + "</b>");
				subjectcounter = 1;
				System.out.println("Class type Customer recognised.");
			}
		}
		for (String aSalesmanSyn : salesmenSyn) {  //This is a candidate for a name change
			if (input.contains(aSalesmanSyn)) {   //This is a candidate for a name change
				classtype = theSalesmenList;            //This is a candidate for a name change
				input = input.replace(aSalesmanSyn, "<b>" + aSalesmanSyn + "</b>");
				subjectcounter = 1;
				System.out.println("Class type Salesman recognised.");
			}
		}
		
		if(subjectcounter == 0)
		{
			for (String aRecentobjectsyn : recentobjectsyn) {
				if (input.contains(aRecentobjectsyn)) {
					classtype = theRecentThing;
					input = input.replace(aRecentobjectsyn, "<b>" + aRecentobjectsyn + "</b>");
					subjectcounter = 1;
					System.out.println("Class type recognised as" + aRecentobjectsyn);
				}
			}
		}
		// More than one subject in question + Library ...
		// "Does the Library has .. Subject 2 ?"

		System.out.println("subjectcounter = "+subjectcounter);

		for (String aPhoneShopSyn : phoneShopSyn) {  //This is a candidate for a name change
			if (input.contains(aPhoneShopSyn)) {   //This is a candidate for a name change
				// Problem: "How many Books does the Library have ?" -> classtype = Library
				// Solution:
				if (subjectcounter == 0) { // Library is the first subject in the question
					input = input.replace(aPhoneShopSyn, "<b>" + aPhoneShopSyn + "</b>");
					classtype = thePhoneShopList;        //This is a candidate for a name change
					System.out.println("class type Phone Shop recognised");
				}
			}
		}

		// Compose Method call and generate answerVector

		if (questiontype.equals("amount")) { // Number of Subject
			Integer numberof = Count(classtype);
			answer=("The number of "
					+ classtype.get(0).getClass().getSimpleName() + "s is "
					+ numberof + ".");
			Answered = 1; // An answer was given
		}

		if (questiontype.equals("list")) { // List all Subjects of a kind
			answer=("You asked for the listing of all "
					+ classtype.get(0).getClass().getSimpleName() + "s. <br>"
					+ "We have the following "
					+ classtype.get(0).getClass().getSimpleName() + "s:"
					+ ListAll(classtype));
			Answered = 1; // An answer was given
		}

		if (questiontype.equals("checkfor")) { // test for a certain Subject instance
			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			Answered = 1; // An answer was given
			if (check.size() > 1) {
				Currentitemofinterest = classtype.get(Integer.valueOf(check
						.get(1)));
				System.out.println("Classtype List = "
						+ classtype.getClass().getSimpleName());
				System.out.println("Index in Liste = "
						+ Integer.valueOf(check.get(1)));
				Currentindex = Integer.valueOf(check.get(1));
				theRecentThing.clear(); // Clear it before adding (changing) the
				// now recent thing
				theRecentThing.add(classtype.get(Currentindex));
			}
		}

		// Location Question in Pronomial form "Where can i find it"

		if (questiontype.equals("location")) {   // We always expect a pronomial question to refer to the last
											// object questioned for
			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Location(classtype, input));
			Answered = 1; // An answer was given
		}

		if ((questiontype.equals("intent") && classtype == thePhoneList)
				|| (questiontype.equals("intent") && classtype == theRecentThing))
		{
			// Can I lend the book or not (Can I lent "it" or not)
			answer=("You "+ BookAvailable(classtype, input));
			Answered = 1; // An answer was given
		}

		if (questiontype.equals("farewell")) {       // Reply to a farewell
			answer=("You are welcome.");
			Answered = 1; // An answer was given
		}
		
		if (Answered == 0) { // No answer was given
			answer=("Sorry I didn't understand that.");
		}

		out.add(input);
		out.add(answer);
		
		return out;
	}

	// Methods to generate answers for the different kinds of Questions
	
	// Answer a question of the "Is a book or "it (meaning a book) available ?" kind

	public String BookAvailable(List thelist, String input) {

		boolean available =true;
		String answer ="";
		Book curbook = new Book();
		String booktitle="";

		if (thelist == thePhoneList) {                      //This is a candidate for a name change

			int counter = 0;

			//Identify which book is asked for 

			for (int i = 0; i < thelist.size(); i++) {

				curbook = (Book) thelist.get(i);         //This is a candidate for a name change

				if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = thePhoneList;                                    //This is a candidate for a name change
					theRecentThing.add(classtype.get(Currentindex));
					booktitle=curbook.getTitle();
										
					if (input.contains(curbook.getTitle().toLowerCase())){input = input.replace(curbook.getTitle().toLowerCase(), "<b>"+curbook.getTitle().toLowerCase()+"</b>");}          
					if (input.contains(curbook.getIsbn().toLowerCase())) {input = input.replace(curbook.getIsbn().toLowerCase(), "<b>"+curbook.getIsbn().toLowerCase()+"</b>");}     
					if (input.contains(curbook.getAutor().toLowerCase())){input = input.replace(curbook.getAutor().toLowerCase(), "<b>"+curbook.getAutor().toLowerCase()+"</b>");}
										
					i = thelist.size() + 1; 									// force break
				}
			}
		}

		// maybe other way round or double 

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("book")) {                  //This is a candidate for a name change

				curbook = (Book) theRecentThing.get(0);               //This is a candidate for a name change		
				booktitle=curbook.getTitle();
			}
		}

		// check all lendings if they contain the books ISBN

		for (int i = 0; i < thePhoneSaleList.size(); i++) {

			Lending curlend = (Lending) thePhoneSaleList.get(i);         //This is a candidate for a name change

			// If there is a lending with the books ISBN, the book is not available

			if ( curbook.getIsbn().toLowerCase().equals(curlend.getIsbn().toLowerCase())) {           //This is a candidate for a name change

				input = input.replace(curlend.getIsbn().toLowerCase(), "<b>"+curlend.getIsbn().toLowerCase()+"</b>");
				
				available=false;
				i = thelist.size() + 1; 									// force break
			}
		}

		if(available){
			answer="can lend the book.";
		}
		else{ 
			answer="cannot lend the book as someone else has lent it at the moment.";
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ booktitle;
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return(answer);

	}

	// Answer a question of the "How many ...." kind 
	
	public Integer Count(List thelist) { // List "thelist": List of Class Instances (e.g. thePhoneList)

		//URL = "http://en.wiktionary.org/wiki/"		

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return thelist.size();
	}

	// Answer a question of the "What kind of..." kind
	
	private String ListAll(List thelist) {

		String listemall = "<ul>";

		if (thelist == thePhoneList) {                                  //This is a candidate for a name change
			for (Object aPhone : thelist) {
				Phone phone = (Phone) aPhone;                  //This is a candidate for a name change
				listemall += "<li>" + (phone.getName() + "</li>");    //This is a candidate for a name change
			}
		}

		if (thelist == thePhoneSaleList) {                               //This is a candidate for a name change
			for (Object aPhoneSale : thelist) {
				PhoneSale phoneSale = (PhoneSale) aPhoneSale;             //This is a candidate for a name change
				listemall += "<li>" + (phoneSale.getReceiptID() + "</li>");                //This is a candidate for a name change
			}
		}

		if (thelist == thePhoneLeaseList) {                               //This is a candidate for a name change
			for (Object aPhoneLease : thelist) {
				PhoneLease phoneLease = (PhoneLease) aPhoneLease;             //This is a candidate for a name change
				listemall += "<li>" + (phoneLease.getReceiptID() + "</li>");                //This is a candidate for a name change
			}
		}

		if (thelist == theCustomerList) {                                //This is a candidate for a name change
			for (Object aCustomer : thelist) {
				Customer customer = (Customer) aCustomer;               //This is a candidate for a name change
				listemall += "<li>" + (customer.getName()  + "</li>");  //This is a candidate for a name change
			}
		}

		if (thelist == theSalesmenList) {                               //This is a candidate for a name change
			for (Object aSalesman : thelist) {
				Salesman salesman = (Salesman) aSalesman;             //This is a candidate for a name change
				listemall += "<li>" + (salesman.getName() + "</li>");      //This is a candidate for a name change
			}
		}

		listemall += "</ul>";

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);
		
		return listemall;
	}

	// Answer a question of the "Do you have..." kind 
	
	private Vector<String> CheckFor(List theList, String input) {

		Vector<String> yesOrNo = new Vector<String>();
		if (classtype.isEmpty()){
			yesOrNo.add("Class not recognised. Please specify if you are searching for a Phone, Customer, Salesman, Phone Lease or Phone Sale?");
		} else {
			yesOrNo.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (theList == thePhoneList) {                         //This is a candidate for a name change
			for (Object aPhone : theList) {
				Phone curbook = (Phone) aPhone;                           //This is a candidate for a name change
				if (input.contains(curbook.getMake().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curbook.getModel().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curbook.getType().toLowerCase())) {  //This is a candidate for a name change
					counter = theList.indexOf(aPhone);
					yesOrNo.set(0, "Yes we have such a Phone");                  //This is a candidate for a name change
					yesOrNo.add(counter.toString());
					break;
				}
			}
		}

		if (theList == thePhoneSaleList) {                                     //This is a candidate for a name change
			for (Object aPhoneSale : theList ) {
				PhoneSale phoneSale = (PhoneSale) aPhoneSale;                  //This is a candidate for a name change
				if (input.contains(phoneSale.getReceiptID().toLowerCase())          //This is a candidate for a name change
						|| input.contains(phoneSale.getCustomerID().toLowerCase())){ //This is a candidate for a name change
					counter = theList.indexOf(aPhoneSale);
					yesOrNo.set(0, "Yes we have such a Sale");            //This is a candidate for a name change
					yesOrNo.add(counter.toString());
					break;
				}
			}
		}

		if (theList == thePhoneLeaseList) {                                     //This is a candidate for a name change
			for (Object aPhoneLease : theList ) {
				PhoneLease phoneLease = (PhoneLease) aPhoneLease;                  //This is a candidate for a name change
				if (input.contains(phoneLease.getReceiptID().toLowerCase())          //This is a candidate for a name change
						|| input.contains(phoneLease.getCustomerID().toLowerCase())){ //This is a candidate for a name change
					counter = theList.indexOf(aPhoneLease);
					yesOrNo.set(0, "Yes we have such a Lease");            //This is a candidate for a name change
					yesOrNo.add(counter.toString());
					break;
				}
			}
		}

		if (theList == theCustomerList) {                                      //This is a candidate for a name change
			for (Object aCustomer : theList){
				Customer customer = (Customer) aCustomer;                      //This is a candidate for a name change
				if (input.contains(customer.getName().toLowerCase())         //This is a candidate for a name change
						|| input.contains(customer.getCustomerID().toLowerCase()) //This is a candidate for a name change
						|| input.contains(customer.getMobileNumber().toLowerCase())) {  //This is a candidate for a name change
					counter = theList.indexOf(aCustomer);
					yesOrNo.set(0, "Yes we have such a Customer");               //This is a candidate for a name change
					yesOrNo.add(counter.toString());
					break;
				}
			}
		}

		if (theList == theSalesmenList) {                                    //This is a candidate for a name change
			for (Object aSalesMan : theList){
				Salesman salesman = (Salesman) aSalesMan;                  //This is a candidate for a name change
				if (input.contains(salesman.getName().toLowerCase())          //This is a candidate for a name change
						|| input.contains(salesman.getSalesManID().toLowerCase())) { //This is a candidate for a name change
					counter = theList.indexOf(aSalesMan);
					yesOrNo.set(0, "Yes we have such a Salesman");           //This is a candidate for a name change
					yesOrNo.add(counter.toString());
					break;
				}
			}
		}

		if (classtype.isEmpty()) {
			System.out.println("Not class type given.");
		} else {
			URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			System.out.println("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL2);
		}
		return yesOrNo;
	}

	//  Method to retrieve the location information from the object (Where is...) kind

	public String Location(List classtypelist, String input) {

		List thelist = classtypelist;
		String location = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("book")) {                  //This is a candidate for a name change

				Book curbook = (Book) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curbook.getLocation() + " ");             //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("member")) {               //This is a candidate for a name change

				Member curmem = (Member) theRecentThing.get(0);      //This is a candidate for a name change
				location = (curmem.getCity() + " " + curmem.getStreet() + " " + curmem  //This is a candidate for a name change
						.getHousenumber());                                    //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()  
					.toLowerCase().equals("catalog")) {                 //This is a candidate for a name change

				Catalog curcat = (Catalog) theRecentThing.get(0);       //This is a candidate for a name change
				location = (curcat.getLocation() + " ");                //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("library")) {                  //This is a candidate for a name change

				location = (phoneShop.getCity() + " " + phoneShop.getStreet() + phoneShop   //This is a candidate for a name change
						.getHousenumber());                                           //This is a candidate for a name change
			}

		}

		// if a direct noun was used (book, member, etc)

		else {

			if (thelist == thePhoneList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Book curbook = (Book) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curbook.getTitle().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curbook.getIsbn().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curbook.getAutor().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curbook.getLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = thePhoneList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

			if (thelist == theCustomerList) {                                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Member curmember = (Member) thelist.get(i);         				  //This is a candidate for a name change

					if (input.contains(curmember.getSurname().toLowerCase())              //This is a candidate for a name change
							|| input.contains(curmember.getLastname().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curmember.getMemberid().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						location = (curmember.getCity() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 										// Clear it before adding (changing) the
						classtype = theCustomerList;            	 						//This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 				             	        // force break
					}
				}
			}

			if (thelist == theSalesmenList) {                                       	 //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Catalog curcatalog = (Catalog) thelist.get(i);                    //This is a candidate for a name change

					if (input.contains(curcatalog.getName().toLowerCase())            //This is a candidate for a name change						     
							|| input.contains(curcatalog.getUrl().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						location = (curcatalog.getLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear();                                      // Clear it before adding (changing) the	
						classtype = theSalesmenList;                                  //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1;                                      // force break
					}
				}
			}

			if (thelist == thePhoneShopList) {                                                  //This is a candidate for a name change

				location = (phoneShop.getCity() + " " + phoneShop.getStreet() + phoneShop  //This is a candidate for a name change
						.getHousenumber());                                                   //This is a candidate for a name change
			}
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return location;
	}

	public String testit() {   // test the loaded knowledge by querying for books written by dostoyjewski

		String answer = "";

		System.out.println("Book List = " + thePhoneList.size());  //This is a candidate for a name change

		for (int i = 0; i < thePhoneList.size(); i++) {   // check each book in the List, //This is a candidate for a name change

			Book curbook = (Book) thePhoneList.get(i);    // cast list element to Book Class //This is a candidate for a name change
			System.out.println("Testing Book" + curbook.getAutor());

			if (curbook.getAutor().equalsIgnoreCase("dostoyjewski")) {     // check for the author //This is a candidate for a name change

				answer = "A book written by " + curbook.getAutor() + "\n"  //This is a candidate for a name change
						+ " is for example the classic " + curbook.getTitle()      //This is a candidate for a name change
						+ ".";
			}
		}
		return answer;
	}

	public String readwebsite(String url) {

		String webtext = "";
		try {
			BufferedReader readit = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			String lineread = readit.readLine();

			System.out.println("Reader okay");

			while (lineread != null) {
				webtext = webtext + lineread;
				lineread = readit.readLine();				
			}

			// Hard coded cut out from "wordnet website source text": 
			//Check if website still has this structure   vvvv ...definitions...  vvvv 		
			
			webtext = webtext.substring(webtext.indexOf("<ul>"),webtext.indexOf("</ul>"));                                 //               ^^^^^^^^^^^^^^^^^              

			webtext = "<table width=\"700\"><tr><td>" + webtext
					+ "</ul></td></tr></table>";

		} catch (Exception e) {
			webtext = "Not yet";
			System.out.println("Error connecting to wordnet");
		}
		return webtext;
	}
}