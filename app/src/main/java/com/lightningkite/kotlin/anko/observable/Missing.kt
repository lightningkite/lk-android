package com.lightningkite.kotlin.anko.observable

import android.view.View
import lk.android.activity.access.ActivityAccess
import lk.android.animations.AnimationSet
import lk.android.animations.SwapView
import lk.android.lifecycle.lifecycle
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.StackObservableProperty
import lk.kotlin.observable.property.lifecycle.bind

fun <T : (ActivityAccess) -> View> SwapView.bind(
        access: ActivityAccess,
        observable: ObservableProperty<T>,
        getAnimation: (T) -> AnimationSet = { AnimationSet.fade }
) {
    lifecycle.bind(observable) {
        swap(it.invoke(access), getAnimation(it))
    }
}

fun <T : (ActivityAccess) -> View> SwapView.bind(
        access: ActivityAccess,
        observable: StackObservableProperty<T>,
        pushAnimationSet: AnimationSet = AnimationSet.slidePush,
        neutralAnimationSet: AnimationSet = AnimationSet.fade,
        popAnimationSet: AnimationSet = AnimationSet.slidePop
) {
    var previousSize = observable.stack.size
    bind(
            access = access,
            observable = observable,
            getAnimation = {
                val diff = observable.stack.size - previousSize
                previousSize = observable.stack.size
                if (diff > 0) pushAnimationSet
                else if (diff < 0) popAnimationSet
                else neutralAnimationSet
            }
    )
}

fun <T> SwapView.bindRenderMap(
        access: ActivityAccess,
        observable: ObservableProperty<T>,
        getView: (T) -> (ActivityAccess) -> View,
        getAnimation: (T) -> AnimationSet = { AnimationSet.fade }
) {
    lifecycle.bind(observable) {
        swap(getView(it).invoke(access), getAnimation(it))
    }
}

fun <T> SwapView.bindRenderMapStack(
        access: ActivityAccess,
        observable: StackObservableProperty<T>,
        getView: (T) -> (ActivityAccess) -> View,
        pushAnimationSet: AnimationSet = AnimationSet.slidePush,
        neutralAnimationSet: AnimationSet = AnimationSet.fade,
        popAnimationSet: AnimationSet = AnimationSet.slidePop
) {
    var previousSize = observable.stack.size
    bindRenderMap(
            access = access,
            observable = observable,
            getView = getView,
            getAnimation = {
                val diff = observable.stack.size - previousSize
                previousSize = observable.stack.size
                if (diff > 0) pushAnimationSet
                else if (diff < 0) popAnimationSet
                else neutralAnimationSet
            }
    )
}