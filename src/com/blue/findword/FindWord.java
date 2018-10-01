package com.blue.findword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
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

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
public class FindWord {
	static Map<Long, String> map = new HashMap<>();
	static HSSFCellStyle style;

	public static void main(String[] args) throws Exception{
		File folder = new File("d:/group");
		for(File t:folder.listFiles()) {
			generate(t);
		}
	}
	
	public static void generate(File f) throws Exception{
		if (f.isDirectory()) {
			return;
		}
//		System.out.println("processing "+f.getName());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf8"));
		String s  = null;
		Pattern p = Pattern.compile("title=\"(\\d*.*?(\\d+))\"");
		List<Role> roles = new ArrayList<>();
		while((s=br.readLine())!=null) {
			Matcher m = p.matcher(s);
			if(m.find()) {
				Role r = new Role();
				r.setName(m.group(1));
				r.setId(Long.parseLong(m.group(2)));
				roles.add(r);
				if(map.containsKey(r.getId())) {
					System.out.print("重复学号 ["+r.getName()+"]班级["+getClass(f)+"]");
					System.out.println(" 和 "+map.get(r.getId()));
				}else {
					map.put(r.getId(), r.getName()+"班级["+getClass(f)+"]");
				}
//				System.out.println(m.group(1)+"\t"+m.group(2));
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
