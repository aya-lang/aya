package aya.util;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class AWTKeyDecoder {

	private static HashMap<Integer, String> namedKeys;
	private static HashMap<Integer, String> namedLocations;

	public static String findNamedKey(int keyCode) {
		if (namedKeys == null) {
			namedKeys = resolvePublicStaticIntegerFields("VK_");
		}
		return namedKeys.getOrDefault(keyCode, null);
	}

	public static String findLocationName(int locationCode) {
		if (namedLocations == null) {
			namedLocations = resolvePublicStaticIntegerFields("KEY_LOCATION_");
		}
		return namedLocations.getOrDefault(locationCode, null);
	}

	private static HashMap<Integer, String> resolvePublicStaticIntegerFields(String fieldNamePrefix) {
		HashMap<Integer, String> result = new HashMap<>();
		for (Field declaredField : KeyEvent.class.getDeclaredFields()) {
			int modifiers = declaredField.getModifiers();
			if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
				continue;
			}

			if (!int.class.isAssignableFrom(declaredField.getType())) {
				continue;
			}

			String fieldName = declaredField.getName();
			if (!fieldName.startsWith(fieldNamePrefix)) {
				continue;
			}

			try {
				result.put(declaredField.getInt(null), fieldName.substring(fieldNamePrefix.length()));
			} catch (IllegalAccessException e) {
				// do nothing
			}
		}
		return result;
	}

}
