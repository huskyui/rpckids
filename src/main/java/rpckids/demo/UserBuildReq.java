package rpckids.demo;

/**
 * @author huskyui
 * @date 2019/11/28 14:09
 */

public class UserBuildReq {
    private String name;
    private Integer age;

    public UserBuildReq() {
    }

    public UserBuildReq(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
