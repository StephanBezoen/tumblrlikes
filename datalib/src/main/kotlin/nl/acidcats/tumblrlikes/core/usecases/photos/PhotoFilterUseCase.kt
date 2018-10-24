package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.constants.FilterType
import rx.Observable

/**
 * Created on 19/10/2018.
 */
interface PhotoFilterUseCase {
    fun getSelectedFilterType(): Observable<FilterType>
    fun storeFilterSelection(filterType: FilterType): Observable<FilterType>
}
