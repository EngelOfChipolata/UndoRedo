package fr.ups.m2ihm.drawingtool.model.core;

import java.awt.Point;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Rectangle implements Shape {
    Point upperLeftCorner;
    Point lowerRightCorner;

    public Rectangle(Point source, Point destination) {
        this.upperLeftCorner = new Point(min(source.x, destination.x), min(source.y, destination.y));
        this.lowerRightCorner = new Point(max(source.x, destination.x), max(source.y, destination.y));
    }


    public Point getUpperLeftCorner() {
        return upperLeftCorner;
    }

    public Point getLowerRightCorner() {
        return lowerRightCorner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rectangle other = (Rectangle) obj;
        if (this.upperLeftCorner != other.upperLeftCorner && (this.upperLeftCorner == null || !this.upperLeftCorner.equals(other.upperLeftCorner))) {
            return false;
        }
        if (this.lowerRightCorner != other.lowerRightCorner && (this.lowerRightCorner == null || !this.lowerRightCorner.equals(other.lowerRightCorner))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.upperLeftCorner != null ? this.upperLeftCorner.hashCode() : 0);
        hash = 97 * hash + (this.lowerRightCorner != null ? this.lowerRightCorner.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Rectangle{" + "upperLeftCorner=" + upperLeftCorner + ", lowerRightCorner=" + lowerRightCorner + '}';
    }

}
