package imallett.FlashBang.imallett.FlashBang.Threading;

public abstract class ThreadBase extends Thread {
	public volatile boolean stop_requested = false;
}
