package com.dts.tasktracker.web.view;

import com.dts.tasktracker.entity.TaskStatus;

public final class ViewUtils {

	public static String prettyStatus(TaskStatus status) {
		if (status == null) {
			return "";
		}
		String s = status.name().toLowerCase().replace('_', ' ');
		String[] parts = s.split(" ");
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isEmpty()) continue;
			String p = parts[i];
			b.append(Character.toUpperCase(p.charAt(0)));
			if (p.length() > 1) {
				b.append(p.substring(1));
			}
			if (i < parts.length - 1) {
				b.append(' ');
			}
		}
		return b.toString();
	}

	private ViewUtils() {}
}
