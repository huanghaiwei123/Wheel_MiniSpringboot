package web.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 包扫描器：扫描制定包（含子包）下所有.class,加载为class对象
 * 做到按包找类
 */
public class ClassScanner {
    //Class<?>就是某个任意类型的类的类对象，里面装载着这个类的元信息，包含方法，注解，字段等等，是反射的入口
    public static List<Class<?>> scan(String packageName) {
        //定位磁盘目录用"/"格式(com/gdou),拼类名用"."格式(com.gdou),两者不能混用
        String path = packageName.replace('.', '/');
        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            //Thread.currentThread()返回当前线程
            //getContextClassLoader()获取当前线程的上下文类加载器，由线程决定用哪个类加载器，防止当前线程要用的类在别的加载器里，因为jvm的类加载规则默认是双亲委派模型
            //getResources(path)返回当前包的url,可以有一个或多个，因为只有类加载才知道项目文件具体位置
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while(resources.hasMoreElements()) {  //判断是否还有元素
                URL resource = resources.nextElement();  //有的话就下一个
                //传给scanDir的是"."格式的packageName,用于拼类名
                scanDir(new File(resource.toURI()), packageName, classes);
            }
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
        return classes;
    }

//        遍历目录，把所有文件 .class 加载成class对象
    public static void scanDir(File file,String packageName,List<Class<?>> classes) {
//        返回的file一定是目录
        File[] files = file.listFiles();
        if(files==null||files.length==0) {
            return;
        }
        for(File f:files) {
            if(f.isDirectory()) {
//                子目录=子包,拼类名用"."
                scanDir(f,packageName+"."+f.getName(),classes);
            }else if(f.getName().endsWith(".class") && !f.getName().contains("$")) {  //这里只要字节码文件，txt那些都不要,跳过内部类
                //包名+"."+文件名(去掉.class后缀)
                String className = packageName+"."+f.getName().substring(0,f.getName().lastIndexOf("."));
                try {
//                    继续使用当前线程的类加载器,只解析，不初始化
                    Class<?> aClass = Class.forName(className, false, Thread.currentThread().getContextClassLoader());   //把class文件翻译成class对象
                    classes.add(aClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
