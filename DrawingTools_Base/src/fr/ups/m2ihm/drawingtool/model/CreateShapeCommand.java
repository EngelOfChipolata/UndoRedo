/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.model;

import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Shape;
import fr.ups.m2ihm.drawingtool.undomanager.Command;

/**
 *
 * @author David Navarre
 */
class CreateShapeCommand implements Command {

    private final DrawingToolCore core;
    private final Shape shape;

    public CreateShapeCommand(DrawingToolCore core, Shape shape) {
        this.core = core;
        this.shape = shape;
    }

    @Override
    public void execute() {
        core.createShape(shape);
    }

    @Override
    public void undo() {
        core.removeShape(shape);
    }

}
