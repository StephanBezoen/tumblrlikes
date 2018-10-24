package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class PhotoFilterUseCaseImpl @Inject constructor(private val photoDataRepository: PhotoDataRepository) : PhotoFilterUseCase {

    override fun getSelectedFilterType(): Observable<FilterType> {
        return Observable.just(photoDataRepository.getFilterType())
    }

    override fun storeFilterSelection(filterType: FilterType): Observable<FilterType> {
        photoDataRepository.setFilterType(filterType)

        return Observable.just(filterType)
    }
}