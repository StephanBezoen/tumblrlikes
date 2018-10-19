package nl.acidcats.tumblrlikes.core.usecases.photos;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import rx.Observable;

/**
 * Created on 19/10/2018.
 */
public interface PhotoFilterUseCase {
    Observable<Boolean> storeFilterSelection(FilterType filterType);

    Observable<FilterType> getSelectedFilterType();
}
