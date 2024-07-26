package org.zcb.common.baseline

/**
 * 对象
 *
 * 1.单例对象
 *  (1) Scala并没有提供Java那样的静态方法或静态成员，但是，可以采用object关键字实现单例对象，具备和Java静态方法同样的功能。
 *  (2) 单例对象的定义和类的定义很相似，明显的区分是，用object关键字，而不是用class关键字。
 *
 * 2. 伴生对象
 *  (1) 在Java中，我们经常需要用到同时包含实例方法和静态方法的类，在Scala中可以通过伴生对象来实现。
 *  (2) 当单例对象与某个类具有相同的名称时，它被称为这个类的“伴生对象”。
 *  (3) 类和它的伴生对象必须存在于同一个文件中，而且可以相互访问私有成员（字段和方法）。
 *  (4) 伴生对象中定义的newPersonId()实际上就实现了Java中静态（static）方法的功能
 *  Scala源代码编译后都会变成JVM字节码，实际上，在编译上面的源代码文件以后，在Scala里面的class和object在Java层面都会被合二为一，class里面的成员成了实例成员，object成员成了static成员。
 *  经过编译后，伴生类Person中的成员和伴生对象Person中的成员都被合并到一起，并且，伴生对象中的方法newPersonId()，成为静态方法。
 *
 * 3. 应用程序对象
 *  每个Scala应用程序都必须从一个对象的main方法开始
 */
object Person {
    private var lastId = 0      //一个人的身份编号
    def newPersonId(): Int = {
        lastId +=1
        lastId
    }
}


class Person {
    // 调用伴生对象中的方法
    private val id: Int = Person.newPersonId()
    private var name: String = ""

    def this(name: String) {
        this()
        this.name = name
    }
    def info(): Unit = {
        printf("The id of %s is %d.\n", this.name, this.id)
    }
}


/**
 * Windows:
 *       cd \Users\chubin.zheng\JavaProjects\bigdata-learnning
 *  编译：scalac src\main\scala\org.zcb.common.baseline\ch4_2_对象.scala
    “反编译”查看字节码 Person.class：
        > javap src.main.scala.org.zcb.common.baseline.Person
        Compiled from "ch4_2_对象.scala"
        public class src.main.scala.org.zcb.common.baseline.Person {
          public static int newPersonId();
          public void info();
          public src.main.scala.org.zcb.common.baseline.Person();
          public src.main.scala.org.zcb.common.baseline.Person(java.lang.String);
        }
 */
object ch4_2_对象 {
    def main(args: Array[String]): Unit = {
        // 单例对象
        printf("The first person id is %d.\n",Person.newPersonId())
        printf("The second person id is %d.\n",Person.newPersonId())
        printf("The third person id is %d.\n",Person.newPersonId())

        // 伴生对象
        val person1 = new Person("HuJiang")
        val person2 = new Person("ZhengChubin")
        person1.info()
        person2.info()
    }
}
