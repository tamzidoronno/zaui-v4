/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.thundashop.core.youtubemanager;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.thundashop.core.common.GetShopLogHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Prints a list of videos based on a search term.
 *
 * @author Jeremy Walker
 */
public class Search {

    /**
     * Global instance properties filename.
     */
    private static String PROPERTIES_FILENAME = "youtube.properties";
    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the max number of videos we want returned (50 = upper
     * limit per page).
     */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    /**
     * Global instance of Youtube object to make all API requests.
     */
    private static YouTube youtube;

    /**
     * Initializes YouTube object to search for videos on YouTube
     * (Youtube.Search.List). The program then prints the names and thumbnails
     * of each of the videos (only first 50 videos).
     *
     * @param args command line args.
     */
    public static void main(String[] args) {
        
    }

    /*
     * Returns a query term (String) from user via the terminal.
     */
    private static String getInputQuery() throws IOException {

        String inputQuery = "";

        GetShopLogHandler.logPrintStaticSingle("Please enter a search term: ", null);
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // If nothing is entered, defaults to "YouTube Developers Live."
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }
}