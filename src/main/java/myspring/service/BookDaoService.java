package myspring.service;

import myspring.annotation.Autowired;
import myspring.annotation.Component;
import myspring.dao.BookDao;

@Component
public class BookDaoService {

    @Autowired
    private BookDao bookDao;

    public void test(){
        System.out.println(bookDao.toString());
    }
}
