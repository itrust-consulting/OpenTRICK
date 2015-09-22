/**
 * 
 */
package lu.itrust.TS.helper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author eomar
 *
 */
public class TestSharingData {

	private static final Map<String, Object> sharingData = new LinkedHashMap<String, Object>();

	/**
	 * @return
	 * @see java.util.Map#size()
	 */
	public static int size() {
		return sharingData.size();
	}

	/**
	 * @return
	 * @see java.util.Map#isEmpty()
	 */
	public static boolean isEmpty() {
		return sharingData.isEmpty();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public static boolean containsKey(Object key) {
		return sharingData.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public static boolean containsValue(Object value) {
		return sharingData.containsValue(value);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	protected static Object get(Object key) {
		return sharingData.get(key);
	}

	public static <T> T get(Object key, Class<T> clazz) {
		try {
			return clazz.cast(get(key));
		} catch (ClassCastException | NullPointerException e) {
			return null;
		}
	}

	public static String getString(Object key) {
		return get(key, String.class);
	}

	public static Integer getInteger(Object key) {
		return get(key, Integer.class);
	}

	public static Double getDouble(Object key) {
		return get(key, Double.class);
	}

	public static Long getLong(Object key) {
		return get(key, Long.class);
	}

	public static Boolean getBoolean(Object key) {
		return get(key, Boolean.class);
	}

	public static Object getObject(Object key) {
		return get(key);
	}

	public static String getStringValue(Object key) {
		Object object = get(key);
		return object == null ? null : String.valueOf(object);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public static Object put(String key, Object value) {
		return sharingData.put(key, value);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public static Object remove(Object key) {
		return sharingData.remove(key);
	}

	/**
	 * @param m
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public static void putAll(Map<? extends String, ? extends Object> m) {
		sharingData.putAll(m);
	}

	/**
	 * @return
	 * @see java.util.Map#keySet()
	 */
	public static Set<String> keySet() {
		return sharingData.keySet();
	}

	/**
	 * @return
	 * @see java.util.Map#values()
	 */
	public static Collection<Object> values() {
		return sharingData.values();
	}

	/**
	 * @return
	 * @see java.util.Map#entrySet()
	 */
	public static Set<Entry<String, Object>> entrySet() {
		return sharingData.entrySet();
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 * @see java.util.Map#getOrDefault(java.lang.Object, java.lang.Object)
	 */
	public static Object getOrDefault(Object key, Object defaultValue) {
		return sharingData.getOrDefault(key, defaultValue);
	}

	/**
	 * @param action
	 * @see java.util.Map#forEach(java.util.function.BiConsumer)
	 */
	public static void forEach(BiConsumer<? super String, ? super Object> action) {
		sharingData.forEach(action);
	}

	/**
	 * @param function
	 * @see java.util.Map#replaceAll(java.util.function.BiFunction)
	 */
	public static void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
		sharingData.replaceAll(function);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	public static Object putIfAbsent(String key, Object value) {
		return sharingData.putIfAbsent(key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#remove(java.lang.Object, java.lang.Object)
	 */
	public static boolean remove(Object key, Object value) {
		return sharingData.remove(key, value);
	}

	/**
	 * @param key
	 * @param oldValue
	 * @param newValue
	 * @return
	 * @see java.util.Map#replace(java.lang.Object, java.lang.Object,
	 *      java.lang.Object)
	 */
	public static boolean replace(String key, Object oldValue, Object newValue) {
		return sharingData.replace(key, oldValue, newValue);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#replace(java.lang.Object, java.lang.Object)
	 */
	public static Object replace(String key, Object value) {
		return sharingData.replace(key, value);
	}

	/**
	 * @param key
	 * @param mappingFunction
	 * @return
	 * @see java.util.Map#computeIfAbsent(java.lang.Object,
	 *      java.util.function.Function)
	 */
	public static Object computeIfAbsent(String key, Function<? super String, ? extends Object> mappingFunction) {
		return sharingData.computeIfAbsent(key, mappingFunction);
	}

	/**
	 * @param key
	 * @param remappingFunction
	 * @return
	 * @see java.util.Map#computeIfPresent(java.lang.Object,
	 *      java.util.function.BiFunction)
	 */
	public static Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		return sharingData.computeIfPresent(key, remappingFunction);
	}

	/**
	 * @param key
	 * @param remappingFunction
	 * @return
	 * @see java.util.Map#compute(java.lang.Object,
	 *      java.util.function.BiFunction)
	 */
	public static Object compute(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		return sharingData.compute(key, remappingFunction);
	}

	/**
	 * @param key
	 * @param value
	 * @param remappingFunction
	 * @return
	 * @see java.util.Map#merge(java.lang.Object, java.lang.Object,
	 *      java.util.function.BiFunction)
	 */
	public static Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return sharingData.merge(key, value, remappingFunction);
	}

}
