/**
 * 五套页面共用内存数据（刷新即重置）。
 * - 群组 = 接收人（系统用户 / 部门 / 内嵌外部人）；无独立「外部联系人」模块
 * - 渠道发送时勾选；Webhook/Bean 忽略群组成员
 * - Token = 调用方 + 可选渠道白名单
 */
(function (global) {
  const now = () => {
    const d = new Date();
    const p = (n) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
  };

  const uid = (prefix) =>
    `${prefix}_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 6)}`;

  const users = [
    { id: 1, name: "张三", dept: "运维中心" },
    { id: 2, name: "李四", dept: "运维中心" },
    { id: 3, name: "王五", dept: "研发一部" },
    { id: 4, name: "赵六", dept: "基础设施" },
    { id: 5, name: "钱七", dept: "应用运维" },
    { id: 6, name: "孙八", dept: "研发一部" },
    { id: 7, name: "周九", dept: "产品中心" },
    { id: 8, name: "吴十", dept: "基础设施" },
  ];

  const depts = [
    { id: 10, name: "总部", parentId: null },
    { id: 11, name: "运维中心", parentId: 10 },
    { id: 12, name: "基础设施", parentId: 11 },
    { id: 13, name: "应用运维", parentId: 11 },
    { id: 14, name: "研发一部", parentId: 10 },
    { id: 15, name: "产品中心", parentId: 10 },
  ];

  const beanCatalog = [
    { name: "noticeSender", label: "NoticeSender · 自定义通知" },
    { name: "iyuuSender", label: "IyuuSender · IYUU 推送" },
    { name: "smsStubSender", label: "SmsStubSender · 短信 Stub" },
  ];

  function usersInDept(deptId, includeChildren) {
    const ids = new Set([deptId]);
    if (includeChildren) {
      let changed = true;
      while (changed) {
        changed = false;
        depts.forEach((d) => {
          if (d.parentId != null && ids.has(d.parentId) && !ids.has(d.id)) {
            ids.add(d.id);
            changed = true;
          }
        });
      }
    }
    const names = new Set(depts.filter((d) => ids.has(d.id)).map((d) => d.name));
    return users.filter((u) => names.has(u.dept));
  }

  function normalizeExternal(ex) {
    return {
      id: ex.id || uid("ex"),
      name: (ex.name || "").trim(),
      phone: (ex.phone || "").trim(),
      email: (ex.email || "").trim(),
    };
  }

  function validateExternal(ex) {
    if (!ex.name?.trim()) throw new Error("外部人请填写名称");
    if (!ex.phone?.trim() && !ex.email?.trim()) {
      throw new Error("外部人手机号与邮箱至少填一项");
    }
  }

  const state = {
    channels: [
      {
        id: "ch_inbox",
        name: "系统站内信",
        type: "INBOX",
        status: "1",
        webhookUrl: "",
        beanName: "",
        remark: "内置",
        updateTime: "2026-10-20 10:00",
      },
      {
        id: "ch_ding",
        name: "运维钉钉群",
        type: "WEBHOOK",
        status: "1",
        webhookUrl: "https://oapi.dingtalk.com/robot/send?access_token=demo",
        beanName: "",
        remark: "值班群",
        updateTime: "2026-10-18 09:30",
      },
      {
        id: "ch_feishu",
        name: "发版飞书",
        type: "WEBHOOK",
        status: "0",
        webhookUrl: "https://open.feishu.cn/open-apis/bot/v2/hook/demo",
        beanName: "",
        remark: "发版通知",
        updateTime: "2026-09-02 16:20",
      },
      {
        id: "ch_iyuu",
        name: "IYUU 推送",
        type: "BEAN",
        status: "1",
        webhookUrl: "",
        beanName: "iyuuSender",
        remark: "自定义 Bean",
        updateTime: "2026-10-29 12:00",
      },
    ],
    groups: [
      {
        id: "gp_1",
        name: "核心运维",
        remark: "用户 + 部门 + 内嵌外部人",
        userIds: [1, 2, 4],
        deptIds: [11, 12],
        externals: [
          { id: "ex_1", name: "外包值班", phone: "13800002211", email: "" },
        ],
      },
      {
        id: "gp_2",
        name: "发版通知",
        remark: "",
        userIds: [3, 7],
        deptIds: [14],
        externals: [],
      },
    ],
    tokens: [
      {
        id: "tk_1",
        name: "CI 脚本",
        token: "msg_ci_a3f2demo",
        expireTime: "2027-01-01",
        status: "1",
        allowedChannelIds: [],
      },
      {
        id: "tk_2",
        name: "监控网关",
        token: "msg_mon_91bcdemo",
        expireTime: "",
        status: "1",
        allowedChannelIds: ["ch_ding", "ch_iyuu"],
      },
    ],
    inbox: [
      {
        id: "in_1",
        title: "生产环境 CPU 告警",
        content: "host-a CPU 92%，持续 5 分钟。\n请值班同学处理。",
        read: false,
        time: "2026-10-29 14:02",
        channelName: "系统站内信",
      },
      {
        id: "in_2",
        title: "发版完成 · payment",
        content: "v2.18.0 已上线，变更 12 项。",
        read: true,
        time: "2026-10-28 09:11",
        channelName: "系统站内信",
      },
      {
        id: "in_3",
        title: "CI 失败通知",
        content: "payment-service #1284 tests failed (3)。",
        read: true,
        time: "2026-10-27 18:20",
        channelName: "系统站内信",
      },
    ],
    records: [
      {
        id: "rc_1",
        title: "生产环境 CPU 告警",
        content: "host-a CPU 92%",
        time: "2026-10-29 14:02",
        channelIds: ["ch_inbox", "ch_ding"],
        results: [
          { channelId: "ch_inbox", status: "SUCCESS", detail: "展开用户投递" },
          { channelId: "ch_ding", status: "SUCCESS", detail: "HTTP 200（忽略群组）" },
        ],
        recipientSummary: "群组·核心运维 → 仅站内信消费；Webhook 忽略群组",
      },
    ],
  };

  function channelById(id) {
    return state.channels.find((c) => c.id === id);
  }

  function channelTypeLabel(type) {
    return { INBOX: "站内信", WEBHOOK: "Webhook", BEAN: "Bean" }[type] || type;
  }

  function channelUsesRecipients(type) {
    return type === "INBOX";
  }

  function expandRecipients({
    userIds = [],
    deptIds = [],
    groupIds = [],
    externals = [],
    includeChildDepts = true,
  }) {
    const u = new Set(userIds);
    const extMap = new Map();
    (externals || []).forEach((ex) => {
      const n = normalizeExternal(ex);
      if (n.name) extMap.set(n.id, n);
    });
    (groupIds || []).forEach((gid) => {
      const g = state.groups.find((x) => x.id === gid);
      if (!g) return;
      (g.userIds || []).forEach((id) => u.add(id));
      (g.deptIds || []).forEach((id) => {
        usersInDept(id, true).forEach((user) => u.add(user.id));
      });
      (g.externals || []).forEach((ex) => {
        const n = normalizeExternal(ex);
        if (n.name) extMap.set(n.id, n);
      });
    });
    (deptIds || []).forEach((id) => {
      usersInDept(id, includeChildDepts).forEach((user) => u.add(user.id));
    });
    return {
      users: users.filter((x) => u.has(x.id)),
      externals: [...extMap.values()],
    };
  }

  function groupSummary(g) {
    const expanded = expandRecipients({
      userIds: g.userIds,
      deptIds: g.deptIds,
      externals: g.externals,
    });
    return {
      userCount: (g.userIds || []).length,
      externalCount: (g.externals || []).length,
      deptCount: (g.deptIds || []).length,
      expandedUserCount: expanded.users.length,
      deptNames: (g.deptIds || [])
        .map((id) => depts.find((d) => d.id === id)?.name)
        .filter(Boolean)
        .join("、"),
    };
  }

  function assertTokenChannels(tokenId, channelIds) {
    if (!tokenId) return;
    const t = state.tokens.find((x) => x.id === tokenId);
    if (!t) throw new Error("Token 不存在");
    if (t.status !== "1") throw new Error("Token 已停用");
    if (!t.allowedChannelIds?.length) return;
    const denied = channelIds.filter((id) => !t.allowedChannelIds.includes(id));
    if (denied.length) {
      const names = denied.map((id) => channelById(id)?.name || id).join("、");
      throw new Error(`Token 不允许渠道：${names}`);
    }
  }

  function sendMessage(payload) {
    const {
      title,
      content,
      channelIds,
      userIds = [],
      deptIds = [],
      groupIds = [],
      externals = [],
      includeChildDepts = true,
      tokenId,
    } = payload;
    if (!title?.trim()) throw new Error("请填写标题");
    if (!content?.trim()) throw new Error("请填写内容");
    if (!channelIds?.length) throw new Error("请至少选择一个渠道");

    assertTokenChannels(tokenId, channelIds);

    const enabled = channelIds.map(channelById).filter((c) => c && c.status === "1");
    if (!enabled.length) throw new Error("所选渠道均未启用");

    const needPeople = enabled.some((c) => channelUsesRecipients(c.type));
    const expanded = expandRecipients({
      userIds,
      deptIds,
      groupIds,
      externals,
      includeChildDepts,
    });
    if (needPeople && !expanded.users.length && !expanded.externals.length) {
      throw new Error("站内信需要接收人：请选择群组 / 用户 / 部门（Webhook、Bean 不消费群组）");
    }

    const results = enabled.map((ch) => {
      if (ch.type === "INBOX") {
        if (!expanded.users.length) {
          const skip = expanded.externals.length
            ? `外部人 ${expanded.externals.length} 名无法写站内信；无系统用户`
            : "无系统用户可写收件箱";
          return { channelId: ch.id, status: "SKIPPED", detail: skip };
        }
        expanded.users.forEach((u) => {
          state.inbox.unshift({
            id: uid("in"),
            title,
            content,
            read: false,
            time: now(),
            channelName: ch.name,
            toUser: u.name,
          });
        });
        const extra = expanded.externals.length
          ? `；外部人 ${expanded.externals.length} 名不入站内信`
          : "";
        return {
          channelId: ch.id,
          status: "SUCCESS",
          detail: `展开 ${expanded.users.length} 人${extra}`,
        };
      }
      if (ch.type === "WEBHOOK") {
        if (!ch.webhookUrl) {
          return { channelId: ch.id, status: "FAIL", detail: "未配置 URL" };
        }
        return {
          channelId: ch.id,
          status: "SUCCESS",
          detail: "HTTP 200（模拟；忽略群组）",
        };
      }
      if (ch.type === "BEAN") {
        if (!ch.beanName) {
          return { channelId: ch.id, status: "FAIL", detail: "未配置 Bean 名称" };
        }
        if (!beanCatalog.some((b) => b.name === ch.beanName)) {
          return { channelId: ch.id, status: "FAIL", detail: `容器中无 Bean：${ch.beanName}` };
        }
        return {
          channelId: ch.id,
          status: "SUCCESS",
          detail: `调用 ${ch.beanName}（整批投递；忽略群组成员）`,
        };
      }
      return { channelId: ch.id, status: "FAIL", detail: "未知渠道类型" };
    });

    const parts = [];
    if (groupIds.length) parts.push(`群组×${groupIds.length}`);
    if (deptIds.length) parts.push(`部门×${deptIds.length}`);
    if (userIds.length) parts.push(`用户×${userIds.length}`);
    if (needPeople) {
      parts.push(`展开用户 ${expanded.users.length}`);
      if (expanded.externals.length) parts.push(`外部人 ${expanded.externals.length}`);
    }
    const hookLike = enabled.filter((c) => !channelUsesRecipients(c.type)).length;
    if (hookLike) parts.push(`Webhook/Bean×${hookLike}`);

    const record = {
      id: uid("rc"),
      title,
      content,
      time: now(),
      channelIds: enabled.map((c) => c.id),
      results,
      recipientSummary: parts.join(" · ") || "仅渠道投递",
      tokenId: tokenId || null,
    };
    state.records.unshift(record);
    return record;
  }

  function saveChannel(input) {
    if (!input.name?.trim()) throw new Error("请填写渠道名称");
    const type = input.type || "WEBHOOK";
    if (type === "WEBHOOK" && !input.webhookUrl?.trim()) {
      throw new Error("Webhook 渠道必须填写 URL");
    }
    if (type === "BEAN" && !input.beanName?.trim()) {
      throw new Error("Bean 渠道必须选择 Bean 名称");
    }
    if (type === "BEAN" && !beanCatalog.some((b) => b.name === input.beanName)) {
      throw new Error("Bean 不在容器目录中");
    }
    if (input.id) {
      const ch = channelById(input.id);
      if (!ch) throw new Error("渠道不存在");
      if (ch.type === "INBOX" && type !== "INBOX") {
        throw new Error("站内信渠道类型不可改");
      }
      Object.assign(ch, {
        name: input.name.trim(),
        status: input.status ?? ch.status,
        webhookUrl: type === "WEBHOOK" ? (input.webhookUrl || "").trim() : "",
        beanName: type === "BEAN" ? (input.beanName || "").trim() : "",
        remark: input.remark ?? ch.remark,
        updateTime: now(),
      });
      if (ch.type !== "INBOX") ch.type = type;
      return ch;
    }
    if (type === "INBOX") throw new Error("站内信为内置渠道，不可新建");
    const ch = {
      id: uid("ch"),
      name: input.name.trim(),
      type,
      status: input.status ?? "1",
      webhookUrl: type === "WEBHOOK" ? (input.webhookUrl || "").trim() : "",
      beanName: type === "BEAN" ? (input.beanName || "").trim() : "",
      remark: input.remark || "",
      updateTime: now(),
    };
    state.channels.push(ch);
    return ch;
  }

  function saveGroup(input) {
    if (!input.name?.trim()) throw new Error("请填写群组名称");
    const externals = (input.externals || []).map(normalizeExternal);
    externals.forEach(validateExternal);
    const body = {
      name: input.name.trim(),
      remark: input.remark || "",
      userIds: [...new Set(input.userIds || [])],
      deptIds: [...new Set(input.deptIds || [])],
      externals,
    };
    if (!body.userIds.length && !body.deptIds.length && !body.externals.length) {
      throw new Error("请至少选择用户、部门，或添加一名外部人");
    }
    if (input.id) {
      const g = state.groups.find((x) => x.id === input.id);
      if (!g) throw new Error("群组不存在");
      Object.assign(g, body);
      return g;
    }
    const g = { id: uid("gp"), ...body };
    state.groups.push(g);
    return g;
  }

  function removeGroup(id) {
    const i = state.groups.findIndex((g) => g.id === id);
    if (i >= 0) state.groups.splice(i, 1);
  }

  function saveToken(input) {
    if (!input.name?.trim()) throw new Error("请填写 Token 名称");
    const allowed = [...new Set(input.allowedChannelIds || [])];
    if (input.id) {
      const t = state.tokens.find((x) => x.id === input.id);
      if (!t) throw new Error("Token 不存在");
      Object.assign(t, {
        name: input.name.trim(),
        expireTime: input.expireTime || "",
        status: input.status ?? t.status,
        allowedChannelIds: allowed,
      });
      return t;
    }
    const t = {
      id: uid("tk"),
      name: input.name.trim(),
      token: `msg_${Math.random().toString(36).slice(2, 10)}demo`,
      expireTime: input.expireTime || "",
      status: input.status ?? "1",
      allowedChannelIds: allowed,
    };
    state.tokens.push(t);
    return t;
  }

  function removeToken(id) {
    const i = state.tokens.findIndex((t) => t.id === id);
    if (i >= 0) state.tokens.splice(i, 1);
  }

  function markInboxRead(id, read = true) {
    const m = state.inbox.find((x) => x.id === id);
    if (m) m.read = read;
    return m;
  }

  function markAllInboxRead() {
    state.inbox.forEach((m) => {
      m.read = true;
    });
  }

  function channelConfigText(ch) {
    if (ch.type === "INBOX") return "内置";
    if (ch.type === "WEBHOOK") return ch.webhookUrl || "未配置 URL";
    if (ch.type === "BEAN") return ch.beanName ? `Bean: ${ch.beanName}` : "未配置 Bean";
    return "—";
  }

  function tokenScopeText(t) {
    if (!t.allowedChannelIds?.length) return "全部启用渠道";
    return t.allowedChannelIds
      .map((id) => channelById(id)?.name || id)
      .join("、");
  }

  global.MsgMock = {
    state,
    users,
    depts,
    beanCatalog,
    now,
    uid,
    normalizeExternal,
    validateExternal,
    channelById,
    channelTypeLabel,
    channelUsesRecipients,
    channelConfigText,
    tokenScopeText,
    usersInDept,
    expandRecipients,
    groupSummary,
    sendMessage,
    saveChannel,
    saveGroup,
    removeGroup,
    saveToken,
    removeToken,
    markInboxRead,
    markAllInboxRead,
  };
})(window);
