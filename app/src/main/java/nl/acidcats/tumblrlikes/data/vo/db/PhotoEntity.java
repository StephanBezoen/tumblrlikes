package nl.acidcats.tumblrlikes.data.vo.db;

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

    public PhotoEntity(String url, long photoId) {
        this.url = url;
        this.photoId = photoId;
    }

    @Generated(hash = 1406380572)
    public PhotoEntity(Long id, String url, long photoId) {
        this.id = id;
        this.url = url;
        this.photoId = photoId;
    }

    @Generated(hash = 1889335700)
    public PhotoEntity() {
    }

    @Override
    public String toString() {
        return "PhotoVO{" +
                "url='" + url + '\'' +
                '}';
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
}
