package com.mediamonks.mylikes.data.util;

import com.mediamonks.mylikes.data.vo.db.PhotoEntity;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrPhotoPostVO;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrPhotoVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoUtil {
    public static List<PhotoEntity> toPhotoEntities(TumblrLikeVO likeVO) {
        List<TumblrPhotoPostVO> postVOs = likeVO.photos();
        if (postVOs == null || postVOs.size() == 0) return null;

        List<PhotoEntity> photoEntities = new ArrayList<>();

        for (TumblrPhotoPostVO postVO : postVOs) {
            List<TumblrPhotoVO> photos = new ArrayList<>();

            // add original photo
            if (postVO.originalPhoto() != null) {
                photos.add(postVO.originalPhoto());
            }

            // add alt photos
            if (postVO.altPhotos() != null) {
                photos.addAll(postVO.altPhotos());
            }

            // skip if none were found
            if (photos.size() == 0) continue;

            // sort by size
            photos.sort(new PhotoSizeComparator());

            // store biggest
            photoEntities.add(new PhotoEntity(photos.get(0).url(), likeVO.id()));
        }

        return photoEntities;
    }

    private static class PhotoSizeComparator implements java.util.Comparator<TumblrPhotoVO> {
        @Override
        public int compare(TumblrPhotoVO photo1, TumblrPhotoVO photo2) {
            long sizeDiff = photo1.getSize() - photo2.getSize();

            // sort descending
            if (sizeDiff == 0) return 0;
            if (sizeDiff < 0) return 1;
            return -1;
        }
    }
}
