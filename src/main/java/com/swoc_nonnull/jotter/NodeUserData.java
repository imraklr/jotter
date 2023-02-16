package com.swoc_nonnull.jotter;

@SuppressWarnings("unused")
class NodeUserData {
    private boolean isResizable;
    private boolean isArcifiable;
    private boolean isMovable;
    private boolean isLayoutElement;

    NodeUserData(boolean isResizable, boolean isArcifiable, boolean isMovable, boolean isLayoutElement) {
        this.isResizable = isResizable;
        this.isArcifiable = isArcifiable;
        this.isMovable = isMovable;
        this.isLayoutElement = isLayoutElement;
    }

    public boolean isLayoutElement() {
        return isLayoutElement;
    }

    public void setLayoutElement(boolean layoutElement) {
        isLayoutElement = layoutElement;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public void setMovable(boolean movable) {
        isMovable = movable;
    }

    public boolean isResizable() {
        return isResizable;
    }

    public void setResizable(boolean resizable) {
        isResizable = resizable;
    }

    public boolean isArcifiable() {
        return isArcifiable;
    }

    public void setArcifiable(boolean arcifiable) {
        isArcifiable = arcifiable;
    }
}