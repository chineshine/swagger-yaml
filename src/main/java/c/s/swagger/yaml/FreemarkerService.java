/**
 * 
 */
package c.s.swagger.yaml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author chineshine
 *
 */
@Service("freemarkerService")
public class FreemarkerService {

	@Autowired
	private Configuration configuration;

	public String parseToString(Map<String, Object> map, String uri) throws IOException, TemplateException {
		Template template = configuration.getTemplate(uri);
		String str = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
		System.out.println(str);
		return str;
	}

	public void writeToFile(String filePath, Map<String, Object> map, String template) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			String content = this.parseToString(map, template);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			
		}
	}

}
