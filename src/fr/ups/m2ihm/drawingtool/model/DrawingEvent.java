package fr.ups.m2ihm.drawingtool.model;

import java.awt.Point;

public class DrawingEvent {
    private final DrawingEventType eventType;
    private final Point point;

    public DrawingEvent(DrawingEventType eventType, Point point) {
        this.eventType = eventType;
        this.point = point;
    }


    public DrawingEventType getEventType() {
        return eventType;
    }

    public Point getPoint() {
        return point;
    }
}
