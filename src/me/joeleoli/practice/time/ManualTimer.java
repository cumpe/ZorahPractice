package me.joeleoli.practice.time;

public class ManualTimer extends AbstractTimer {

	private long timerEnd;

	public ManualTimer(boolean prefferedFormat) {
		this(prefferedFormat, 0L);
	}

	public ManualTimer(boolean prefferedFormat, long timerEnd) {
		super(prefferedFormat);
		this.timerEnd = timerEnd;
	}

	public void setTimerEnd(long timerEnd) {
		this.timerEnd = timerEnd;
	}
	
	public void reset() {
		this.timerEnd = System.currentTimeMillis();
	}

	@Override
	public long getTimerEnd() {
		return this.timerEnd;
	}

}