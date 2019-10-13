package com.example.ahmad2.shopproject;

import android.content.Context;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.core.view.ViewCompat;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabHide extends FloatingActionButton.Behavior {

    public FabHide(Context context, AttributeSet attrs) {

        super();

    }

    @Override

    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        //child -> Floating Action Button

        if (dyConsumed > 0) {

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

            int fab_bottomMargin = layoutParams.bottomMargin;

            child.animate().translationY(child.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();

        } else if (dyConsumed < 0) {

            child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();

        }

    }

    @Override

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;

    }














//        private Handler handler = new Handler();
//        private FloatingActionButton fab;
//
//        public FabHide(Context context, AttributeSet attrs) {
//            super();
//        }
//
//        @Override
//        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
//                                           FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
//            fab = child;
//            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
//                    super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
//                            nestedScrollAxes);
//        }
//
//
//
//        Runnable showRunnable = new Runnable() {
//            @Override
//            public void run() {
//                fab.show();
//            }
//        };
//
//
//        @Override
//        public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
//                                   View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
//                    dyUnconsumed);
//            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
//                handler.removeCallbacks(showRunnable);
//                handler.postDelayed(showRunnable,2000);
//                child.hide();
//            }
//        }


}
