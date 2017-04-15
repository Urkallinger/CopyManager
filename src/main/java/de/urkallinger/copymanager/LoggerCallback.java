package de.urkallinger.copymanager;

public interface LoggerCallback {
	void info(String info);
	void warning(String warning);
	void error(String error);
	int action(String action, boolean indicator);
	void setDone(int idx);
	void setFailed(int idx);
}
