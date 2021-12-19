package myspring.dao;

import myspring.annotation.Component;
import myspring.annotation.Value;

@Component
public class BookDao {

    @Value("springcloud 学习")
    public String name;

    @Value("3")
    public Integer number;
    @Value("标题")
    public String title;



    @Override
    public String toString() {
        return "BookDao{" +
                "name='" + name + '\'' +
                ", number=" + number +
                ", title='" + title + '\'' +
                '}';
    }
}
