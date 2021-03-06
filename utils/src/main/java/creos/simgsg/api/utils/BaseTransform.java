package creos.simgsg.api.utils;

public class BaseTransform {
    private BaseTransform() {}


    public static boolean[] toBinary(int data, int size) {
        var res = new boolean[size];
        for (int i = 0; i < res.length; i++) {
            res[size - 1 - i] = (1 << i & data) != 0;
        }

        return res;
    }
}
