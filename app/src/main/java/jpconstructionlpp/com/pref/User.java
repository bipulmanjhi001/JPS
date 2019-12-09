package jpconstructionlpp.com.pref;

public class User {
    private String id,branch_id,name;

    public User(String id, String branch_id, String name) {
        this.id = id;
        this.branch_id = branch_id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
