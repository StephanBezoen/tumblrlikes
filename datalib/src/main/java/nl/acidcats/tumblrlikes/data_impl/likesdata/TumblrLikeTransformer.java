package nl.acidcats.tumblrlikes.data_impl.likesdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO;
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrPhotoPostVO;
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrPhotoVO;

/**
 * Created by stephan on 11/04/2017.
 */

class TumblrLikeTransformer {
    List<Photo> transformToPhotos(TumblrLikeVO likeVO) {
        List<TumblrPhotoPostVO> postVOs = likeVO.getPhotos();
        if (postVOs == null || postVOs.size() == 0) return null;

        List<Photo> photos = new ArrayList<>();

        for (TumblrPhotoPostVO postVO : postVOs) {
            List<TumblrPhotoVO> tumblrPhotoVOs = new ArrayList<>();

            // add original photo
            if (postVO.getOriginalPhoto() != null) {
                tumblrPhotoVOs.add(postVO.getOriginalPhoto());
            }

            // add alt photos
            if (postVO.getAltPhotos() != null) {
                tumblrPhotoVOs.addAll(postVO.getAltPhotos());
            }

            // skip if none were found
            if (tumblrPhotoVOs.size() == 0) continue;

            // sort by size
            Collections.sort(tumblrPhotoVOs, new PhotoSizeComparator());

            // store biggest
            photos.add(new Photo(0, likeVO.getId(), null, tumblrPhotoVOs.get(0).getUrl(), false, false, false, 0, 0L, 0L));
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
