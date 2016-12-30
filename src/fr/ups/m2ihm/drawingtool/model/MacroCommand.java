/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.model;

import fr.ups.m2ihm.drawingtool.undomanager.Command;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author guilhem
 */
public class MacroCommand implements Command{
    
    private String name;
    private List<Command> commands;

    public MacroCommand() {
        name = null;
        commands = new ArrayList<>();
    }
    

    @Override
    public void execute() {
        commands.forEach((Command c) -> {
            c.execute();
        });
    }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i < 0; i--){ //Undo the commands backward !
            commands.get(i).undo();
        }
    }
    
    public void add(Command c){
        commands.add(c);
    }
    
}
