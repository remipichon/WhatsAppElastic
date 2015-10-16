package co.paan.entities;

/**
 * Created by remi on 12/10/15.
 */
public class Progress {
    private Integer value;
    private Integer total;

    public Progress() {
    }

    public Progress(Integer value, Integer total) {
        this.value = value;
        this.total = total;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getTotal() {
        return total;
    }
}
