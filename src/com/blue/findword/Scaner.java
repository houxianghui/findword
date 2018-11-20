package com.blue.findword;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Scaner {
	RoleProcessor processor;
	static HSSFCellStyle style;
	public void scan() throws Exception {
		File folder = new File("d:/group");
		ScanObject obj = new ScanObject();
		obj.map = new ConcurrentHashMap<>();// 姓名重复
		obj.idMap = new ConcurrentHashMap<>();// 姓名ID

		for (File t : folder.listFiles()) {
			if (t.isDirectory()) {
				continue;
			}
			doScan(t, obj);
		}
		obj.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/重复名单.txt"), "utf8"));
		displayDupOfHasId(obj);
		displayDupOfOnlyName(obj);
		obj.bw.close();
	}

	private void displayDupOfHasId(ScanObject obj) {
		obj.idMap.entrySet().stream().filter(t -> t.getValue().size() > 1).forEach(t -> {
			StringBuilder sb = new StringBuilder("学号：" + t.getKey() + " 重复[班级_姓名]有：");
			t.getValue().forEach(x -> {
				sb.append("[" + x + "]");
			});
			sb.append("\r\n");
			System.out.println(sb);
			try {
				obj.bw.write(sb.toString());
				obj.bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

	}

	private void displayDupOfOnlyName(ScanObject obj) {
		obj.map.entrySet().stream().filter(t -> t.getValue().size() > 1).forEach(t -> {
			StringBuilder sb = new StringBuilder("姓名：" + t.getKey() + " 重复[班级_姓名]有：");

			t.getValue().forEach(x -> {
				sb.append("[" + x + "]");
			});
			sb.append("\r\n");
			System.out.println(sb);
			try {
				obj.bw.write(sb.toString());
				obj.bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void doScan(File f, ScanObject obj) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
		String s = null;
		obj.roles = new ArrayList<>();
		obj.bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("d:/target/全部名单/" + f.getName() + "_所有名单.txt"), "utf8"));

		while ((s = br.readLine()) != null) {
			final String t = s;
			processor.process(f.getName(), t, obj);
		}
		br.close();
		obj.bw.close();
		Collections.sort(obj.roles, new Comparator<Role>() {
			@Override
			public int compare(Role o1, Role o2) {
				return Long.compare(o1.id, o2.id);
			}
		});
		writeExcel(obj.roles, f);
	}

	private void writeExcel(List<Role> roles, File f) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		style = createCellStyle(wb);
		addHead(sheet);
		final AtomicInteger index = new AtomicInteger(1);
		roles.forEach(t -> {
			try {
				int colIndex = 0;
				Row r = sheet.createRow(index.get());
				addCell(r, colIndex++, index.getAndIncrement());
				addCell(r, colIndex++, t.getName());
				addCell(r, colIndex++, t.getId());
				for (int i = 0; i < 7; i++) {
					addCell(r, colIndex++, "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		for (int i = 0; i < 8; i++) {
			sheet.autoSizeColumn(i);
		}
		FileOutputStream fos = new FileOutputStream(
				"d:/target/" + f.getName().substring(0, f.getName().length() - 4) + ".xls");
		wb.write(fos);
		wb.close();
		fos.close();
	}

	public void setProcessor(RoleProcessor processor) {
		this.processor = processor;
	}

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


	private static void addHead(Sheet sheet) throws Exception {
		int from = 0;
		Row row = sheet.createRow(0);
		addCell(row, from++, "序号");
		addCell(row, from++, "姓名");
		addCell(row, from++, "学号");

		Date date = new Date();
		int day = 0;
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
		addCell(row, from++, df.format(DateUtils.addDays(date, day++)));
	}

	public static HSSFCellStyle createCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();

		// 设置上下左右四个边框宽度

		style.setBorderTop(BorderStyle.THIN);

		style.setBorderBottom(BorderStyle.THIN);

		style.setBorderLeft(BorderStyle.THIN);

		style.setBorderRight(BorderStyle.THIN);

		return style;

	}

	private static void addCell(Row r, int i, String value) {
		Cell cell = r.createCell(i);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}

	private static void addCell(Row r, int i, int value) {
		Cell cell = r.createCell(i);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}

	private static void addCell(Row r, int i, long value) {
		Cell cell = r.createCell(i);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}
}
