@file:JvmName("LkKotlinObservableList")
@file:JvmMultifileClass

package lk.kotlin.observable.list


import lk.kotlin.utils.lambda.invokeAll
import java.io.Closeable
import java.util.*

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class ObservableListMultiGroupingBy<E, G, L>(
        val source: ObservableList<E>,
        val grouper: (E) -> Collection<G>,
        val listWrapper: (ObservableList<E>) -> L,
        val innerList: ObservableListWrapper<Pair<G, L>> = observableListOf()
) : ObservableList<Pair<G, L>> by innerList, Closeable {

    private inner class InnerList() : ObservableListIndicies<E>(source) {
    }

    private val groupLists = HashMap<G, InnerList>()

    private fun getOrMakeGroup(group: G): InnerList {
        val current = groupLists[group]
        if (current != null) return current

        val list = InnerList()
        val wrapper = listWrapper(list)
        innerList.add(group to wrapper)
        groupLists[group] = list
        return list
    }

    private fun removeGroup(group: G) {
        groupLists.remove(group)
        val index = innerList.indexOfFirst { it.first == group }
        if (index != -1) {
            innerList.removeAt(index)
        }
    }

    private fun reset() {
        innerList.clear()
        groupLists.clear()

        for (index in source.indices) {
            val item = source[index]
            val groups = grouper(item)
            for (group in groups) {
                val current = getOrMakeGroup(group)
                current.indexList.add(index)
                current.onAdd.invokeAll(item, current.indexList.size - 1)
                current.onUpdate.update()
            }
        }
    }

    private fun modifyIndicesBy(after: Int, by: Int) {
        for ((group, inner) in groupLists) {
            for (indexIndex in inner.indexList.indices) {
                val index = inner.indexList[indexIndex]
                if (index >= after) {
                    inner.indexList[indexIndex] = index + by
                }
            }
        }
    }

    private fun modifyIndicesBy(after: Int, by: Int, setToIfEqual: Int) {
        for ((group, inner) in groupLists) {
            for (indexIndex in inner.indexList.indices) {
                val index = inner.indexList[indexIndex]
                if (index > after) {
                    inner.indexList[indexIndex] = index + by
                } else if (index == after) {
                    inner.indexList[indexIndex] = setToIfEqual
                }
            }
        }
    }

    private fun getCurrentIndexGroup(index: Int): Collection<G> {
        val result = ArrayList<G>()
        for ((group, list) in groupLists) {
            if (list.indexList.contains(index)) {
                result.add(group)
            }
        }
        return result
    }

    inner private class QueuedOnRemoveCall(val groupList: InnerList, val element: E, val index: Int) : () -> Unit, Comparable<QueuedOnRemoveCall> {
        override fun compareTo(other: QueuedOnRemoveCall): Int = index.compareTo(other.index)
        override fun invoke() {
            groupList.onRemove.invokeAll(element, index)
            groupList.onUpdate.update()
        }
    }

    inner private class QueuedOnAddCall(val groupList: InnerList, val element: E, val index: Int) : () -> Unit, Comparable<QueuedOnAddCall> {
        override fun compareTo(other: QueuedOnAddCall): Int = index.compareTo(other.index)
        override fun invoke() {
            groupList.onAdd.invokeAll(element, index)
            groupList.onUpdate.update()
        }
    }

    inner private class QueuedOnChangeCall(val groupList: InnerList, val oldElement: E, val element: E, val index: Int) : () -> Unit, Comparable<QueuedOnChangeCall> {
        override fun compareTo(other: QueuedOnChangeCall): Int = index.compareTo(other.index)
        override fun invoke() {
            groupList.onChange.invokeAll(oldElement, element, index)
            groupList.onUpdate.update()
        }
    }

    val listener = ObservableListListenerSet<E>(
            onAddListener = { item, index ->
                modifyIndicesBy(index, 1)
                val groups = grouper(item)
                for (group in groups) {
                    val list = getOrMakeGroup(group)
                    list.indexList.add(index)
                    list.onAdd.invokeAll(item, list.indexList.size - 1)
                    list.onUpdate.update()
                }
            },
            onRemoveListener = { item, index ->
                val onRemoveCalls = PriorityQueue<QueuedOnRemoveCall>()
                val groups = getCurrentIndexGroup(index)

                modifyIndicesBy(index, -1, Int.MIN_VALUE)

                for (group in groups) {
                    val groupList = groupLists[group]
                    if (groupList != null) {
                        val indexIndex = groupList.indexList.indexOf(Int.MIN_VALUE)
                        groupList.indexList.removeAt(indexIndex)
                        groupList.onRemove.invokeAll(item, indexIndex)
                        groupList.onUpdate.update()
                        if (groupList.isEmpty()) {
                            removeGroup(group)
                        }
                    } else throw IllegalArgumentException()
                }
            },
            onChangeListener = { oldItem, item, index ->
                val oldGroups = getCurrentIndexGroup(index).toSet()
                val newGroups = grouper(item).toSet()
                val addingGroups = newGroups.minus(oldGroups)
                val removingGroups = oldGroups.minus(newGroups)
                val sameGroups = newGroups.intersect(oldGroups)
                for (group in sameGroups) {
                    val groupList = groupLists[group]
                    if (groupList != null) {
                        val indexIndex = groupList.indexList.indexOf(index)
                        groupList.onChange.invokeAll(oldItem, item, indexIndex)
                        groupList.onUpdate.update()
                    } else throw IllegalArgumentException()
                }
                for (group in removingGroups) {
                    val oldGroupList = groupLists[group] ?: throw IllegalArgumentException()
                    val oldIndexIndex = oldGroupList.indexList.indexOf(index)
                    oldGroupList.indexList.removeAt(oldIndexIndex)
                    oldGroupList.onRemove.invokeAll(oldItem, oldIndexIndex)
                    oldGroupList.onUpdate.update()
                    if (oldGroupList.isEmpty()) {
                        removeGroup(group)
                    }
                }
                for (group in addingGroups) {
                    val newGroupList = getOrMakeGroup(group)
                    newGroupList.indexList.add(index)
                    newGroupList.onAdd.invokeAll(item, newGroupList.indexList.size - 1)
                    newGroupList.onUpdate.update()
                }
            },
            onMoveListener = { item, oldIndex, index ->
                val groups = getCurrentIndexGroup(oldIndex)
                for (group in groups) {
                    val groupList = groupLists[group]
                    if (groupList != null) {
                        val indexIndex = groupList.indexList.indexOf(oldIndex)
                        modifyIndicesBy(oldIndex, -1)
                        modifyIndicesBy(index, 1)
                        groupList.indexList[indexIndex] = index
                    } else throw IllegalArgumentException()
                }
            },
            onReplaceListener = { list ->
                reset()
            }
    )
//
//    private fun updateLists() {
//        val newCategories = source.map(grouper).toSet()
//        val oldCategories = innerList.map { it.first }.toSet()
//        for (category in newCategories) {
//            if (category !in oldCategories) {
//                val filtering = InnerList()
//                val wrapped = listWrapper(filtering)
//                innerLists[wrapped] = filtering
//                innerList.add(category to wrapped)
//            }
//        }
//        for (category in oldCategories) {
//            if (category !in newCategories) {
//                innerList.removeAll() { it.first == category }
//            }
//        }
//    }

    init {
        setup()
    }

    fun setup() {
        reset()
        source.addListenerSet(listener)
    }

    override fun close() {
        source.removeListenerSet(listener)
        innerList.clear()
    }
}

fun <E, G, L> ObservableList<E>.multiGroupingBy(
        grouper: (E) -> Collection<G>,
        listWrapper: (ObservableList<E>) -> L
) = ObservableListMultiGroupingBy(this, grouper, listWrapper)

fun <E, G> ObservableList<E>.multiGroupingBy(grouper: (E) -> Collection<G>) = multiGroupingBy(grouper, { it })