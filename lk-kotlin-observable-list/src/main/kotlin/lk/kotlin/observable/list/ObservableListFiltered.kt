@file:JvmName("LkKotlinObservableList")
@file:JvmMultifileClass

package lk.kotlin.observable.list


import lk.kotlin.observable.property.StandardObservableProperty
import lk.kotlin.utils.collection.addSorted
import lk.kotlin.utils.lambda.invokeAll
import java.io.Closeable
import java.util.*

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class ObservableListFiltered<E>(
        source: ObservableList<E>
) : ObservableListIndicies<E>(source), Closeable {
    init {
        indexList.addAll(source.indices)
    }

    val filterObs = StandardObservableProperty<(E) -> Boolean>({ true })
    var filter by filterObs

    //binding
    val bindings = ArrayList<Pair<MutableCollection<*>, *>>()

    fun <T> bind(collection: MutableCollection<T>, element: T) {
        bindings.add(collection to element)
    }

    var connected = false
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        if (connected) return
        for ((collection, element) in bindings) {
            (collection as MutableCollection<Any?>).add(element)
        }
        connected = true
    }

    override fun close() {
        if (!connected) return
        for ((collection, element) in bindings) {
            collection.remove(element)
        }
        connected = false
    }

    //filtering

    init {
        filterObs.add {
            if (source.none(filter)) {
                indexList.clear()
                onReplace.invokeAll(this)
            } else {
                var passingIndex = 0
                for (fullIndex in source.indices) {
                    var previouslyPassing = false
                    while (passingIndex < indexList.size) {
                        if (indexList[passingIndex] > fullIndex) {
                            previouslyPassing = false
                            break
                        }
                        if (indexList[passingIndex] == fullIndex) {
                            previouslyPassing = true
                            break
                        }
                        passingIndex++
                    }

                    val passes = filter(source[fullIndex])
                    if (passes && !previouslyPassing) {
                        //add to the list
                        val addPos = indexList.addSorted(fullIndex)
                        onAdd.invokeAll(source[fullIndex], addPos)
                    } else if (!passes && previouslyPassing) {
                        //remove from the list
                        indexList.removeAt(passingIndex)
                        onRemove.invokeAll(source[fullIndex], passingIndex)
                    }
                }
            }
            onUpdate.update()
        }
        bind(source.onAdd) { item, index ->
            val passes = filter(item)
            if (passes) {
                for (indexIndex in indexList.indices) {
                    if (indexList[indexIndex] >= index) {
                        indexList[indexIndex] += 1
                    }
                }
                val indexOf = indexList.addSorted(index)
                onAdd.invokeAll(item, indexOf)
                onUpdate.update()
            } else {
                for (indexIndex in indexList.indices) {
                    if (indexList[indexIndex] >= index) {
                        indexList[indexIndex] += 1
                    }
                }
            }
        }
        bind(source.onChange) { old, item, index ->
            val passes = filter(item)
            val indexOf = indexList.indexOf(index)
            val passed = indexOf != -1
            if (passes != passed) {
                if (passes) {
                    val insertionIndex = indexList.addSorted(index)
                    onAdd.invokeAll(item, insertionIndex)
                    onUpdate.update()
                } else {
                    indexList.removeAt(indexOf)
                    onRemove.invokeAll(old, indexOf)
                    onUpdate.update()
                }
            } else {
                if (indexOf != -1) {
                    onChange.invokeAll(old, item, indexOf)
                    onUpdate.update()
                }
            }
        }
        bind(source.onMove) { item, oldIndex, index ->
            //remove from indexList
            val oldIndexOf = indexList.indexOf(oldIndex)
            if (oldIndexOf == -1) return@bind
            for (indexIndex in indexList.indices) {
                if (indexList[indexIndex] > oldIndex) {
                    indexList[indexIndex] -= 1
                }
            }
            indexList.remove(oldIndex)

            //add back into indexList
            val passes = filter(item)
            if (passes) {
                for (indexIndex in indexList.indices) {
                    if (indexList[indexIndex] >= index) {
                        indexList[indexIndex] += 1
                    }
                }
                val indexOf = indexList.addSorted(index)
                onMove.invokeAll(item, oldIndexOf, indexOf)
                onUpdate.update()
            }
        }
        bind(source.onRemove) { item, index ->
            val oldIndexOf = indexList.indexOf(index)
            for (indexIndex in indexList.indices) {
                if (indexList[indexIndex] > index) {
                    indexList[indexIndex] -= 1
                }
            }
            if (oldIndexOf == -1) return@bind
            indexList.remove(index)
            onRemove.invokeAll(item, oldIndexOf)
            onUpdate.update()
        }
        bind(source.onReplace) {
            indexList.clear()
            for (i in source.indices) {
                val passes = filter(source[i])
                if (passes) indexList.add(i)
            }
            onReplace.invokeAll(this)
            onUpdate.update()
        }
        setup()
    }
}

fun <E> ObservableList<E>.filtering(): ObservableListFiltered<E> = ObservableListFiltered(this)

fun <E> ObservableList<E>.filtering(initFilter: (E) -> Boolean): ObservableListFiltered<E> = ObservableListFiltered(this).apply {
    filter = initFilter
}