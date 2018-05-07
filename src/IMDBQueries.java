import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.*;


public class IMDBQueries {

    /**
     * A helper class for pairs of objects of generic types 'K' and 'V'.
     *
     * @param <K> first value
     * @param <V> second value
     */
    public static class Tuple<K, V> {
        K first;
        V second;

        public Tuple(K f, V s) {
            first = f;
            second = s;
        }

        @Override
        public int hashCode() {
            return first.hashCode() + second.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Tuple<?, ?>
                && first.equals(((Tuple<?, ?>) obj).first)
                && second.equals(((Tuple<?, ?>) obj).second);
        }
    }
    
    private static double convertToDouble(String temp){
    	String Value = temp;
    	Value = Value.replaceAll(",", "").trim();
    	Value = Value.replaceAll(" ", "");
    	
    	return Double.parseDouble(Value);
    }
    
    private static long convertToLong(String temp){
    	String Value = temp;
    	Value = Value.replaceAll("[^\\d.]", "");
    	
    	return Long.parseLong(Value);
    }

    /**
     * All-rounder: Determine all movies in which the director stars as an actor
     * (cast). Return the top ten matches sorted by decreasing IMDB rating.
     *
     * @param movies the list of movies which is to be queried
     * @return top ten movies and the director, sorted by decreasing IMDB rating
     */
    protected List<Tuple<Movie, String>> queryAllRounder(List<Movie> movies) {
    	Movie movie;
    	List<Tuple<Movie,String>> topten = new ArrayList<Tuple<Movie,String>>();
    	for(int i = 0; i<movies.size(); i++){
    		movie = movies.get(i);
    		outerloop:
    		for(int j = 0; j < movie.getCastList().size(); j++){
    			for(int k =0; k < movie.getDirectorList().size(); k++){
    				if((movie.getDirectorList().get(k).equals(movie.getCastList().get(j)))){
    					Tuple<Movie,String> tempmovie = new Tuple<Movie,String>(movie,""); 	
    					if(topten.isEmpty()){							
    						topten.add(tempmovie);
    					}else{
    						for(int l = 0; l < topten.size(); l++){
    							if(convertToDouble(tempmovie.first.getRatingValue()) > convertToDouble(topten.get(l).first.getRatingValue())){
    								topten.add(l, tempmovie);    	
    								if(l+1 > topten.size()){ topten.add(tempmovie);}
    								if(topten.size() >= 10){
    									topten.subList(10, topten.size()).clear();;
    								}
    								break;
    							}
    						}
    					}
    					break outerloop;
    				}
    			}
    		}
    	}
        return topten;
    }

    /**
     * Under the Radar: Determine the top ten US-American movies until (including)
     * 2015 that have made the biggest loss despite an IMDB score above
     * (excluding) 8.0, based on at least 1,000 votes. Here, loss is defined as
     * budget minus gross.
     *
     * @param movies the list of movies which is to be queried
     * @return top ten highest rated US-American movie until 2015, sorted by
     * monetary loss, which is also returned
     */
    protected List<Tuple<Movie, Long>> queryUnderTheRadar(List<Movie> movies) {
        // TODO Basic Query: insert code here
    	Movie movie;
    	List<Tuple<Movie,Long>> topten = new ArrayList<Tuple<Movie,Long>>();
    	for(int i = 0; i<movies.size(); i++){
    		movie = movies.get(i);
    		if(convertToDouble(movie.getRatingValue()) >= (double)8.0){
    			if(convertToDouble(movie.getRatingCount()) >= (double)1000){
    				if(!movie.getGross().equals("0") || !movie.getBudget().equals("0")){
    					long loss = (convertToLong(movie.getGross())- convertToLong(movie.getBudget()));
    					Tuple<Movie,Long> tempmovie = new Tuple<Movie,Long>(movie,loss); 	
    					if(topten.isEmpty()){							
						topten.add(tempmovie);
    					}else{
    						for(int l = 0; l < topten.size(); l++){
    							if(tempmovie.second < topten.get(l).second){
    								topten.add(l, tempmovie);    	
    								if(l+1 > topten.size()){ topten.add(tempmovie);}
    								if(topten.size() >= 10){
    									topten.subList(10, topten.size()).clear();;
    								}
    								break;
    							}
    						}
						}
					}
    			}
    		}
    	}
        return topten;
    }

    /**
     * The Pillars of Storytelling: Determine all movies that contain both
     * (sub-)strings "kill" and "love" in their lowercase description
     * (String.toLowerCase()). Sort the results by the number of appearances of
     * these strings and return the top ten matches.
     *
     * @param movies the list of movies which is to be queried
     * @return top ten movies, which have the words "kill" and "love" as part of
     * their lowercase description, sorted by the number of appearances of
     * these words, which is also returned.
     */
    protected List<Tuple<Movie, Integer>> queryPillarsOfStorytelling(
        List<Movie> movies) {
        // TODO Basic Query: insert code here
        return new ArrayList<>();
    }

