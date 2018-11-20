package com.blue.findword;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdRoleProcessor extends RoleProcessor {

	Pattern p1 = Pattern.compile("title=\"(.*?(\\d+))\"");
	Pattern p2 = Pattern.compile("title=\"((\\d+).*?)\"");
	Pattern p3 = Pattern.compile("title=\"(.+?)\"");
	@Override
	public void process(String filename, String s, ScanObject obj) {
		Matcher m = p1.matcher(s);
		if (m.find()) {
			Role r = new Role();
			r.setClazz(filename);
			r.setName(m.group(1));
			r.setId(Long.parseLong(m.group(2)));
			addRole(r, obj);
			return;
		}
		Matcher m2 = p2.matcher(s);
		if (m2.find()) {
			Role r = new Role();
			r.setClazz(filename);
			r.setName(m2.group(1));
			r.setId(Long.parseLong(m2.group(2)));
			addRole(r, obj);
			return;
		}
		Matcher m3 = p3.matcher(s);
		if (m3.find()) {
			Role r = new Role();
			r.setClazz(filename);
			r.setName(m3.group(1));
			addRole(r, obj);
			return;
		}

	}

}
