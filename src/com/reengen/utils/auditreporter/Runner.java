package com.reengen.utils.auditreporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Runner {

	private List<List<String>> users;
	private List<List<String>> files;
	private List<String> csvData;
	List<Dictionary> orderedData;
	Dictionary singleDictionaryElement;
	private MergeSort mergeSort;
	private static String command = "-C";
	public static String outputFile = "/output.csv";

	public int size = 0;

	public static void main(String[] args) throws IOException {
		Runner r = new Runner();
		for (String s : args) {
			System.out.println(s);
		}
		r.loadData("resources/users.csv", "resources/files.csv");
		// r.loadData(args[0], args[1]);
		r.run();
	}

	private void run() {
		// printHeader();
		csvData = new ArrayList<String>();
		mergeSort = new MergeSort(orderedData);

		if (command.toLowerCase().equals("-c")) {
			for (List<String> userRow : users) {
				long userId = Long.parseLong(userRow.get(0));
				String userName = userRow.get(1);
				// printUserHeader(userName);
				for (Dictionary fileRow : orderedData) {
					if (Long.parseLong(fileRow.userId) == userId) {
						csvData.add(userName + "," + fileRow.fileName + "," + String.valueOf(fileRow.fileSize));
					}
				}
			}
			generateGeneralCSV();
		}

		else if (command.contains("--top")) {
			mergeSort.mergeSort(orderedData);
			String[] splited = command.split("\\s+");
			generateTopCSV(Integer.parseInt(splited[1]));
		}
	}

	private void loadData(String userFn, String filesFn) throws IOException {
		String line;
		BufferedReader reader = null;
		singleDictionaryElement = new Dictionary();
		orderedData = new ArrayList<Dictionary>();
		try {
			reader = new BufferedReader(new FileReader(userFn));
			users = new ArrayList<List<String>>();

			reader.readLine(); // skip header

			while ((line = reader.readLine()) != null) {
				users.add(Arrays.asList(line.split(",")));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		reader = null;
		try {
			reader = new BufferedReader(new FileReader(filesFn));
			files = new ArrayList<List<String>>();

			reader.readLine(); // skip header

			while ((line = reader.readLine()) != null) {
				line = line.substring(line.indexOf(",") + 1);
				singleDictionaryElement.fileSize = Long.parseLong(line.split(",")[0]);
				singleDictionaryElement.fileName = line.split(",")[1];
				singleDictionaryElement.userId = line.split(",")[2];
				orderedData.add(singleDictionaryElement);
				singleDictionaryElement = new Dictionary();

			}
			size = files.size();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void printHeader() {
		System.out.println("Audit Report");
		System.out.println("============");
	}

	private void printUserHeader(String userName) {
		System.out.println("## User: " + userName);
	}

	private void generateGeneralCSV() {
		String filePath = System.getProperty("user.dir");
		try {
			FileWriter writer = new FileWriter(filePath + outputFile);
			for (int i = 0; i < csvData.size(); i++) {
				writer.append(csvData.get(i));
				writer.append('\n');

			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateTopCSV(int top) {
		String filePath = System.getProperty("user.dir");
        top=top>orderedData.size()?orderedData.size():top;
		try {
			FileWriter writer = new FileWriter(filePath + outputFile);
			for (int i = orderedData.size() - 1; i >= orderedData.size() - top; i--) {
				for (List<String> userRow : users) {
					long userId = Long.parseLong(userRow.get(0));
					String userName = userRow.get(1);

					if (orderedData.get(i).userId.equals(String.valueOf(userId))) {
						writer.append(orderedData.get(i).fileName + "," + userName + ","
								+ String.valueOf(orderedData.get(i).fileSize));
						writer.append('\n');
					}
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printFile(String fileName, long fileSize) {
		System.out.println("* " + fileName + " ==> " + fileSize + " bytes");
	}

}