    /**
     * The Red Planet: Determine all movies of the Sci-Fi genre that mention
     * "Mars" in their description (case-aware!). List all found movies in
     * ascending order of publication (year).
     *
     * @param movies the list of movies which is to be queried
     * @return list of Sci-Fi movies involving Mars in ascending order of
     * publication.
     */
    protected List<Movie> queryRedPlanet(List<Movie> movies) {
        // TODO Basic Query: insert code here
    	Movie movie;
    	List<Movie> movielist = new ArrayList<Movie>();
    	for(int i = 0; i<movies.size(); i++){
    		movie = movies.get(i);
    		for(int j=0; j < movie.getGenreList().size(); j++){
    			if(movie.getGenreList().get(j).contains("Sci-Fi")){
    				if(movie.getDescription().contains("Mars")){
    					if(movielist.isEmpty()){
    						movielist.add(movie);
    					}else{
    						for(int k=0; k<movielist.size();k++){
    							if(convertToLong(movie.getYear())<convertToLong(movielist.get(k).getYear())){
    								movielist.add(k, movie);
    								break;
    							}
    						}
    					}
    				}
    			}
    		}
    	}
        return movielist;
    }

    /**
     * Colossal Failure: Determine all US-American movies with a duration beyond 2
     * hours, a budget beyond 1 million and an IMDB rating below 5.0. Sort results
     * by ascending IMDB rating.
     *
     * @param movies the list of movies which is to be queried
     * @return list of US-American movies with high duration, large budgets and a
     * bad IMDB rating, sorted by ascending IMDB rating
     */
    protected List<Movie> queryColossalFailure(List<Movie> movies) {
        // TODO Basic Query: insert code here
        return new ArrayList<>();
    }

    /**
     * Uncreative Writers: Determine the 10 most frequent character names of all
     * times ordered by frequency of occurrence. Filter any lowercase names
     * containing substrings "himself", "doctor", and "herself" from the result.
     *
     * @param movies the list of movies which is to be queried
     * @return the top 10 character names and their frequency of occurrence;
     * sorted in decreasing order of frequency
     */
    protected List<Tuple<String, Integer>> queryUncreativeWriters(List<Movie> movies) {
        // TODO Impossibly Hard Query: insert code here
        return new ArrayList<>();
    }

    /**
     * Workhorse: Provide a ranked list of the top ten most active actors (i.e.
     * starred in most movies) and the number of movies they played a role in.
     *
     * @param movies the list of movies which is to be queried
     * @return the top ten actors and the number of movies they had a role in,
     * sorted by the latter.
     */
    protected List<Tuple<String, Integer>> queryWorkHorse(List<Movie> movies) {
        // TODO Impossibly Hard Query: insert code here
    	Movie movie;
    	List<Tuple<String,Integer>> topten = new ArrayList<Tuple<String,Integer>>();
    	boolean inserted = false;
    	for(int i = 0; i<movies.size(); i++){
    		movie = movies.get(i);
    		for(int j=0; j<movie.getCastList().size(); j++){
    			for(int k=0; k<topten.size();k++){
    				if(movie.getCastList().get(j).equals(topten.get(k).first)){
    					topten.get(k).second++;
    					int copy_k = k-1;
    					while(copy_k > 0 && topten.get(k).second > topten.get(copy_k).second)	{copy_k--;}
    					if(copy_k == 0){
    						topten.add(0, topten.get(k));
    						topten.remove(k+1);
    					}else{
    						Collections.swap(topten, copy_k+1, k);
    					}
    					inserted = true;
    				}
    			}
    			if(inserted == false){
    				Tuple<String,Integer> tempactor = new Tuple<String,Integer>(movie.getCastList().get(j),1);
    				topten.add(tempactor);
    			}
    		}
    	}
    	topten.subList(10, topten.size()).clear();
        return topten;
    }

    /**
     * Must See: List the best-rated movie of each year starting from 1990 until
     * (including) 2010 with more than 10,000 ratings. Order the movies by
     * ascending year.
     *
     * @param movies the list of movies which is to be queried
     * @return best movies by year, starting from 1990 until 2010.
     */
    protected List<Movie> queryMustSee(List<Movie> movies) {
        // TODO Impossibly Hard Query: insert code here
    	Movie movie;
    	Movie[] mustsee = new Movie[21];
    	for(int i = 0; i<movies.size(); i++){
    		movie = movies.get(i);
    		int index = (int)convertToLong(movie.getYear()) - 1990;
    		if(index >= 0 && index < 21){
    			if((int)convertToLong(movie.getRatingCount())> 10000){
    				if(mustsee[index] == null){
    					mustsee[index] = movie;
    				}else{
    					if(convertToDouble(movie.getRatingValue()) > convertToDouble(mustsee[index].getRatingValue())){
    						mustsee[index] = movie;
    					}
    				}
    			}
    		}
    	}
    	
        return Arrays.asList(mustsee);
    }

