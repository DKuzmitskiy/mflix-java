package mflix.api.daos;

import com.mongodb.client.MongoClient;
import mflix.config.MongoDBConfiguration;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {MongoDBConfiguration.class})
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FacetedSearchTest extends TicketTest {

  private MovieDao dao;
  @Autowired MongoClient mongoClient;

  @Value("${spring.mongodb.database}")
  String databaseName;

  @Before
  public void setup() {
    this.dao = new MovieDao(mongoClient, databaseName);
  }

  @Test
  public void testRatingRuntimeBuckets() {

    String cast = "Salma Hayek";

    List<Document> moviesInfo = dao.getMoviesCastFaceted(20, 0, cast);

    ArrayList<Document> allMovies = (ArrayList<Document>) moviesInfo.get(0).get("movies");
    assertEquals(
        "Check your movies sub-pipeline on getMoviesFaceted() for multiple cast in single cast",
        20,
        allMovies.size());

    ArrayList rating = (ArrayList<Document>) moviesInfo.get(0).get("rating");
    assertEquals(
        "Check your $bucket rating sub-pipeline on getMoviesFaceted() for multiple cast in single cast",
        3,
        rating.size());

    ArrayList runtime = (ArrayList<Document>) moviesInfo.get(0).get("runtime");
    assertEquals(
        "Check your $bucket runtime sub-pipeline on getMoviesFaceted() for multiple cast in single cast",
        3,
        runtime.size());
  }

  @Test
  public void testFacetedSearchPaging() {

    String cast = "Tom Hanks";

    List<Document> moviesInfo = dao.getMoviesCastFaceted(20, 2 * 13, cast);

    assertEquals("Should return a list of one faceted document", 1, moviesInfo.size());

    ArrayList movies = (ArrayList<Document>) moviesInfo.get(0).get("movies");
    assertEquals(
        "Check your movies sub-pipeline on getMoviesFaceted() for multiple cast in paged results",
        11,
        movies.size());

    ArrayList rating = (ArrayList<Document>) moviesInfo.get(0).get("rating");
    assertEquals(
        "Check your $bucket rating sub-pipeline on getMoviesFaceted() in multiple cast in paged results",
        3,
        rating.size());

    ArrayList runtime = (ArrayList<Document>) moviesInfo.get(0).get("runtime");
    assertEquals(
        "Check your $bucket runtime sub-pipeline on getMoviesFaceted() in paged results",
        4,
        runtime.size());
  }

  @Test
  public void testFacetedMultipleCast() {

    String[] cast = {"Brad Pitt", "Meryl Streep"};

    List<Document> moviesInfo = dao.getMoviesCastFaceted(20, 62, cast);

    assertEquals("Should return a list of one faceted document", 1, moviesInfo.size());

    ArrayList movies = (ArrayList<Document>) moviesInfo.get(0).get("movies");
    assertEquals(
        "Check your movies sub-pipeline on getMoviesFaceted() in multiple cast for multiple cast",
        9,
        movies.size());

    ArrayList rating = (ArrayList<Document>) moviesInfo.get(0).get("rating");
    assertEquals(
        "Check your $bucket rating sub-pipeline on getMoviesFaceted() in multiple cast",
        3,
        rating.size());

    ArrayList runtime = (ArrayList<Document>) moviesInfo.get(0).get("runtime");
    assertEquals(
        "Check your $bucket runtime sub-pipeline on getMoviesFaceted() in multiple cast",
        4,
        runtime.size());
  }
}
