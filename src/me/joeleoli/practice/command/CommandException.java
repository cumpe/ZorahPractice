package me.joeleoli.practice.command;

import java.util.Collections;
import java.util.List;

public class CommandException extends Exception {

    /**
	 * 
	 */
	private static long serialVersionUID = 8045396176987395025L;
	private List<String> messages;
    
    public CommandException() {
        this.messages = Collections.singletonList("Failed to execute command.");
    }
    
    public CommandException(List<String> messages) {
        this.messages = messages;
    }
    
    public List<String> getMessages() {
        return this.messages;
    }

}