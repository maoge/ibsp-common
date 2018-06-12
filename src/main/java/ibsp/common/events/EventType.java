package ibsp.common.events;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import ibsp.common.utils.CONSTS;

public enum EventType {
	
	e61(50061, false, CONSTS.TYPE_CACHE_CLIENT, "cache proxy deployed"),         // 接入机扩容
	e62(50062, false, CONSTS.TYPE_CACHE_CLIENT, "cache proxy undeployed"),       // 接入机缩容
	
	e71(50071, false, CONSTS.TYPE_DB_CLIENT,    "tidb server deployed"),         // TIDB层扩容
	e72(50072, false, CONSTS.TYPE_DB_CLIENT,    "tidb server undeployed");       // TIDB层缩容

	private final int value;
	private final boolean alarm;
	private final String evCmptType;
	private final String info;

	private static final Map<Integer, EventType> map = new HashMap<Integer, EventType>();

	static {
		for (EventType s : EnumSet.allOf(EventType.class)) {
			map.put(s.value, s);
		}
	}

	private EventType(int i, boolean b, String type, String s) {
		value = i;
		alarm = b;
		evCmptType = type;
		info = s;
	}

	public static EventType get(int code) {
		return map.get(code);
	}

	public int getValue() {
		// 得到枚举值代表的字符串。
		return value;
	}

	public boolean isAarm() {
		return alarm;
	}
	
	public String getEvCmptType() {
		return evCmptType;
	}

	public String getInfo() {
		// 得到枚举值代表的字符串。
		return info;
	}

	public boolean equals(EventType e) {
		return this.value == e.value;
	}

}
