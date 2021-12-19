package myspring.config;


import myspring.annotation.Autowired;
import myspring.annotation.Component;
import myspring.annotation.Value;
import org.reflections.Reflections;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private static HashMap<String, Object> ioc = new HashMap<String, Object>();

    private static HashMap<Class,String[]> beanNameByType = new HashMap<Class, String[]>();

    public AnnotationConfigApplicationContext(String pack) {
        //查注解了指定类
        Set<BeanDefinition> beanDefinitions = findBeanDefinitions(pack);
        //创建IOC,@value的注入
        createObject(beanDefinitions);
        //注入
        autowireObject(beanDefinitions);
    }

    private Set<BeanDefinition> findBeanDefinitions(String pack) {
        Set<BeanDefinition> beanDefinitions = new HashSet<BeanDefinition>();
        Reflections f = new Reflections(pack);
        //入参 目标注解类
        Set<Class<?>> set = f.getTypesAnnotatedWith(Component.class);
        for (Class<?> aClass : set) {
            Component componentAnnotation = aClass.getAnnotation(Component.class);
            String beanName = componentAnnotation.value();
            if ("".equals(beanName)) {
                String classSimpleName = aClass.getSimpleName();
                beanName = classSimpleName.substring(0, 1).toLowerCase() + classSimpleName.substring(1, classSimpleName.length());
            }

            beanDefinitions.add(new BeanDefinition(beanName, aClass));
        }
        return beanDefinitions;
    }

    private void createObject(Set<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class clazz = beanDefinition.getaClass();
            try {
                Object objet = clazz.getConstructor().newInstance();
                //设置字段的注解
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    Value valueAnnotation = declaredField.getAnnotation(Value.class);
                    if (valueAnnotation != null) {
                        //类型转换
                        String typeName = declaredField.getType().getSimpleName();
                        Object value = convertTheType(typeName, valueAnnotation.value());
                        declaredField.set(objet, value);
                    }
                }
                ioc.put(beanDefinition.getBeanName(), objet);
                //单例只创建一个对象
                beanNameByType.put(beanDefinition.getaClass(),new String[]{beanDefinition.getBeanName()});

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }


    private void autowireObject(Set<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class aClass = beanDefinition.aClass;
            try {
                Object object = getBean(beanDefinition.beanName);
                //获取字段类型
                Field[] fields = aClass.getDeclaredFields();
                for (Field field : fields) {
                    Autowired filedAnnotation = field.getAnnotation(Autowired.class);
                    if (filedAnnotation != null) {
                        //根据类型查找
                        Object filedObject = getBean(field.getType());
                        field.setAccessible(true);
                        field.set(object,filedObject);
                    }
                }


            }catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getBean(String beanName) {
        Object object = ioc.get(beanName);
        assertIsNotNull(object,"找不到bean");
        return object;
    }

    public Object getBean(Class clazz) {
        String[] beanNames = beanNameByType.get(clazz);
        int sort = beanNames.length;
        //没有报错
        assertIsTrue(sort!=0,"找不到bean");
        String beanName ;
        //如果只有一个，直接装载
        if(sort ==1){
            beanName = beanNames[0];
        }else {
            //多个，按名称查找,多例模式可能使用，这边暂时未使用
            beanName = clazz.getName();
        }
        Object object = getBean(beanName);
        assertIsNotNull(object,"找不到bean");
        return object;
    }



    //类型转换

    /**
     * 基本类型转行
     *
     * @param typeName 转换类型
     * @param value    值
     * @return
     */
    private Object convertTheType(String typeName, String value) {
        Object val = null;
        if ("String".equals(typeName)) {
            val = value;
        } else if ("Integer".equals(typeName)) {
            val = Integer.parseInt(value);
        }
        return val;
    }

    //如果是空，报错
    private void assertIsNotNull(Object object,String message){
        if(object == null){
            throw  new RuntimeException(message);
        }
    }
    //如果是false报错
    private void assertIsTrue(boolean flag,String message){
        if(!flag){
            throw  new RuntimeException(message);
        }
    }
}
