package com.apitest;
import com.apitest.constant.Config;
import com.apitest.models.SearchResponse;
import com.apitest.models.SearchResponseDetail;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppTest {


    Config config;

    @Test
    public void init() {
        config = new Config();
        RestAssured.baseURI = config.getBaseURL();

        SearchResponse searchResponseHarryPotter = getBySearch(config,"Harry Potter");
        getBySearchDetail(config, getMovieID(searchResponseHarryPotter, "Harry Potter and the Sorcerer's Stone")); //

    }

    //returns all movies containing "Harry Potter"
    public SearchResponse getBySearch(Config config,String searchKey) {
        Map<String, String> searchParams = new HashMap<String, String>();
        searchParams.put("apikey", config.getApiKey());
        searchParams.put("s", searchKey);

        SearchResponse searchResponse = RestAssured.given().params(searchParams)
                .when()
                .get()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK).log().all()
                .extract()
                .as(SearchResponse.class);
        return searchResponse;
    }

    //this function returns imdbId of "Harry Potter and the Sorcerer's Stone"
    public String getMovieID(SearchResponse searchResponse, String movieName) {
        List<SearchResponse> list = new ArrayList<>();
        if (searchResponse != null) {
            for (int i = 0; i < searchResponse.Search.size(); i++) {
                if (searchResponse.Search.get(i).Title.equals(movieName)) {
                    return searchResponse.Search.get(i).imdbID;
                }
            }
        }
        return "";
    }


    //this function returns the movie "Harry Potter and the Philosopher's Stone", according to imdbID
    public void getBySearchDetail(Config config, String imdbID) {
        Map<String, String> searchParams = new HashMap<String, String>();
        searchParams.put("apikey", config.getApiKey());
        searchParams.put("i", imdbID);

        SearchResponseDetail searchResponseDetail = RestAssured.given().params(searchParams)
                .when()
                .get()
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK).log().all()
                .body("Title", Is.is("Harry Potter and the Sorcerer's Stone"))
                .body("Year", Is.is("2001"))
                .body("Released", Is.is("16 Nov 2001"))
                .extract()
                .as(SearchResponseDetail.class);

    }


}
