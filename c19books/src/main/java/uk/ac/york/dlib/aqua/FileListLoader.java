package uk.ac.york.dlib.aqua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileListLoader {
	private static List<String> fileList = new ArrayList<String>();
	
	public static void load() {
		// Create file
		File file = new File("src/main/resources/filelist.txt");
		BufferedReader in=null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			String s;

			s = in.readLine();
			while (s != null) {
				fileList.add(s);
				s = in.readLine();
			}
		} catch (FileNotFoundException e1) {
			System.err.println("File not found: " + file);
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<String> getFileLisst() {
		if (fileList.size() == 0)
			FileListLoader.load();
		
		return fileList;
	}
}
