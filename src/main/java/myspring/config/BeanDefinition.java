package myspring.config;

public class BeanDefinition {

    public String beanName;

    public  Class aClass;

    public BeanDefinition(String beanName, Class aClass) {
        this.beanName = beanName;
        this.aClass = aClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