    /**
     * Rotten Tomatoes: List the worst-rated movie of each year starting from 1990
     * till (including) 2010 with an IMDB score larger than 0. Order the movies by
     * increasing year.
     *
     * @param movies the list of movies which is to be queried
     * @return worst movies by year, starting from 1990 till (including) 2010.
     */
    protected List<Movie> queryRottenTomatoes(List<Movie> movies) {
        // TODO Impossibly Hard Query: insert code here
    	Movie movie;
    	Movie[] rotton = new Movie[21];
    	for(int i = 0; i<movies.size(); i++){
    		movie = movies.get(i);    		
    		int index = (int)convertToLong(movie.getYear()) - 1990;
    		if(index >= 0 && index < 21){
    			if(convertToDouble(movie.getRatingValue())> 0){
    				if(rotton[index] == null){
    					rotton[index] = movie;
    				}else{
    					if(convertToDouble(movie.getRatingValue()) < convertToDouble(rotton[index].getRatingValue())){
    						rotton[index] = movie;
    					}
    				}
    			}
    		}
    	}
    	
        return Arrays.asList(rotton);
    }

    /**
     * Magic Couples: Determine those couples that feature together in the most
     * movies. E.g., Adam Sandler and Allen Covert feature together in multiple
     * movies. Report the top ten pairs of actors, their number of movies and sort
     * the result by the number of movies.
     *
     * @param movies the list of movies which is to be queried
     * @return report the top 10 pairs of actors and the number of movies they
     * feature together. Sort by number of movies.
     */
    protected List<Tuple<Tuple<String, String>, Integer>> queryMagicCouple(List<Movie> movies) {
        // TODO Impossibly Hard Query: insert code here
        return new ArrayList<>();
    }


    public static void main(String[] argv) throws IOException {
        String moviesPath = "./data/movies/";
        
        //String temptest = "True Lies - Wahre L&uuml;gen";

        if (argv.length == 1) {
            moviesPath = argv[0];
        } else if (argv.length != 0) {
            System.out.println("Call with: IMDBQueries.jar <moviesPath>");
            System.exit(0);
        }
        MovieReader movieReader = new MovieReader();
        List<Movie> movies = movieReader.readMoviesFrom(Paths.get(moviesPath));
        //System.out.println(movies.get().getTitle());
 

        System.out.println("All-rounder");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Tuple<Movie, String>> result = queries.queryAllRounder(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty() && result.size() == 10) {
                for (Tuple<Movie, String> tuple : result) {
                    System.out.println("\t" + tuple.first.getRatingValue() + "\t"
                        + tuple.first.getTitle() + "\t" + tuple.second);
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("Under the radar");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Tuple<Movie, Long>> result = queries.queryUnderTheRadar(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty() && result.size() <= 10) {
                for (Tuple<Movie, Long> tuple : result) {
                    System.out.println("\t" + tuple.first.getTitle() + "\t"
                        + tuple.first.getRatingCount() + "\t"
                        + tuple.first.getRatingValue() + "\t" + tuple.second);
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("The pillars of storytelling");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Tuple<Movie, Integer>> result = queries
                .queryPillarsOfStorytelling(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty() && result.size() <= 10) {
                for (Tuple<Movie, Integer> tuple : result) {
                    System.out.println("\t" + tuple.first.getTitle() + "\t"
                        + tuple.second);
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("The red planet");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Movie> result = queries.queryRedPlanet(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty()) {
                for (Movie movie : result) {
                    System.out.println("\t" + movie.getTitle());
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("ColossalFailure");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Movie> result = queries.queryColossalFailure(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty()) {
                for (Movie movie : result) {
                    System.out.println("\t" + movie.getTitle() + "\t"
                        + movie.getRatingValue());
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("Uncreative writers");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Tuple<String, Integer>> result = queries
                .queryUncreativeWriters(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty() && result.size() <= 10) {
                for (Tuple<String, Integer> tuple : result) {
                    System.out.println("\t" + tuple.first + "\t" + tuple.second);
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("Workhorse");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Tuple<String, Integer>> result = queries.queryWorkHorse(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty() && result.size() <= 10) {
                for (Tuple<String, Integer> actor : result) {
                    System.out.println("\t" + actor.first + "\t" + actor.second);
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("Must see");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Movie> result = queries.queryMustSee(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty()) {
                for (Movie m : result) {
                    System.out.println("\t" + m.getYear() + "\t" + m.getRatingValue()
                        + "\t" + m.getTitle());
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("Rotten tomatoes");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Movie> result = queries.queryRottenTomatoes(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty()) {
                for (Movie m : result) {
                    System.out.println("\t" + m.getYear() + "\t" + m.getRatingValue()
                        + "\t" + m.getTitle());
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
        }
        System.out.println("");

        System.out.println("Magic Couples");
        {
            IMDBQueries queries = new IMDBQueries();
            long time = System.currentTimeMillis();
            List<Tuple<Tuple<String, String>, Integer>> result = queries
                .queryMagicCouple(movies);
            System.out.println("Time:" + (System.currentTimeMillis() - time));

            if (result != null && !result.isEmpty() && result.size() <= 10) {
                for (Tuple<Tuple<String, String>, Integer> tuple : result) {
                    System.out.println("\t" + tuple.first.first + ":"
                        + tuple.first.second + "\t" + tuple.second);
                }
            } else {
                System.out.println("Error? Or not implemented?");
            }
            System.out.println("");
        }
 
    }
}
