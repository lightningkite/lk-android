package lk.kotlin.observable.property


/**
 * Combines several observable properties into one.
 * Created by joseph on 12/2/16.
 */
class CombineObservableProperty2<A, B, T>(
        val observableA: ObservableProperty<A>,
        val observableB: ObservableProperty<B>,
        val combine: (A, B) -> T
) : EnablingObservableProperty<T>(), ObservableProperty<T> {

    override var value = combine(observableA.value, observableB.value)

    override fun update() {
        value = combine(observableA.value, observableB.value)
        super.update()
    }

    val callbackA = { item: A ->
        update()
    }
    val callbackB = { item: B ->
        update()
    }

    override fun enable() {
        value = combine(observableA.value, observableB.value)
        observableA.add(callbackA)
        observableB.add(callbackB)
    }

    override fun disable() {
        observableA.remove(callbackA)
        observableB.remove(callbackB)
    }
}