package de.urkallinger.copymanager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TestUtils {
	public static final String MKV = "mkv";
	public static final String DOC = "doc";
	public static final String CSV = "csv";
	public static final String TXT = "txt";
	public static final String AVI = "avi";
	public static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList(MKV, DOC, CSV, TXT, AVI));
}
