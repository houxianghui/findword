package com.blue.findword;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class RoleProcessor {

	static String[] white = new String[] { "老师", "顾问", "平行线", "lee", "助教", "唐小丫" };

	public abstract void process(String filename, String s, ScanObject obj);

	protected void addRole(Role r, ScanObject obj) {
		if (isInner(r.getName())) {// 剔除内部人员
			return;
		}
		try {
			obj.bw.write(r.name);
			obj.bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (r.id != null) {
			if (obj.idMap.get(r.id) == null) {
				obj.idMap.put(r.getId(), new ArrayList<>());
			}
			obj.idMap.get(r.getId()).add(r);
			obj.roles.add(r);
		} else {
			if (obj.map.get(r.getName()) == null) {
				obj.map.put(r.getName(), new ArrayList<>());
			}
			obj.map.get(r.getName()).add(r);
		}

	}

	private static boolean isInner(String name) {
		for (String w : white) {
			if (name.indexOf(w) != -1) {
				return true;
			}
		}
		return false;
	}

	protected String getClass(File f) {
		return f.getName().substring(0, f.getName().length() - 4);
	}

}
