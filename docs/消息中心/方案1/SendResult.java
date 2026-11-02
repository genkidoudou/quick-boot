import java.util.List;

/**
 * 一次发送的结果，两种形态互斥：
 * 整单拒绝时 rejection 有值，receipts 为 null；
 * 进入投递后 rejection 为 null，receipts 与地址等长，其中可以有失败行。
 */
public record SendResult(String rejection, List<AddressReceipt> receipts) {

    public static SendResult reject(String reason) {
        return new SendResult(reason, null);
    }

    public static SendResult ok(List<AddressReceipt> receipts) {
        return new SendResult(null, List.copyOf(receipts));
    }

    public boolean rejected() {
        return rejection != null;
    }
}
