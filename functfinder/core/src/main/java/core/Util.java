package core;

import core.model.Data;

public class Util {

    public static String dataCollectionName(String profile) {
        String collectionName;
        if (profile == null || profile.trim().isEmpty()) {
            collectionName = Data.class.getSimpleName();
        } else {
            collectionName = String.format("%s-%s", profile, Data.class.getSimpleName());
        }
        return collectionName;
    }

    public static String getProfile(String collectionName) {
        int i = collectionName.lastIndexOf('-');
        String profile;
        if (i > 0) {
            profile = collectionName.substring(0, i);
        } else {
            profile = collectionName;
        }
        return profile;
    }
}
