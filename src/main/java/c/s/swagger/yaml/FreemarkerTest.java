/**
 * 
 */
package c.s.swagger.yaml;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import c.s.swagger.example.ExampleController;
import c.s.swagger.example.ExampleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author chineshine
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FreemarkerTest {

	@Autowired
	private Configuration configuration;

	@Test
	public void definition() throws IOException, TemplateException {
		Class<ExampleVo> clz = ExampleVo.class;
		Map<String, Object> map = new HashMap<>();
		map.put("voClassName", clz.getSimpleName());
		Field[] fields = clz.getDeclaredFields();
		List<DefinitionField> list = new ArrayList<>();
		for (Field f : fields) {
			DefinitionField definitionField = new DefinitionField();
			definitionField.setName(f.getName());
			Class<?> type = f.getType();
			String typeName = type.getSimpleName();
			definitionField.setType(typeName);
			if (Number.class.isAssignableFrom(type)) {
				definitionField.setHasRef(false);
			} else if (typeName.equals("String") || typeName.equals("Boolean")) {
				definitionField.setHasRef(false);
			} else {
				definitionField.setHasRef(true);
				definitionField.setRefClassName(typeName);
			}
			list.add(definitionField);
		}
		map.put("fields", list);
		this.process(map, "definition.ftl");
	}

	@Test
	public void path() throws IOException, TemplateException {
		Class<ExampleController> clz = ExampleController.class;
		String uri = "";
		if (clz.isAnnotationPresent(RequestMapping.class)) {
			RequestMapping mapping = clz.getAnnotation(RequestMapping.class);
			uri = mapping.value()[0];
		}

		Method[] methods = clz.getDeclaredMethods();
		for (Method m : methods) {
			Map<String, Object> map = new HashMap<>();
			if (m.isAnnotationPresent(GetMapping.class)) {
				GetMapping get = m.getAnnotation(GetMapping.class);
				if (get.value() != null) {
					map.put("uri", uri + get.value()[0]);
				} else {
					map.put("uri", uri);
				}
				map.put("method", "get");
			} else if (m.isAnnotationPresent(PostMapping.class)) {
				PostMapping post = m.getAnnotation(PostMapping.class);
				if (post.value() != null && post.value().length > 0) {
					map.put("uri", uri + post.value()[0]);
				} else {
					map.put("uri", uri);
				}
				map.put("method", "post");
			} else if (m.isAnnotationPresent(DeleteMapping.class)) {
				DeleteMapping delete = m.getAnnotation(DeleteMapping.class);
				if (delete.value() != null) {
					map.put("uri", uri + delete.value()[0]);
				} else {
					map.put("uri", uri);
				}
				map.put("method", "delete");
			} else if (m.isAnnotationPresent(PutMapping.class)) {
				PutMapping put = m.getAnnotation(PutMapping.class);
				if (put.value() != null) {
					map.put("uri", uri + put.value()[0]);
				} else {
					map.put("uri", uri);
				}
				map.put("method", "put");
			} else {
				continue;
			}
			List<PathField> fields = new ArrayList<>();
			Parameter[] parameters = m.getParameters();
			Boolean isPageabled = false;
			for (Parameter p : parameters) {
				String name = p.getType().getSimpleName();
				if (("Pageable").equals(name)) {
					isPageabled = true;
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
			map.put("isPageabled", isPageabled);
			map.put("fields", fields);
			System.out.println(map);
			this.process(map, "path.ftl");
		}
	}

	public void process(Map<String, Object> map, String uri) throws IOException, TemplateException {
		Template template = configuration.getTemplate(uri);
		String str = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
		System.out.println(str);
	}

}
