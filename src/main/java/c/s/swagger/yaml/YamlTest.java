/**
 * 
 */
package c.s.swagger.yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import c.s.swagger.example.ExampleController;
import c.s.swagger.example.ExampleVo;
import freemarker.template.TemplateException;

/**
 * @author chineshine
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class YamlTest {

	@Autowired
	private FreemarkerService freemarkerService;

	@Autowired
	private SwaggerService swaggerService;

	@Test
	public void test1() throws IOException {
		String pkg = "com.newtouch.one.currency.vo";
		String uri = "definition.ftl";
		List<String> list = swaggerService.getClassnames(pkg);
		String dir = "E:\\project\\newtouch-coin\\git\\newtouchcurrency\\api\\src\\main\\resources\\doc\\api\\definitions\\";
		list.forEach(t -> {
			System.out.println(t);
			try {
				Class<?> clz = Class.forName(t);
				String filename = dir+clz.getSimpleName()+".yml";
				File file = new File(filename);
				if(!file.exists()) {
					file.createNewFile();
				}
				Map<String, Object> map = swaggerService.definition(clz);
//				freemarkerService.parseToString(map, uri);
				freemarkerService.writeToFile(filename, map, uri);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Test
	public void test3() throws IOException, TemplateException {
		Class<?> clz = ExampleVo.class;
		String uri = "definition.ftl";
		Map<String, Object> map = swaggerService.definition(clz);
		freemarkerService.parseToString(map, uri);
	}
	
	@Test
	public void test2() throws IOException {
		String uri = "path.ftl";
		Class<ExampleController> clz = ExampleController.class;
		List<Map<String,Object>> maps = swaggerService.path(clz);
		maps.forEach(m->{
			try {
				freemarkerService.parseToString(m, uri);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		});
	}
}
