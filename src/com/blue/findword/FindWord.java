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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
public class FindWord {
	static Map<Long, List<Role>> map = new HashMap<>();
	static Map<String, List<Role>> dupNameMap = new HashMap<>();
	static HSSFCellStyle style;
	static String[] white = new String[] { "老师", "顾问", "平行线" };
	public static void main(String[] args) throws Exception{
		boolean findAll = System.getProperty("findAll") != null;
		File folder = new File("d:/group");
		for(File t:folder.listFiles()) {
			generate(t);
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/重复名单.txt"), "utf-8"));
		BufferedWriter allWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("d:/全部名单.txt"), "utf-8"));
		System.out.println("-----------------重复学号---------------------");
		bw.write("-----------------重复学号---------------------");
		bw.newLine();
		for (Long l : map.keySet()) {
			if (map.get(l).size() > 1) {
				map.get(l).forEach(t -> {
					System.out.print("[" + t + "]");
					try {
						bw.write("[" + t + "]");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} else {
				continue;
			}
			System.out.println();
			bw.newLine();
		}
		System.out.println("-----------------重复姓名---------------------");
		bw.write("-----------------重复姓名---------------------");
		bw.newLine();
		for (String name : dupNameMap.keySet()) {
			if (isInner(name)) {
				continue;
			}
			if (findAll) {
				dupNameMap.get(name).stream().forEach(t -> {
					System.out.println(t);
					try {
						allWriter.write(t.toString());
						allWriter.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} else {

				if (dupNameMap.get(name).size() > 1) {
					dupNameMap.get(name).forEach(t -> {
						System.out.print("[" + t + "]");
						try {
							bw.write("[" + t + "]");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} else {
					continue;
				}
				System.out.println();
				bw.newLine();
			}
		}
		bw.close();
		allWriter.close();
	}

	private static boolean isInner(String name) {
		for (String w : white) {
			if (name.indexOf(w) != -1) {
				return true;
			}
		}
		return false;
	}
	
	public static void generate(File f) throws Exception{
		if (f.isDirectory()) {
			return;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf8"));
		String s  = null;
		Pattern p = Pattern.compile("title=\"(\\d*.*?(\\d+))\"");
		Pattern p2 = Pattern.compile("title=\"(.+?)\"");
		List<Role> roles = new ArrayList<>();
		while((s=br.readLine())!=null) {
			boolean hasId = false;
			Matcher m = p.matcher(s);
			if(m.find()) {

				Role r = new Role();
				r.setName(m.group(1));
				r.setId(Long.parseLong(m.group(2)));
				if (r.getId() > 5) {
					hasId = true;
					r.setClazz(getClass(f));
					roles.add(r);

					if (!map.containsKey(r.getId())) {
						map.put(r.getId(), new ArrayList<>());
					}
					map.get(r.getId()).add(r);
				}

			}
			if (hasId) {
				continue;
			}
			Matcher m2 = p2.matcher(s);
			if (m2.find()) {
				Role r = new Role();
				r.setName(m2.group(1));
				if (StringUtils.isBlank(r.getName())) {
					continue;
				}
				r.setClazz(getClass(f));
				if (!dupNameMap.containsKey(r.getName())) {
					dupNameMap.put(r.getName(), new ArrayList<>());
				}
				dupNameMap.get(r.getName()).add(r);
			}
		}
		br.close();
		Collections.sort(roles, new Comparator<Role>() {
			@Override
			public int compare(Role o1, Role o2) {
				return Long.compare(o1.id ,o2.id);
			}
		});
		HSSFWorkbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		style = createCellStyle(wb);
		addHead(sheet);
		final AtomicInteger index = new AtomicInteger(1);
		roles.forEach(t->{
			try {
				int colIndex = 0;
				Row r = sheet.createRow(index.get());
				addCell(r, colIndex++, index.getAndIncrement());
				addCell(r,colIndex++,t.getName());
				addCell(r,colIndex++,t.getId());
				for (int i = 0; i < 7; i++) {
					addCell(r, colIndex++, "");
				}
//				bw.write(t.toString());
//				bw.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		for (int i = 0; i < 8; i++) {
			sheet.autoSizeColumn(i);
		}
		FileOutputStream fos = new FileOutputStream("d:/target/"+f.getName().substring(0, f.getName().length()-4)+".xls");
		wb.write(fos);
		wb.close();
		fos.close();

	}

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private static String getClass(File f) {
		return f.getName().substring(0, f.getName().length()-4);
	}
	private static void addHead(Sheet sheet)throws Exception {
		int from = 0;
		Row row = sheet.createRow(0);
		addCell(row,from++,"序号");
		addCell(row,from++,"姓名");
		addCell(row,from++,"学号");
		
		Date date = new Date();
		int day = 0;
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
		addCell(row,from++,df.format(DateUtils.addDays(date, day++)));
	}

	public static HSSFCellStyle createCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();

        //设置上下左右四个边框宽度

		style.setBorderTop(BorderStyle.THIN);

		style.setBorderBottom(BorderStyle.THIN);

		style.setBorderLeft(BorderStyle.THIN);

		style.setBorderRight(BorderStyle.THIN);

       

        return style;

  }
	private static void addCell(Row r,int i,String value) {
		Cell cell = r.createCell(i);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}
	private static void addCell(Row r,int i,int value) {
		Cell cell = r.createCell(i);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}
	private static void addCell(Row r,int i,long value) {
		Cell cell = r.createCell(i);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}
}
