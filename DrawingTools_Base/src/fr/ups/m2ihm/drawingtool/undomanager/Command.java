/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.undomanager;

/**
 *
 * @author David Navarre
 */
public interface Command {
    void execute();
    void undo();
}
