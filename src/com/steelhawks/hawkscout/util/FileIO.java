package com.steelhawks.hawkscout.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIO {

	public static String readTextFile(String filePath) {
		if (!new File(filePath).exists()) return null;
		String returnValue = "";
		FileReader file = null;
		String line = "";
		try {
			file = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {
				returnValue += line + "\n";
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnValue;
	}

	public static void writeTextFile(String filePath, String s) {
		FileWriter output = null;
		BufferedWriter writer = null;
		try {
			output = new FileWriter(filePath);
			writer = new BufferedWriter(output);
			writer.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					writer.close();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
} 
