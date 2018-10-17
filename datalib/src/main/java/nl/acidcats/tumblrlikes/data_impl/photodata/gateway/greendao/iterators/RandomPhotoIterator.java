package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators;

/**
 * Created on 17/10/2018.
 */
public class RandomPhotoIterator extends AbstractPhotoIterator {

    @Override
    protected int getNextIndex() {
        return (int) (Math.floor(getTotalCount() * Math.random()));
    }
}
