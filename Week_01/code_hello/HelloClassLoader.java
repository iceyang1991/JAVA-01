package demo.jone;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.concurrent.Callable;

/**
 * @create 2021-01-10 下午1:47
 */
public class HelloClassLoader extends ClassLoader{
    
    
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object obj = new HelloClassLoader().findClass("Hello").newInstance();
        Method method = obj.getClass().getMethod("hello");
        method.invoke(obj);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        ///Users/pikachu/personwork/02.极客大学/课程资料/第一课作业/Hello/Hello.xlass
        File file = new File("/Users/pikachu/personwork/02.极客大学/课程资料/第一课作业/Hello/Hello.xlass");

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] originFileBytes = new byte[0];
        try {
            originFileBytes = new byte[in.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.read(originFileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] decodeFileBytes = new byte[originFileBytes.length];
        System.out.println(originFileBytes.length);
        for(int index=0;index<originFileBytes.length; index++){
            decodeFileBytes[index]=(byte)((byte)255-(originFileBytes[index]));
        }
        return defineClass(name,decodeFileBytes,0,decodeFileBytes.length);
    }
}