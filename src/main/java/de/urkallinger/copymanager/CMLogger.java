package de.urkallinger.copymanager;

public interface CMLogger {
	void info(String info);
	void warning(String warning);
	void error(String error);
	int action(String action, boolean indicator);
	void setDone(int idx);
	void setFailed(int idx);
	void setProgress(double x);
	void enableProgressBar(boolean enable);
}
