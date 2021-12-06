package org.telegram.messenger;

import android.graphics.Color;

import java.util.Random;

public class Randoms {
    public static int color() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }
}
