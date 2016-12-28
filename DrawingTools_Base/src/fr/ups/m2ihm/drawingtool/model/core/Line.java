package fr.ups.m2ihm.drawingtool.model.core;

import java.awt.Point;

public class Line implements Shape {
    Point source;
    Point destination;

    public Line(Point source, Point destination) {
        this.source = source;
        this.destination = destination;
    }

    public Point getSource() {
        return source;
    }

    public Point getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Line other = (Line) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.destination != other.destination && (this.destination == null || !this.destination.equals(other.destination))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 13 * hash + (this.destination != null ? this.destination.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Line{" + "source=" + source + ", destination=" + destination + '}';
    }


}
