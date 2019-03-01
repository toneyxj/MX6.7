package com.moxi.bookstore.utils;

import android.content.Context;

//import com.onyx.android.sdk.data.cms.OnyxBookProgress;
//import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
//import com.onyx.android.sdk.data.cms.OnyxMetadata;

/**
 * Created by Archer on 16/9/26.
 */
public class PrepareCMS {
    private Context mContext;

    public PrepareCMS(Context context) {
        this.mContext = context;
    }

//    private OnyxMetadata setManualInformation(OnyxMetadata metadata) {
//        ArrayList<String> authors = new ArrayList<String>();
//        authors.add("author1");
//        metadata.setProgress(new OnyxBookProgress(1, 327));
//        metadata.setPublisher("MOXI");
//        metadata.setAuthors(authors);
//        metadata.setLastAccess(new Date());
//        return metadata;
//    }

    public void insertCMS(String path) {
//        try {
//            OnyxMetadata metadata = OnyxMetadata.createFromFile(path);
//            metadata = setManualInformation(metadata);
//            OnyxCmsCenter.updateRecentReading(mContext, metadata);
//            OnyxCmsCenter.insertLibraryItem(this.mContext, new File(path));
//        }catch (Exception e){
//
//        }

    }
}
