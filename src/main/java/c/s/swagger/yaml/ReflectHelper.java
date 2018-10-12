/**
 * 
 */
package c.s.swagger.yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author chineshine
 *
 */
public class ReflectHelper {

	private ReflectHelper() {
	}

	/**
	 * 返回类型的泛型
	 * 
	 * @param m
	 * @return
	 */
	public static final Type responseType(Method m) {
		Type t = m.getGenericReturnType();
		Type[] genericTypes = types(t);
		return CollectionUtils.sizeIsEmpty(genericTypes) ? null : genericTypes[0];
	}

	/**
	 * 传入参数的泛型
	 * 
	 * @param p
	 * @return
	 */
	@Deprecated
	public static final List<String> parameterType(Parameter p) {
		List<String> list = new ArrayList<>();
		Type t = p.getType();
		Type[] genericTypes = types(t);
		if (!CollectionUtils.sizeIsEmpty(genericTypes)) {
			for (Type type : genericTypes) {
				list.add(type.getTypeName());
			}
		}
		return list;
	}


	public static final Type[] fieldGenericType(Field f) {
		Type type = f.getGenericType();
		Type[] genericTypes = types(type);
		return CollectionUtils.sizeIsEmpty(genericTypes) ? null : genericTypes;
	}
	
	/**
	 * 泛型解析
	 * 
	 * @param type
	 * @return
	 */
	public static final Type[] types(Type type) {
		if (type instanceof ParameterizedType) {
			Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
			return genericTypes;
		}
		return new Type[] {};
	}

	/**
	 * 是不是常用类的类型
	 * 
	 * @param type
	 * @return
	 */
	public static final Boolean isCommonClass(Class<?> type) {
		// byte int short long float double
		if (Number.class.isAssignableFrom(type)) {
			return true;
		}
		// string stringbuffer stringbuilder
		if (CharSequence.class.isAssignableFrom(type)) {
			return true;
		}
		// localdate localtime localdatetime
		if (Temporal.class.isAssignableFrom(type)) {
			return true;
		}
		if(Boolean.class == type) {
			return true;
		}
		return false;
	}
}
