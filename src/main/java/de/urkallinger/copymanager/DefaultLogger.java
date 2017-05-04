package de.urkallinger.copymanager;

public class DefaultLogger implements CMLogger {

	@Override
	public void info(String info) {
		System.out.println("INFO: " + info);
	}

	@Override
	public void warning(String warning) {
		System.out.println("WARNING: " + warning);
	}

	@Override
	public void error(String error) {
		System.err.println("ERROR: " + error);
	}

	@Override
	public int action(String action, boolean indicator) {
		System.out.println("ACTION: " + action);
		return -1;
	}

	@Override
	public void setDone(int idx) {}

	@Override
	public void setFailed(int idx) {}

	@Override
	public void setProgress(double x) {
		int p = (int) (x*100);
		System.out.println(p+"%");
	}

	@Override
	public void enableProgressBar(boolean enable, final long millis) {}

}
