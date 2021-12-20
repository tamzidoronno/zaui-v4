package com.thundashop.repository.utils;

public class SessionInfo {

    private final String storeId;

    private final String currentUserId;

    private final String language;

    private final String managerName;

    private SessionInfo(String storeId, String currentUserId, String language, String managerName) {
        this.storeId = storeId;
        this.currentUserId = currentUserId;
        this.language = language;
        this.managerName = managerName;
    }

    public static SessionInfo empty() {
        return new SessionInfo("", "", "", "");
    }

    public static SessionInfoBuilder builder() {
        return new SessionInfoBuilder();
    }

    public String getStoreId() {
        return storeId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public String getLanguage() {
        return language;
    }

    public String getManagerName() {
        return managerName;
    }

    public static class SessionInfoBuilder {
        private String storeId = "";
        private String currentUserId = "";
        private String language = "";
        private String managerName = "";

        public SessionInfoBuilder setStoreId(String storeId) {
            this.storeId = storeId;
            return this;
        }

        public SessionInfoBuilder setCurrentUserId(String currentUserId) {
            this.currentUserId = currentUserId;
            return this;
        }

        public SessionInfoBuilder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public SessionInfoBuilder setManagerName(String managerName) {
            this.managerName = managerName;
            return this;
        }

        public SessionInfo build() {
            return new SessionInfo(storeId, currentUserId, language, managerName);
        }
    }

}
