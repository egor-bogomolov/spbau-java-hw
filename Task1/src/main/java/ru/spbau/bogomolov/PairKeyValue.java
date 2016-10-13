package ru.spbau.bogomolov;

/**
 * Created by Egor Bogomolov on 9/19/16.
 */

public class PairKeyValue {
    public int key;
    public String value;

    public PairKeyValue(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PairKeyValue that = (PairKeyValue) o;

        return key == that.key && value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        int result = key;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
