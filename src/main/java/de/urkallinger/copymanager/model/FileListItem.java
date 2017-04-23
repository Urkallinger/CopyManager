package de.urkallinger.copymanager.model;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileListItem {

	private final String[] UNITS = {"B", "KB", "MB", "GB", "TB"};
	
	private final BooleanProperty chb = new SimpleBooleanProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty newName = new SimpleStringProperty();
	private final StringProperty extension = new SimpleStringProperty();
	private final ObjectProperty<SizeObj> size = new SimpleObjectProperty<>();
    
	private final String absolutPath;
	
	public FileListItem(File f) {
		absolutPath = f.getAbsolutePath();
    	setName(FilenameUtils.getBaseName(f.getName()));
    	setNewName("");
        setChecked(false);
        setExtension(FilenameUtils.getExtension(f.getName()));
        setSize(f.length(), genSizeText(f.length(), 0));
        
    }
	
	private final String genSizeText(double size, int unitIdx) {
		if (size >= 1024) {
			return genSizeText(size / 1024, unitIdx + 1);
		} else {
			double round = Math.round(size*100);
			return (round / 100) + " " + UNITS[unitIdx];
		}
	}

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public final StringProperty newNameProperty() {
        return this.newName;
    }

    public final String getNewName() {
        return this.newNameProperty().get();
    }
    
    public final void setNewName(String newName) {
    	this.newNameProperty().set(newName);
    }

    public final BooleanProperty chbProperty() {
        return this.chb;
    }

    public final boolean isChecked() {
        return this.chbProperty().get();
    }

    public final void setChecked(final boolean checked) {
        this.chbProperty().set(checked);
    }
    
    public final StringProperty extensionProperty() {
        return this.extension;
    }

    public final String getExtension() {
        return this.extensionProperty().get();
    }

    public final void setExtension(final String ext) {
        this.extensionProperty().set(ext);
    }

    public final ObjectProperty<SizeObj> sizeProperty() {
        return this.size;
    }

    public final SizeObj getSize() {
        return this.sizeProperty().get();
    }

    public final void setSize(final long size, final String text) {
        this.sizeProperty().set(new SizeObj(size, text));
    }

    public String getAbsolutPath() {
    	return absolutPath;
    }
    
	@Override
    public String toString() {
        return getName();
    }
	
	public class SizeObj implements Comparable<SizeObj>{
		private final long size;
		private final String text;

		public SizeObj(long size, String text) {
			this.size = size;
			this.text = text;
		}
		
		public long getSize() {
			return size;
		}

		public String getText() {
			return text;
		}

		@Override
		public int compareTo(SizeObj o) {
			return Long.compare(size, o.getSize());
		}
	}
}
