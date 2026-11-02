/**
 * 一个地址的回执。机器人没有收件人地址时，address 填通道 ID。
 * delivered 为 false 时 reason 必有值。
 */
public record AddressReceipt(String address, boolean delivered, String reason) {

    public static AddressReceipt ok(String address) {
        return new AddressReceipt(address, true, null);
    }

    public static AddressReceipt fail(String address, String reason) {
        return new AddressReceipt(address, false, reason);
    }
}
