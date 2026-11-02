/**
 * 供应商调用。connect 在第一条真正发出之前调用；
 * 这里失败时，尚未发出的合法地址使用同一个原因。
 * send 的失败只影响这一个地址。
 */
interface Outbound {
    void connect();

    void send(String address, Rendered rendered);
}
