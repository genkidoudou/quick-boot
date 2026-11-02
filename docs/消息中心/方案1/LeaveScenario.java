import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用内存里的通道和群组走一遍「请假已通过」。
 * 数据与 sample.sql 的主键一致。直接运行 main 即可看到回执。
 * 不连接 SMTP，也不访问钉钉、企业微信或 IYUU。
 */
public final class LeaveScenario {

    private static final SendMessage MESSAGE = new SendMessage(
            "请假已通过",
            "王敏 11 月 3 日的年假已通过。",
            "/leave/2041");

    public static void main(String[] args) {
        Memory memory = memory();
        MessageSender sender = service(memory, failures());

        System.out.println("消息");
        System.out.println(Payload.email(MESSAGE).title());
        System.out.println(Payload.email(MESSAGE).text());
        System.out.println();

        print("邮件 · 地址列表", sender.send(101L, List.of(
                "wangmin@corp.example",
                "hr-dept@corp.example",
                "guest@partner.example"), null, MESSAGE));
        print("邮件 · 群组 201", sender.send(101L, null, 201L, MESSAGE));

        System.out.println("短信实际发出");
        System.out.println(Payload.sms(MESSAGE).text());
        print("短信", sender.send(102L, List.of("13800001111", "abc"), null, MESSAGE));

        print("站内信", sender.send(103L, List.of("U10086", "U10087"), null, MESSAGE));
        System.out.println("站内信表");
        for (InboxRow row : memory.inbox) {
            System.out.println(row.userAddress + "  " + row.message.title() + "  " + row.message.link());
        }
        System.out.println();

        System.out.println("企业微信应用实际发出");
        System.out.println(Payload.wecomApp(MESSAGE).text());
        print("企业微信应用", sender.send(104L, List.of("ZhangSan", "LiSi"), null, MESSAGE));

        print("钉钉机器人", sender.send(105L, null, null, MESSAGE));
        print("企业微信机器人", sender.send(106L, null, null, MESSAGE));
        print("IYUU", sender.send(107L, null, null, MESSAGE));

        print("通道不存在", sender.send(999L, List.of("a@b.c"), null, MESSAGE));
        print("通道已停用", sender.send(108L, null, null, new SendMessage("  ", "正文", null)));
        print("标题或正文为空", sender.send(101L, List.of("a@b.c"), null, new SendMessage("  ", "正文", null)));
        print("地址和群组一起传", sender.send(101L, List.of("a@b.c"), 201L, MESSAGE));
        print("缺少收件人", sender.send(101L, null, null, MESSAGE));
        print("空地址列表", sender.send(101L, List.of(), null, MESSAGE));
        print("机器人带地址", sender.send(105L, List.of("ZhangSan"), null, MESSAGE));
        print("群组不存在", sender.send(101L, null, 999L, MESSAGE));
        print("群组类型不符", sender.send(102L, null, 201L, MESSAGE));
        print("空群组但类型不符", sender.send(101L, null, 203L, MESSAGE));
        print("群组为空", sender.send(101L, null, 202L, MESSAGE));

        MessageSender broken = service(memory, connectFailure());
        print("连接在发出之前失败", broken.send(101L, List.of(
                "wangmin@corp.example",
                "hr-dept@corp.example",
                "guest@partner.example"), null, MESSAGE));
    }

    private static void print(String title, SendResult result) {
        System.out.println(title);
        if (result.rejected()) {
            System.out.println("整单拒绝  " + result.rejection());
            System.out.println("回执  无");
            System.out.println();
            return;
        }
        for (AddressReceipt row : result.receipts()) {
            if (row.delivered()) {
                System.out.println(row.address() + "  已送达");
            } else {
                System.out.println(row.address() + "  未送达  " + row.reason());
            }
        }
        System.out.println();
    }

    private static MessageSender service(Memory memory, Map<Long, Outbound> outbounds) {
        return new MessageSendService(memory, memory, List.of(
                RecipientAdapters.email(outbounds.get(101L)),
                RecipientAdapters.sms(outbounds.get(102L)),
                RecipientAdapters.inbox(memory),
                RecipientAdapters.wecomApp(outbounds.get(104L)),
                RobotAdapters.dingtalk(outbounds.get(105L)),
                RobotAdapters.wecomRobot(outbounds.get(106L)),
                RobotAdapters.iyuu(outbounds.get(107L))));
    }

