package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators;

import java.util.Iterator;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.FilterOption;

/**
 * Created on 17/10/2018.
 */
public abstract class AbstractPhotoIterator implements Iterator<PhotoEntity> {

    private int _size;
    private FilterOption _filterOption;

    public void setFilterOption(FilterOption filterOption) {
        _filterOption = filterOption;

        _size = filterOption.getCount();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public PhotoEntity next() {
        return _filterOption.getPhoto(getNextIndex());
    }

    protected abstract int getNextIndex();

    int getTotalCount() {
        return _size;
    }
}
