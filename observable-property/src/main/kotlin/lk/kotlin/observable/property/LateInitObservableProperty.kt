package lk.kotlin.observable.property


/**
 * An observable that doesn't have to be set at its creation.
 * Created by jivie on 2/11/16.
 */
open class LateInitObservableProperty<T : Any>() : BaseObservableProperty<T>() {

    var internalValue: T? = null
    override var value: T
        get() = internalValue ?: throw IllegalStateException("Value not set.")
        set(value) {
            internalValue = value
            update()
        }
}