    /** 供应商按地址拒绝。连接本身是通的。 */
    private static Map<Long, Outbound> failures() {
        Map<Long, Outbound> map = new LinkedHashMap<>();
        map.put(101L, new ScriptedOutbound(null, Map.of("guest@partner.example", "对方服务器拒收")));
        map.put(102L, new ScriptedOutbound(null, Map.of()));
        map.put(104L, new ScriptedOutbound(null, Map.of("LiSi", "不在应用可见范围")));
        map.put(105L, new ScriptedOutbound(null, Map.of()));
        map.put(106L, new ScriptedOutbound(null, Map.of()));
        map.put(107L, new ScriptedOutbound(null, Map.of()));
        return map;
    }

    private static Map<Long, Outbound> connectFailure() {
        Map<Long, Outbound> map = failures();
        map.put(101L, new ScriptedOutbound("无法连接 SMTP", Map.of()));
        return map;
    }

    private static Memory memory() {
        ChannelConfig empty = new ChannelConfig(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        Memory memory = new Memory();
        memory.channels.put(101L, new Channel(101L, "mail-office", ChannelType.EMAIL, true,
                new ChannelConfig("smtp.corp.example", 465, "notice", "secret", "notice@corp.example",
                        null, null, null, null, null, null, null, null, null)));
        memory.channels.put(102L, new Channel(102L, "sms-notice", ChannelType.SMS, true,
                new ChannelConfig(null, null, null, null, null, "sms-key", "人事通知", "https://sms.example/send",
                        null, null, null, null, null, null)));
        memory.channels.put(103L, new Channel(103L, "inbox", ChannelType.INBOX, true, empty));
        memory.channels.put(104L, new Channel(104L, "wecom-hr", ChannelType.WECOM_APP, true,
                new ChannelConfig(null, null, null, null, null, null, null, null, "ww-corp", "1000024", "agent-secret",
                        null, null, null)));
        memory.channels.put(105L, new Channel(105L, "ding-ops", ChannelType.DINGTALK_ROBOT, true,
                new ChannelConfig(null, null, null, null, null, null, null, null, null, null, null,
                        "https://oapi.dingtalk.com/robot/send?access_token=demo", "ding-secret", null)));
        memory.channels.put(106L, new Channel(106L, "wecom-bot", ChannelType.WECOM_ROBOT, true,
                new ChannelConfig(null, null, null, null, null, null, null, null, null, null, null,
                        "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=demo", null, null)));
        memory.channels.put(107L, new Channel(107L, "iyuu-ops", ChannelType.IYUU, true,
                new ChannelConfig(null, null, null, null, null, null, null, null, null, null, null, null, null, "iyuu-token")));
        memory.channels.put(108L, new Channel(108L, "mail-paused", ChannelType.EMAIL, false, empty));
        memory.groups.put(201L, new AddressGroup(201L, "g-hr-mail", ChannelType.EMAIL, List.of(
                "wangmin@corp.example", "hr-dept@corp.example", "guest@partner.example")));
        memory.groups.put(202L, new AddressGroup(202L, "g-empty-mail", ChannelType.EMAIL, List.of()));
        memory.groups.put(203L, new AddressGroup(203L, "g-empty-sms", ChannelType.SMS, List.of()));
        return memory;
    }

    /** 通道、群组和站内信都放在内存里，主键与 sample.sql 相同。 */
    private static final class Memory implements ChannelStore, GroupStore, InboxStore {
        private final Map<Long, Channel> channels = new LinkedHashMap<>();
        private final Map<Long, AddressGroup> groups = new LinkedHashMap<>();
        private final List<InboxRow> inbox = new ArrayList<>();

        public Channel findChannel(long id) {
            return channels.get(id);
        }

        public AddressGroup findGroup(long id) {
            return groups.get(id);
        }

        public void save(long channelId, String userAddress, SendMessage message) {
            inbox.add(new InboxRow(channelId, userAddress, message));
        }
    }

    private record InboxRow(long channelId, String userAddress, SendMessage message) {
    }

    /** connectError 非空时，第一条还没发出就失败。failures 的键是地址，值是该地址的供应商原因。 */
    private static final class ScriptedOutbound implements Outbound {
        private final String connectError;
        private final Map<String, String> failures;

        private ScriptedOutbound(String connectError, Map<String, String> failures) {
            this.connectError = connectError;
            this.failures = failures;
        }

        public void connect() {
            if (connectError != null) {
                throw new IllegalStateException(connectError);
            }
        }

        public void send(String address, Rendered rendered) {
            String reason = failures.get(address);
            if (reason != null) {
                throw new IllegalStateException(reason);
            }
        }
    }
}
