import javax.json.*;
import org.htmlcleaner.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URLEncoder;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class IMDBSpider {

    public IMDBSpider() {
    }

    /**
     * For each title in file movieListJSON:
     *
     * <pre>
     * You should:
     * - First, read a list of 500 movie titles from the JSON file in 'movieListJSON'.
     *
     * - Secondly, for each movie title, perform a web search on IMDB and retrieve
     * movie’s URL: https://www.imdb.com/find?q=<MOVIE>&s=tt&ttype=ft
     *
     * - Thirdly, for each movie, extract metadata (actors, budget, description)
     * from movie’s URL and store to a JSON file in directory 'outputDir':
     *    https://www.imdb.com/title/tt0499549/?ref_=fn_al_tt_1 for Avatar - store
     * </pre>
     *
     * @param movieListJSON JSON file containing movie titles
     * @param outputDir     output directory for JSON files with metadata of movies.
     * @throws IOException
     */
    public void fetchIMDBMovies(String movieListJSON, String outputDir) throws IOException {
        // TODO add code here
    	try (Reader fileReader = Files.newBufferedReader(Paths.get(movieListJSON), StandardCharsets.UTF_8);          	
                JsonReader jsonReader = Json.createReader(fileReader)) {
    		JsonArray movies = jsonReader.readArray();        
    		//System.out.println(movies.get(1));
    		//.get(1) um element an stelle 1 bekommen
    		//System.out.println(movies.size());
			JsonArrayBuilder builder = Json.createArrayBuilder(); 
        	  		
    		for(int i=0; i<movies.size(); i++){
        	//for(int i=0; i<1; i++){
        		JsonObject jobj = movies.getJsonObject(i);       		
        		String result = URLEncoder.encode(jobj.getString("movie_name"), "UTF-8");
        		System.out.println(result);
        		URL moviesearch = new URL("https://www.imdb.com/find?q="+ result +"&s=tt&ttype=ft");
        		
        		HtmlCleaner cleaner = new HtmlCleaner();
        		CleanerProperties props = cleaner.getProperties();
        		TagNode tagNode = new HtmlCleaner(props).clean(moviesearch);
        		
        		DomSerializer domSerializer = new DomSerializer(new CleanerProperties()); 
        		Document doc = null;
        		try{
        			doc = domSerializer.createDOM(tagNode);
        		}catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                
                    // Create XPathFactory object
                    XPathFactory xpathFactory = XPathFactory.newInstance();

                    // Create XPath object
                    XPath xpath = xpathFactory.newXPath();

                    String name = getXMLString(doc, xpath,"//table[@class='findList']//td[@class='result_text']/a[1]/@href");
                    System.out.println(name);
                   
                builder = newentry(name, builder);    
                System.out.println("ENTRY NUMBER:" + i);
                JsonArray finallist =builder.build();
            	try{
            		FileWriter fw = new FileWriter("outputDir/"+"("+i+") "+result+".json");
                    JsonWriter jsonWriter = Json.createWriter(fw);
                    jsonWriter.writeArray(finallist);
                    jsonWriter.close();
                    fw.close(); 
            	}catch (IOException e) {
                    e.printStackTrace();
                }
        	}        	 
    	}
    }
    
    private static JsonArrayBuilder newentry(String urlmovie, JsonArrayBuilder builder)throws IOException{
    	//String result = URLEncoder.encode(urlmovie, "UTF-8");
    	URL moviesearch = new URL("https://www.imdb.com/"+urlmovie);
    	
    	HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		TagNode tagNode = new HtmlCleaner(props).clean(moviesearch);
		
		DomSerializer domSerializer = new DomSerializer(new CleanerProperties()); 
		Document doc = null;
		try{
			doc = domSerializer.createDOM(tagNode);
		}catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
		
		new PrettyXmlSerializer(props).writeToFile(
			    tagNode, "moviesite.xml", "utf-8"
			);
		
		// Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xpathentry = xpathFactory.newXPath();

	
		//URL
		System.out.println(moviesearch);
		
		//Title
		String title = null;
		title = getXMLString(doc,xpathentry,"//div[@class='titleBar']//h1[@itemprop='name']/text()");
		title = title.substring(0, title.length() - 1);
		System.out.println(title);
		
		//Year
		String year = null;
		year = getXMLString(doc,xpathentry,"//div[@class='titleBar']//h1[@itemprop='name']/span[@id='titleYear']/a/text()");
		System.out.println(year);
		
		//Genre
		List<String> genre = new ArrayList<>();
		genre = getXMLList (doc, xpathentry, "//div[@id='titleStoryLine']//div[@itemprop='genre']/a/text()");
		System.out.println(Arrays.toString(genre.toArray()));
		
		//countryList
		List<String> countryList = new ArrayList<>();
		countryList = getXMLList (doc, xpathentry, "//div[@id='titleDetails']/div[@class='txt-block']/h4[contains(text(), 'Country')]/following-sibling::a/text()");
		System.out.println(Arrays.toString(countryList.toArray()));
		
		//description
		String description = null;
		description = getXMLString(doc,xpathentry,"//div[@id ='titleStoryLine']//span[@itemprop='description']/text()");
		System.out.println(description);
		
		//budget (dirty)
		//div[@class='txt-block']/h4[contains(text(), 'Budget')]/following-sibling::text()
		String budget = null;
		budget = getXMLString(doc,xpathentry,"//div[@class='txt-block']/h4[contains(text(), 'Budget')]/following-sibling::text()");
		System.out.println(budget);
		
		//gross
		String gross = null;
		gross  = getXMLString(doc,xpathentry,"//div[@class='txt-block']/h4[contains(text(), 'Cumulative Worldwide Gross')]/following-sibling::text()");
		if(gross !=null && gross.isEmpty()){
			getXMLString(doc,xpathentry,"//div[@class='txt-block']/h4[contains(text(), 'Gross USA')]/following-sibling::text()");
		}
		System.out.println(gross);
		
		//ratingValue
		String ratingValue = null;
		ratingValue = getXMLString(doc,xpathentry,"//div[@class='ratings_wrapper']//span[@itemprop='ratingValue']/text()");
		System.out.println(ratingValue);
		
		//ratingCount
		String ratingCount = null;
		ratingCount = getXMLString(doc,xpathentry,"//div[@class='ratings_wrapper']//span[@itemprop='ratingCount']/text()");
		System.out.println(ratingCount);
		
		//duration
		//div[@class='titleBar']//time[@itemprop='duration']/text()
		String duration = null;
		duration = getXMLString(doc,xpathentry,"//div[@class='titleBar']//time[@itemprop='duration']/text()");
		System.out.println(duration);
		
		//castList
		List<String> castList = new ArrayList<>();
		castList = getXMLList (doc, xpathentry, "//table[@class='cast_list']//td[@itemprop='actor']//span[@itemprop='name']/text()");
		System.out.println(Arrays.toString(castList.toArray()));
		
		//characterList
		List<String> characterList = new ArrayList<>();
		characterList = getXMLList (doc, xpathentry, "//table[@class='cast_list']//td[@class='character']/a/text()");
		System.out.println(Arrays.toString(characterList.toArray()));
		
		//directorList
		List<String> directorList = new ArrayList<>();
		directorList = getXMLList (doc, xpathentry, "//div[@class ='plot_summary_wrapper']//span[@itemprop = 'director']//span[@itemprop='name']/text()");
		System.out.println(Arrays.toString(directorList.toArray()));
		
		JsonObjectBuilder entry = Json.createObjectBuilder();
		entry.add("url", moviesearch.toString());
		entry.add("title", title);
		entry.add("year", year);
		
		JsonArrayBuilder genreList = Json.createArrayBuilder();
		for (int i = 0; i < genre.size();i++){
			genreList.add(genre.get(i));
		}
		entry.add("genreList", genreList);
		
		JsonArrayBuilder countryL = Json.createArrayBuilder();
		for (int i = 0; i < countryList.size();i++){
			countryL.add(countryList.get(i));
		}
		entry.add("countryList", countryL);
		entry.add("description", description);
		entry.add("budget", budget);
		entry.add("gross",gross);
		entry.add("ratingValue", ratingValue);
		entry.add("ratingCount", ratingCount);
		entry.add("duration", duration);
		
		JsonArrayBuilder castL = Json.createArrayBuilder();
		for (int i = 0; i < castList.size();i++){
			castL.add(castList.get(i));
		}
		entry.add("castList", castL);
		
		JsonArrayBuilder charaL = Json.createArrayBuilder();
		for (int i = 0; i < characterList.size();i++){
			charaL.add(characterList.get(i));
		}
		entry.add("characterList", charaL);
		
		JsonArrayBuilder direcL = Json.createArrayBuilder();
		for (int i = 0; i < directorList.size();i++){
			direcL.add(directorList.get(i));
		}
		entry.add("directorList", direcL);
		
		builder.add(entry);
		
		return builder;
		
    }
    

    private static List<String> getXMLList (Document doc, XPath xpath, String input){
    	List<String> list = new ArrayList<>();
    	try {
            //create XPathExpression object
            XPathExpression expr = 
            		xpath.compile(input);
            //evaluate expression result on XML document
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++)
                list.add(cleanText(nodes.item(i).getNodeValue()));
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    	return list;
    }
    
    private static String getXMLString(Document doc, XPath xpath,String input){
    	String name = null;
    	try {
            XPathExpression expr =
                xpath.compile(input);
            name = (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
		name = cleanText(name);
    	return name;
    }
    
    /**
     * Helper method to remove html and formatting from text.
     *
     * @param text The text to be cleaned
     * @return clean text
     */
    protected static String cleanText(String text) {
        return text.replaceAll("\\<.*?>", "").replace("&nbsp;", " ")
            .replace("\n", " ").replaceAll("\\s+", " ").trim();
    }

    public static void main(String[] argv) throws IOException {
        String moviesPath = "./data/movies.json";
        String outputDir = "./data";

        if (argv.length == 2) {
            moviesPath = argv[0];
            outputDir = argv[1];

        } else if (argv.length != 0) {
            System.out.println("Call with: IMDBSpider.jar <moviesPath> <outputDir>");
            System.exit(0);
        }

        IMDBSpider sp = new IMDBSpider();
        sp.fetchIMDBMovies(moviesPath, outputDir);
    }
}
