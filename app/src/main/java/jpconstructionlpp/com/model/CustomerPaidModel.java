package jpconstructionlpp.com.model;

public class CustomerPaidModel {
    private String id,getdate,amount;

    public CustomerPaidModel(String id, String getdate, String amount) {
        this.id = id;
        this.getdate = getdate;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGetdate() {
        return getdate;
    }

    public void setGetdate(String getdate) {
        this.getdate = getdate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
