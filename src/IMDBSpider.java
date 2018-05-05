import javax.json.*;
import org.htmlcleaner.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.net.*;
import java.io.*;


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

        	//for(int i=0; i<movies.size(); i++){
        	for(int i=0; i<1; i++){
        		JsonObject jobj = movies.getJsonObject(i);       		
        		String result = URLEncoder.encode(jobj.getString("movie_name"), "UTF-8");
        		System.out.println(result);
        		URL moviesearch = new URL("https://www.imdb.com/find?q="+ result +"&s=tt&ttype=f");
        		
        		/**
        		BufferedReader in = new BufferedReader(
        		        new InputStreamReader(moviesearch.openStream()));
        		String inputLine;
                while ((inputLine = in.readLine()) != null)
                    System.out.println(inputLine);
                in.close();
                */
        		HtmlCleaner cleaner = new HtmlCleaner();
        		CleanerProperties props = cleaner.getProperties();
        		TagNode tagNode = new HtmlCleaner(props).clean(moviesearch);
        		new PrettyXmlSerializer(props).writeToFile(
        			    tagNode, "htmlmovie.xml", "utf-8"
        			);
        		
        	}
    	}
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
