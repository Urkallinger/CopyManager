package de.urkallinger.copymanager.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.ParamCallback;
import de.urkallinger.copymanager.TaskManager;
import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.data.Task;
import de.urkallinger.copymanager.data.TaskStatus;
import de.urkallinger.copymanager.utils.Str;

public class FileReader implements Runnable {

	private final File rootDir;
	private final ParamCallback<List<FileListItem>> callback;

	public FileReader(File rootDir, ParamCallback<List<FileListItem>> callback) {
		this.rootDir = rootDir;
		this.callback = callback;
	}

	@Override
	public void run() {
		String taskText = String.format(Str.get("FileReader.read_action"), rootDir);
		Task task = new Task(taskText, TaskStatus.IN_PROGRESS);
        TaskManager.addTask(task);

		Collection<File> files = FileUtils.listFiles(rootDir, null, true);
		List<FileListItem> items = new ArrayList<>(files.size());
		files.forEach(f -> items.add(new FileListItem(f)));

		task.setStatus(TaskStatus.SUCCESS);
		TaskManager.updateTask(task);
		callback.call(items);
	}
}
