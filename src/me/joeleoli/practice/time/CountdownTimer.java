package me.joeleoli.practice.time;

public interface CountdownTimer {
	
    boolean isActive();
    
    long getTimeLeft();
    
    long getTimerEnd();
    
    String toString();
    
    String toString(boolean usePreferred);
    
}