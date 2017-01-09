package fr.ups.m2ihm.drawingtool.model;

public enum DrawingEventType {
    BEGIN_DRAW("beginDrawProperty"),
    DRAW("drawProperty"),
    CANCEL_DRAW("cancelDrawProperty"),
    END_DRAW("endDrawProperty"),
    NO_DRAW("noDrawProperty");
    private final String propertyName;

    private DrawingEventType(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
