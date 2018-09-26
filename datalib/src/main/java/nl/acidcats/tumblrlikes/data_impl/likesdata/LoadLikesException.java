package nl.acidcats.tumblrlikes.data_impl.likesdata;

/**
 * Created on 12/07/2018.
 */
public class LoadLikesException extends Exception {
    private int _code;

    public LoadLikesException(int code) {
        _code = code;
    }

    public int getCode() {
        return _code;
    }
}
