package de.urkallinger.copymanager.callables;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.config.Settings;
import de.urkallinger.copymanager.model.FileListItem;
import de.urkallinger.copymanager.utils.Str;

public class FileCopier implements Runnable {

	private final List<FileListItem> files;
	private final File targetDir;
	private final Configuration cfg;

	public FileCopier(List<FileListItem> files, File targetDir) {
		this.files = files;
		this.targetDir = targetDir;
		this.cfg = ConfigurationManager.loadConfiguration();
	}

	@Override
	public void run() {
		double stepSize = 1.0 / files.size();
		double step = 0;
		MainApp.getLogger().enableProgressBar(true, 0);
		for (FileListItem file : files) {
			final String name = file.getNewName().isEmpty() ? file.getName() : file.getNewName();
			final String newName = name + "." + file.getExtension();

			File from = new File(file.getAbsolutPath());
			File to = new File(targetDir, newName);

			// Wenn Ziel nicht existiert -> kopieren
			// Wenn Ziel exisitiert und überschreiben aktiviert ist -> kopieren
			if (!to.exists() || (to.exists() && cfg.getSetting(Settings.OVERRIDE_FILES))) {
				Thread copyFilesThread = new Thread(getCpyFileRunnable(from, to));
				copyFilesThread.setDaemon(true);
				copyFilesThread.start();

				try {
					copyFilesThread.join();
				} catch (InterruptedException e) {
					MainApp.getLogger().error(e.getMessage());
				}

			} else {
				String msg = String.format(Str.get("FileCopier.override_file_exists"), to.getAbsolutePath());
				MainApp.getLogger().warning(msg);
			}
			step += stepSize;
			MainApp.getLogger().setProgress(step);
		}
		MainApp.getLogger().enableProgressBar(false, 2000);
	}

	private Runnable getCpyFileRunnable(File from, File to) {
		return () -> {
			String action = String.format(Str.get("FileCopier.copy_action"), from.getAbsolutePath(),
					to.getAbsolutePath());
			int idx = MainApp.getLogger().action(action, true);
			try {
				FileUtils.copyFile(from, to);
				
				if(md5(from).equals(md5(to))) {
					MainApp.getLogger().setDone(idx);
				} else {
					MainApp.getLogger().setFailed(idx);
					MainApp.getLogger().error(Str.get("FileCopier.checksum_error"));
				}
			} catch (IOException | NoSuchAlgorithmException e) {
				MainApp.getLogger().setFailed(idx);
				MainApp.getLogger().error(e.getMessage());
			}
		};
	}

	private static String md5(File f) throws NoSuchAlgorithmException, IOException {
		InputStream is = new FileInputStream(f);
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[8192];
			int read = 0;

			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			
			return bigInt.toString(16);
		} finally {
			try { is.close(); } catch (Exception e) {}
		}
	}
}
