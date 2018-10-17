package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators;

/**
 * Created on 17/10/2018.
 */
public class LinearPhotoIterator extends AbstractPhotoIterator {

    private int _currentIndex = 0;

    @Override
    protected int getNextIndex() {
        _currentIndex = (_currentIndex + 1) % getTotalCount();

        return _currentIndex;
    }
}
