import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 需要收件人的通道共用的投递循环。
 * 格式不合法的地址不调用 Outbound.send。
 * 连接在第一条发出之前失败时，合法地址都用该原因；格式错误的行仍是「地址格式不正确」。
 */
final class Delivery {

    private Delivery() {
    }

    static List<AddressReceipt> each(List<String> rawAddresses, Predicate<String> valid, Outbound outbound, Rendered rendered) {
        List<String> addresses = new ArrayList<>();
        List<Boolean> accepted = new ArrayList<>();
        boolean anyValid = false;
        for (String raw : rawAddresses) {
            String address = raw == null ? "" : raw.trim();
            boolean ok = valid.test(address);
            addresses.add(address);
            accepted.add(ok);
            anyValid = anyValid || ok;
        }
        if (anyValid) {
            try {
                outbound.connect();
            } catch (RuntimeException ex) {
                return failedConnection(addresses, accepted, ex.getMessage());
            }
        }
        List<AddressReceipt> rows = new ArrayList<>();
        for (int i = 0; i < addresses.size(); i++) {
            String address = addresses.get(i);
            if (!accepted.get(i)) {
                rows.add(AddressReceipt.fail(address, RejectReason.BAD_ADDRESS));
                continue;
            }
            try {
                outbound.send(address, rendered);
                rows.add(AddressReceipt.ok(address));
            } catch (RuntimeException ex) {
                rows.add(AddressReceipt.fail(address, ex.getMessage()));
            }
        }
        return rows;
    }

    private static List<AddressReceipt> failedConnection(List<String> addresses, List<Boolean> accepted, String reason) {
        List<AddressReceipt> rows = new ArrayList<>();
        for (int i = 0; i < addresses.size(); i++) {
            if (!accepted.get(i)) {
                rows.add(AddressReceipt.fail(addresses.get(i), RejectReason.BAD_ADDRESS));
            } else {
                rows.add(AddressReceipt.fail(addresses.get(i), reason));
            }
        }
        return rows;
    }
}
