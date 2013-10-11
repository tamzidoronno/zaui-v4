package com.thundashop.core.youtubemanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.youtubemanager.data.SearchResult;
import java.util.List;

/**
 * The youtube manager handles the communication between the google youtube api and the frontend.
 */
@GetShopApi
public interface IYouTubeManager {
    public List<SearchResult> searchYoutube(String searchword) throws ErrorException;
}
