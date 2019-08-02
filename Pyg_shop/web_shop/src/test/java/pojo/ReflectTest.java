package pojo;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Comparator;

public class ReflectTest {

    @Test
    public void demo1() throws Exception {
        Class<?> personClass = ReflectTest.class.getClassLoader().loadClass("pojo.Person");
        Person person = (Person) personClass.newInstance();
        System.out.println(person);
    }

    @Test
    public void demo2() {
        //lambda
        //new Thread(()-> System.out.println("我在吃饭， 你随便！！！")).start();
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("DLF1" , 100));
        people.add(new Person("DLF2" , 29));
        people.add(new Person("DLF3" , 32));
        people.add(new Person("DLF4" , 13));
        people.sort((Person p1 , Person p2)->{
            if(!p1.getName().equals(p2.getName())){
                return p1.getAge() - p2.getAge();
            }
            return p1.getName().compareTo(p2.getName());
        });

        System.out.println(people);

    }
}
