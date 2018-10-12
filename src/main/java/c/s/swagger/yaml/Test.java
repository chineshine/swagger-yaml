/**
 * 
 */
package c.s.swagger.yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import c.s.swagger.example.ExampleVo;
import freemarker.template.TemplateException;

/**
 * @author chineshine
 *
 */

public class Test {

	@org.junit.Test
	public void test1() throws IOException, TemplateException {
		SwaggerService service = new SwaggerService();
//		service.path();

		String pkg = "com.newtouch.one.currency.domain";
		String pkgDir = pkg.replace(".", "/");
		System.out.println(pkgDir);
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		Enumeration<URL> urls =  service.getUrls(pkgDir);
		while(urls.hasMoreElements()) {
			URL url = urls.nextElement();
			 String protocol = url.getProtocol();
			 if ("file".equals(protocol)) {
                 // 获取包的物理路径
                 String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                 // 以文件的方式扫描整个包下的文件 并添加到集合中
                 findAndAddClassesInPackageByFile(pkg, filePath,
                         true, classes);
             } 
		}
		
	
	}
	
	@org.junit.Test
	public void test2() throws ClassNotFoundException {
		Class<?> clz = Class.forName("java.lang.Boolean");
		System.out.println(clz.getSimpleName());
		URL url = Thread.currentThread().getContextClassLoader().getResource("com/newtouch/one/currency/domain");
		System.out.println(url.toString());
	}
	
	@org.junit.Test
	public void test5() throws IOException{
		SwaggerService service = new SwaggerService();
		String pkg = "com.newtouch.one.currency.domain";
		List<String> list = service.getClassnames(pkg);
		list.forEach(t->System.out.println(t));
	}
	
	@org.junit.Test
	public void test3() throws IOException {
		String pkg = "com.newtouch.one.currency.domain";
		String pkgDir = pkg.replaceAll(".", "/");
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		Enumeration<URL> urls =  Thread.currentThread().getContextClassLoader().getResources(pkgDir);
		while(urls.hasMoreElements()) {
			URL url = urls.nextElement();
			 String protocol = url.getProtocol();
			 if ("file".equals(protocol)) {
                 System.err.println("file类型的扫描");
                 // 获取包的物理路径
                 String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                 // 以文件的方式扫描整个包下的文件 并添加到集合中
                 findAndAddClassesInPackageByFile(pkg, filePath,
                         true, classes);
             } 
		}
		
	}
	
	public static void findAndAddClassesInPackageByFile(String packageName,
            String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                        + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                                         //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));  
                                        System.out.println(packageName + '.' + className);
                                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }
	
	
	@org.junit.Test
	public void test6() throws NoSuchMethodException, SecurityException {
		Class<ExampleVo> clz = ExampleVo.class;
		Method m = clz.getMethod("page", ExampleVo.class,Pageable.class);
		Type type = ReflectHelper.responseType(m);
		Type type1 = ReflectHelper.types(type)[0];
		System.out.println(type.getTypeName()+"--------"+type1.getTypeName());
	}
	
	@org.junit.Test
	public void test7() throws ClassNotFoundException {
		Class<?> a = String.class;
		System.out.println(String.class ==a);
		System.out.println(Class.forName("org.springframework.data.domain.Page"));
	}
	
	@org.junit.Test
	public void test8() throws IOException {
		Runtime runtime = Runtime.getRuntime();
//		String[] args = {"-J-version"};
//		String[] cmd = {"javadoc", "-help"};
//		String[] cmd = {"ipconfig","-all"};
		String[] cmd  = {"javadoc", "-d", "docs",
                "-sourcepath", "E:\\project\\newtouch-coin\\git\\newtouchcurrency\\currency\\src",
                "com.newtouch.one.currency.vo" };
		Process process = runtime.exec(cmd);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
		String line = null;   
		 while ((line = bufferedReader.readLine()) != null) {   
			 System.out.println(line);
		 }
		 bufferedReader.close();
	}
}
