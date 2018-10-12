/**
 * 
 */
package c.s.swagger.yaml;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.TemplateException;

/**
 * @author chineshine
 *
 */
@Service("swaggerService")
public class SwaggerService {

	private static final String SPRING_PAGE_CLASS = "org.springframework.data.domain.Page";

	// TODO 获取 map list 中的类型
	public Map<String, Object> definition(Class<?> clz) throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<>();
		map.put("classname", clz.getSimpleName());
		Field[] fields = clz.getDeclaredFields();
		List<DefinitionField> list = new ArrayList<>();
		for (Field f : fields) {
			Class<?> type = f.getType();
			if(isMap(type)) {
				// 既然已经用 vo 接受数据,不建议在vo 中写 map 故直接过滤,建议使用对象
				continue;
			}
			DefinitionField definitionField = new DefinitionField();
			definitionField.setName(f.getName());
			String typeName = this.getTypeName(type);
			definitionField.setType(typeName);
			if(isList(type)) {
				Type refType = ReflectHelper.fieldGenericType(f)[0];
				// TODO 类型转换异常直接为 false vo 字段不支持泛型再套接泛型
				Class<?> rtype = (Class<?>) refType;
				String rtypeName = getTypeName(rtype);
				if(("object").equals(rtypeName)) {
					definitionField.setHasRef(true);
					definitionField.setRefClassName(rtype.getSimpleName());
				}
			}
			list.add(definitionField);
		}
		map.put("fields", list);
		return map;
	}

	public Boolean isSimpleType(Class<?> type) {
		if (String.class == type) {
			return true;
		}
		if (Integer.class == type) {
			return true;
		}
		if (Boolean.class == type) {
			return true;
		}
		return false;
	}

	public Boolean isList(Class<?> type) {
		return List.class == type;
	}
	
	public Boolean isMap(Class<?> type) {
		return Map.class == type;
	}

	public Boolean isNumber(Class<?> type) {
		return Integer.class != type && Number.class.isAssignableFrom(type);
	}
	
	public String getTypeName(Class<?> type) {
		if(CharSequence.class.isAssignableFrom(type)) {
			return "string";
		}
		if (Temporal.class.isAssignableFrom(type)) {
			return "string";
		}
		if(isSimpleType(type)) {
			return type.getSimpleName().toLowerCase();
		}
		if(isList(type)) {
			return "array";
		}
		if(Integer.class==type) {
			return "integer";
		}
		if(isNumber(type)) {
			return "number";
		}
		return "object";
	}

	public List<Map<String, Object>> path(Class<?> clz) throws IOException {
		String uri = this.getClassRequestMappingUri(clz);
		// 获取 controller 中的方法
		List<Map<String, Object>> maps = new ArrayList<>();
		Method[] methods = clz.getDeclaredMethods();
		for (Method m : methods) {
			String uri1 = "";
			String method = "";
			if (m.isAnnotationPresent(GetMapping.class)) {
				GetMapping get = m.getAnnotation(GetMapping.class);
				uri1 = this.value(get.value());
				method = "get";
			} else if (m.isAnnotationPresent(PostMapping.class)) {
				PostMapping post = m.getAnnotation(PostMapping.class);
				uri1 = this.value(post.value());
				method = "post";
			} else if (m.isAnnotationPresent(DeleteMapping.class)) {
				DeleteMapping delete = m.getAnnotation(DeleteMapping.class);
				uri1 = this.value(delete.value());
				method = "delete";
			} else if (m.isAnnotationPresent(PutMapping.class)) {
				PutMapping put = m.getAnnotation(PutMapping.class);
				uri1 = this.value(put.value());
				method = "put";
			} else {
				continue;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("uri", uri + uri1);
			map.put("method", method);
			// 传参处理
			Parameter[] parameters = m.getParameters();
			if (CollectionUtils.sizeIsEmpty(parameters)) {
				map.put("hasParameters", false);
			} else {
				map.put("hasParameters", true);
				List<PathField> fields = this.getFields(parameters);
				map.put("fields", fields);
			}
			String responseName = "";
			// 返回类型
			Type responseType = ReflectHelper.responseType(m);
			Boolean isPageabled = false;
			Boolean isList = false;
			String responseTypeName = responseType.getTypeName();
			if (responseTypeName.indexOf(SPRING_PAGE_CLASS) >= 0) {
				isPageabled = true;
			} else if (responseTypeName.indexOf("java.util.List") >= 0) {
				isList = true;
			} else {
				// isObject TODO 排除 map
				// Class<?> c =
			}
			if (isPageabled || isList) {
				// 嵌套 泛型
				Type type2 = ReflectHelper.types(responseType)[0];
				responseName = ((Class<?>) type2).getSimpleName();
			} else {
				responseName = ((Class<?>) responseType).getSimpleName();
			}
			map.put("isList", isList);
			map.put("responseType", responseName);
			map.put("isPageabled", isPageabled);
			// System.out.println(map);
			maps.add(map);
		}
		return maps;
	}

	public List<PathField> getFields(Parameter[] parameters) {
		List<PathField> fields = new ArrayList<>();
		for (Parameter p : parameters) {
			String name = p.getType().getSimpleName();
			if (this.isFilter(name)) {
				continue;
			}
			PathField field = new PathField();
			field.setName(p.getName());
			field.setType(name);
			if (p.isAnnotationPresent(RequestBody.class)) {
				field.setRequired(true);
				field.setIn("body");
			} else if (p.isAnnotationPresent(PathVariable.class)) {
				field.setRequired(true);
				field.setIn("path");
			} else {
				field.setRequired(false);
				field.setIn("query");
			}
			fields.add(field);
		}
		return fields;
	}


	/**
	 * 是否引用对象,有就返回对象名称
	 * 
	 * @param typeName
	 * @return
	 */

	public List<String> getClassnames(String pkg) throws IOException {
		List<String> classnames = new ArrayList<>();
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		Enumeration<URL> urls = this.getUrlsFromPackage(pkg);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				// 获取包的物理路径
				String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				// 以文件的方式扫描整个包下的文件 并添加到集合中
				classnames.addAll(getAllClassnames(pkg, filePath, true, classes));
			}
		}
		return classnames;

	}

	public static List<String> getAllClassnames(String packageName, String packagePath, final boolean recursive,
			Set<Class<?>> classes) {
		List<String> classnames = new ArrayList<>();
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return Collections.emptyList();
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				classnames.addAll(getAllClassnames(packageName + "." + file.getName(), file.getAbsolutePath(),
						recursive, classes));
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				System.out.println(packageName + '.' + className);
				classnames.add(packageName + '.' + className);
			}
		}
		return classnames;
	}

	public Enumeration<URL> getUrlsFromPackage(String pkg) throws IOException {
		pkg = pkg.replace(".", "/");
		return this.getUrls(pkg);
	}

	public Enumeration<URL> getUrls(String pkgDir) throws IOException {
		// Thread.currentThread().getContextClassLoader().getResources("com/newtouch/one/currency/domain");
		return Thread.currentThread().getContextClassLoader().getResources(pkgDir);
	}

	/**
	 * 参数过滤
	 * 
	 * @param name
	 * @return
	 */
	private Boolean isFilter(String name) {
		if (("Pageable").equals(name)) {
			return true;
		} else if (("Principal").equals(name)) {
			return true;
		} else if (("HttpServletRequest").equals(name)) {
			return true;
		}
		return false;
	}

	private String getClassRequestMappingUri(Class<?> clz) {
		if (clz.isAnnotationPresent(RequestMapping.class)) {
			RequestMapping mapping = clz.getAnnotation(RequestMapping.class);
			return this.value(mapping.value());
		}
		return "";
	}

	private String value(String[] value) {
		if (value != null && value.length > 0) {
			return value[0];
		}
		return "";
	}
}
