package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by stephan on 11/04/2017.
 */

@Entity
public class PhotoEntity {
    @Id(autoincrement = true)
    private Long id;

    private String url;

    private long photoId;

    private String filePath;

    private boolean isCached;

    private int viewCount;

    private long viewTime;

    private boolean isFavorite;

    private boolean isHidden;

    private long timePerView;

    private boolean isLiked;

    public PhotoEntity(String url, long photoId) {
        this.url = url;
        this.photoId = photoId;
    }

    @Generated(hash = 1936656342)
    public PhotoEntity(Long id, String url, long photoId, String filePath,
            boolean isCached, int viewCount, long viewTime, boolean isFavorite,
            boolean isHidden, long timePerView, boolean isLiked) {
        this.id = id;
        this.url = url;
        this.photoId = photoId;
        this.filePath = filePath;
        this.isCached = isCached;
        this.viewCount = viewCount;
        this.viewTime = viewTime;
        this.isFavorite = isFavorite;
        this.isHidden = isHidden;
        this.timePerView = timePerView;
        this.isLiked = isLiked;
    }

    @Generated(hash = 1889335700)
    public PhotoEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean getIsCached() {
        return this.isCached;
    }

    public void setIsCached(boolean isCached) {
        this.isCached = isCached;
    }

    public int getViewCount() {
        return this.viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public long getViewTime() {
        return this.viewTime;
    }

    public void setViewTime(long viewTime) {
        this.viewTime = viewTime;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean getIsHidden() {
        return this.isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public long getTimePerView() {
        return this.timePerView;
    }

    public void setTimePerView(long timePerView) {
        this.timePerView = timePerView;
    }

    public boolean getIsLiked() {
        return this.isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

}
