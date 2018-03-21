package com.sf.vivosmainapp;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.polyak.iconswitch.IconSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IconSwitch.CheckedChangeListener,
        ValueAnimator.AnimatorUpdateListener {


    private static final int DURATION_COLOR_CHANGE_MS = 400;
    private int[] toolbarColors;
    private int[] statusBarColors;
    private ValueAnimator statusBarAnimator;
    private Interpolator contentInInterpolator;
    private Interpolator contentOutInterpolator;
    private Point revealCenter;

    private Window window;
    private View toolbar;
    private View content;
    private IconSwitch iconSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        window = getWindow();

        initColors();
        initAnimationRelatedFields();


        //content = findViewById(R.id.content);
        toolbar = findViewById(R.id.toolbar);
        iconSwitch = (IconSwitch) findViewById(R.id.icon_switch);
        iconSwitch.setCheckedChangeListener(this);
        updateColors(false);

        if (savedInstanceState == null)
            changeFragment(new ListFragment());


    }

    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out)
                .replace(R.id.container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onCheckChanged(IconSwitch.Checked current) {
        updateColors(true);
        changeContentVisibility();
    }

    private void changeContentVisibility() {
        /*
        int targetTranslation = 0;
        Interpolator interpolator = null;
        switch (iconSwitch.getChecked()) {
            case LEFT:
                targetTranslation = 0;
                interpolator = contentInInterpolator;
                break;
            case RIGHT:
                targetTranslation = content.getHeight();
                interpolator = contentOutInterpolator;
                break;
        }
        content.animate().cancel();
        content.animate()
                .translationY(targetTranslation)
                .setInterpolator(interpolator)
                .setDuration(DURATION_COLOR_CHANGE_MS)
                .start();*/

        switch (iconSwitch.getChecked()) {
            case LEFT:
                changeFragment(new ListFragment());
                break;
            case RIGHT:
                changeFragment(new MapFragment());
                break;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        if (animator == statusBarAnimator) {
            int color = (Integer) animator.getAnimatedValue();
            window.setStatusBarColor(color);
        }
    }

    private void updateColors(boolean animated) {
        int colorIndex = iconSwitch.getChecked().ordinal();
        toolbar.setBackgroundColor(toolbarColors[colorIndex]);
        if (animated) {
            switch (iconSwitch.getChecked()) {
                case LEFT:
                    statusBarAnimator.reverse();
                    break;
                case RIGHT:
                    statusBarAnimator.start();
                    break;
            }
            revealToolbar();
        } else {
            window.setStatusBarColor(statusBarColors[colorIndex]);
        }
    }

    private void revealToolbar() {
        iconSwitch.getThumbCenter(revealCenter);
        moveFromSwitchToToolbarSpace(revealCenter);
        ViewAnimationUtils.createCircularReveal(toolbar,
                revealCenter.x, revealCenter.y,
                iconSwitch.getHeight(), toolbar.getWidth())
                .setDuration(DURATION_COLOR_CHANGE_MS)
                .start();
    }

    private void initAnimationRelatedFields() {
        revealCenter = new Point();
        statusBarAnimator = createArgbAnimator(
                statusBarColors[IconSwitch.Checked.LEFT.ordinal()],
                statusBarColors[IconSwitch.Checked.RIGHT.ordinal()]);
        contentInInterpolator = new OvershootInterpolator(0.5f);
        contentOutInterpolator = new DecelerateInterpolator();
    }

    private void initColors() {
        toolbarColors = new int[IconSwitch.Checked.values().length];
        statusBarColors = new int[toolbarColors.length];
        toolbarColors[IconSwitch.Checked.LEFT.ordinal()] = color(R.color.informationPrimary);
        statusBarColors[IconSwitch.Checked.LEFT.ordinal()] = color(R.color.informationPrimaryDark);
        toolbarColors[IconSwitch.Checked.RIGHT.ordinal()] = color(R.color.mapPrimary);
        statusBarColors[IconSwitch.Checked.RIGHT.ordinal()] = color(R.color.mapPrimaryDark);
    }

    private ValueAnimator createArgbAnimator(int leftColor, int rightColor) {
        ValueAnimator animator = ValueAnimator.ofArgb(leftColor, rightColor);
        animator.setDuration(DURATION_COLOR_CHANGE_MS);
        animator.addUpdateListener(this);
        return animator;
    }

    private void moveFromSwitchToToolbarSpace(Point point) {
        point.set(point.x + iconSwitch.getLeft(), point.y + iconSwitch.getTop());
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

}
