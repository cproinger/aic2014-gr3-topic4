package at.tuwien.aic2014.gr3.tweetsminer.filters;

public class DataCarrier<T> {

    private T data;

    public DataCarrier(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
