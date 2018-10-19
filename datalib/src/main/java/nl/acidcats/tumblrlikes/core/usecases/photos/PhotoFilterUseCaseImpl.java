package nl.acidcats.tumblrlikes.core.usecases.photos;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;

/**
 * Created on 19/10/2018.
 */
public class PhotoFilterUseCaseImpl implements PhotoFilterUseCase {

    private PhotoDataRepository _photoDataRepository;

    @Inject
    public PhotoFilterUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;
    }

    @Override
    public Observable<Boolean> storeFilterSelection(FilterType filterType) {
        _photoDataRepository.setFilterType(filterType);

        return Observable.just(true);
    }

    @Override
    public Observable<FilterType> getSelectedFilterType() {
        return Observable.just(_photoDataRepository.getFilterType());
    }
}
