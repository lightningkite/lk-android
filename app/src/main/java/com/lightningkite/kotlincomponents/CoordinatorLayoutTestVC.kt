package com.lightningkite.kotlincomponents

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import lk.android.activity.access.ActivityAccess
import lk.android.activity.access.ViewGenerator
import lk.android.extensions.selectableItemBackgroundResource
import lk.android.observable.bindAny
import lk.anko.activity.access.anko
import lk.anko.adapters.observable.listAdapter
import lk.anko.extensions.verticalRecyclerView
import lk.kotlin.observable.list.ObservableListWrapper
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.collapsingToolbarLayout
import org.jetbrains.anko.design.coordinatorLayout

/**
 * A view for testing the coordinator layout.
 */
class CoordinatorLayoutTestVC() : ViewGenerator {

    override fun toString(): String {
        return "Coordinator Layout Test"
    }

    val junk = ObservableListWrapper((0..100).map { Math.random() }.toMutableList())

    override fun invoke(access: ActivityAccess): View = access.anko {
        coordinatorLayout {
            appBarLayout {
                collapsingToolbarLayout {

                    toolbar() {
                        title = "Pinned Title"

                        layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, dip(30)).apply {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_OFF
                        }
                    }

                    imageView(R.mipmap.test_image) {
                        setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
                        scaleType = ImageView.ScaleType.CENTER_CROP

                        layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, wrapContent).apply {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }
                    }

                }.lparams { scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED }

                textView("Constant Bar") {
                    padding = dip(8)
                    textSize = 18f
                    textColor = Color.WHITE
                    backgroundColor = Color.GRAY
                }.lparams(matchParent, wrapContent) {
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                }
            }.lparams(matchParent, wrapContent)

            verticalRecyclerView() {
                adapter = listAdapter(junk) { itemObs ->
                    textView() {
                        backgroundResource = selectableItemBackgroundResource
                        padding = dip(8)
                        gravity = Gravity.CENTER
                        textColor = Color.WHITE
                        textSize = 18f
                        bindAny(itemObs)
                    }.lparams(matchParent, wrapContent)
                }
            }.lparams(matchParent, matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}