package com.moldedbits.echo.chat.utils;

import android.animation.Animator;
import android.view.View;

public class AnimationUtils {

    public static final long SHORT_DURATION = 1000;
    public static final float TRANSLATION_TOP = -5F;
    public static final float TRANSLATION_BOTTOM = 5F;

    public static void hideFromTop(final View view) {
        view.animate().alpha(0f).setDuration(SHORT_DURATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void showFromBottom(final View view) {
        view.animate().alpha(1f).setDuration(SHORT_DURATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
}
