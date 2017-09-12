package nl.acidcats.tumblrlikes.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.Photo;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrPhotoPostVO;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrPhotoVO;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoUtil {
    public static List<Photo> toPhotoEntities(TumblrLikeVO likeVO) {
        List<TumblrPhotoPostVO> postVOs = likeVO.photos();
        if (postVOs == null || postVOs.size() == 0) return null;

        List<Photo> photos = new ArrayList<>();

        for (TumblrPhotoPostVO postVO : postVOs) {
            List<TumblrPhotoVO> tumblrPhotoVOs = new ArrayList<>();

            // add original photo
            if (postVO.originalPhoto() != null) {
                tumblrPhotoVOs.add(postVO.originalPhoto());
            }

            // add alt photos
            if (postVO.altPhotos() != null) {
                tumblrPhotoVOs.addAll(postVO.altPhotos());
            }

            // skip if none were found
            if (tumblrPhotoVOs.size() == 0) continue;

            // sort by size
            Collections.sort(tumblrPhotoVOs, new PhotoSizeComparator());

            // store biggest
            photos.add(Photo.create(tumblrPhotoVOs.get(0).url(), likeVO.id()));
        }

        return photos;
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
