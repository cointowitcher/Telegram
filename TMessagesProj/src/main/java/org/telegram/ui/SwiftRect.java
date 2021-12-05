package org.telegram.ui;

public class SwiftRect {
    public float x;
    public float y;
    public float width;
    public float height;

    public SwiftRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getRight() {
        return x + width;
    }

    public float getBottom() {
        return y + height;
    }
}
