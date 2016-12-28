package fr.ups.m2ihm.drawingtool.undomanager;

public enum UndoEvent {
    UNDO("undoProperty"),
    REDO("redoProperty");
    private String propertyName;

    private UndoEvent(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
