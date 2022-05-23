package com.thundashop.core.youtubemanager;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
//import com.google.api.services.youtube.model.SearchListResponsfe;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.gson.Gson;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.jomres.JomresManager;
import com.thundashop.core.jomres.dto.FetchBookingResponse;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import com.thundashop.core.youtubemanager.data.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class YouTubeManager extends ManagerBase implements IYouTubeManager {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
	private GsonFactory factory = new GsonFactory();
    private static YouTube youtube;

    //TODO: Currently jomres Manager is not autowired in scheduler class,
    // temporarily autowiring jomres manager from this manager. need to mode jomres manager into jomres scheduler
    @Autowired
    JomresManager jomresManager;

    @PostConstruct
    public void init() {
        isSingleton = true;
        storeId = "all";
        initialize();
    }

    @Override
    public List<SearchResult> searchYoutube(String queryTerm) throws ErrorException {

        //TODO: Currently jomres Manager is not autowired in scheduler class,
        // temporarily called jomres methods from this manager.
        try{
            System.out.println("Jomres Scheduler started..");
            Gson gson = new Gson();
            List<FetchBookingResponse> jomresBookingResponse = jomresManager.fetchBookings();
            String updateAvailabilityResponse = jomresManager.updateAvailability();
            System.out.println(gson.toJson(jomresBookingResponse));
            System.out.println(updateAvailabilityResponse);
            System.out.println("Jomres Scheduler ended..");
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Jomres Scheduler failed..");
        }


//        // Is this still in use? Should we even support this?
//        try {
//            if(youtube == null) {
//                youtube = new YouTube.Builder(HTTP_TRANSPORT, factory, new HttpRequestInitializer() {
//                    public void initialize(HttpRequest request) throws IOException {
//                    }
//                }).setApplicationName("youtube-cmdline-search-sample").build();
//            }
//            YouTube.Search.List search = youtube.search().list("id,snippet");
//            String apiKey = "AIzaSyBtJvCqbqQF1rcEHxFKp-W7UTdnSOeXUrs";
//            search.setKey(apiKey);
//            search.setQ(queryTerm);
//            search.setType("video");
//            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
//            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
//            SearchListResponse searchResponse = search.execute();
//
//            List<com.google.api.services.youtube.model.SearchResult> searchResultList = searchResponse.getItems();
//
//            List<SearchResult> result = new ArrayList();
//            Iterator<com.google.api.services.youtube.model.SearchResult> iteratorSearchResults = searchResultList.iterator();
//            if (searchResultList != null) {
//                while (iteratorSearchResults.hasNext()) {
//
//                    com.google.api.services.youtube.model.SearchResult singleVideo = iteratorSearchResults.next();
//                    ResourceId rId = singleVideo.getId();
//
//                    // Double checks the kind is video.
//                    if (rId.getKind().equals("youtube#video")) {
//                        Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
//
//                        SearchResult sres = new SearchResult();
//                        sres.name = singleVideo.getSnippet().getTitle();
//                        sres.id = rId.getVideoId();
//                        sres.thumbnail = thumbnail.getUrl();
//                        result.add(sres);
//                    }
//                }
//            }
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ErrorException(1017);
//        }

        return new ArrayList();
    }
}
