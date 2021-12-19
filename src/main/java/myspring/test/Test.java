package myspring.test;

import myspring.config.AnnotationConfigApplicationContext;
import myspring.config.ApplicationContext;
import myspring.dao.BookDao;
import myspring.service.BookDaoService;


public class Test {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("myspring");
        BookDaoService bookDaoService = (BookDaoService) applicationContext.getBean("bookDaoService");
        bookDaoService.test();

    }
}
