package de.urkallinger.copymanager;

public interface CMLogger {
	int action(String action, boolean indicator);
	void setDone(int idx);
	void setFailed(int idx);
	void setProgress(double x);
	void enableProgressBar(boolean enable, final long millis);
}
