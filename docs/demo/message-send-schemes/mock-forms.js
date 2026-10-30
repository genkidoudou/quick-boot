/**
 * 共用表单片段（依赖 mock-store.js）。
 * 对齐：群组≠渠道；Webhook/Bean 忽略群组；Token 可配渠道白名单；渠道含 Bean。
 */
(function (global) {
  const M = () => global.MsgMock;

  function esc(s) {
    return String(s ?? "").replace(/[&<>"']/g, (c) =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c])
    );
  }

  function channelTypeOptions(selected, { forNew } = { forNew: true }) {
    const cur = selected || "WEBHOOK";
    if (!forNew && cur === "INBOX") {
      return `<option value="INBOX" selected>站内信（内置）</option>`;
    }
    return `
      <option value="WEBHOOK" ${cur === "WEBHOOK" ? "selected" : ""}>Webhook</option>
      <option value="BEAN" ${cur === "BEAN" ? "selected" : ""}>Bean</option>`;
  }

  function beanOptions(selected) {
    return M()
      .beanCatalog.map(
        (b) =>
          `<option value="${esc(b.name)}" ${b.name === selected ? "selected" : ""}>${esc(b.label)}</option>`
      )
      .join("");
  }

  /** 渠道编辑字段（放入已有抽屉结构） */
  function channelEditorFields(ch, { isNew }) {
    const inbox = ch.type === "INBOX";
    return `
      <div class="field"><label>名称</label><input id="mfName" value="${esc(ch.name || "")}"></div>
      <div class="field"><label>类型</label>
        <select id="mfType" ${inbox || (!isNew && inbox) ? "disabled" : ""}>
          ${channelTypeOptions(ch.type || "WEBHOOK", { forNew: isNew && !inbox })}
        </select>
      </div>
      <div class="field" id="mfUrlWrap" style="${(ch.type || "WEBHOOK") === "WEBHOOK" && !inbox ? "" : "display:none"}">
        <label>Webhook URL</label><input id="mfUrl" value="${esc(ch.webhookUrl || "")}">
      </div>
      <div class="field" id="mfBeanWrap" style="${ch.type === "BEAN" ? "" : "display:none"}">
        <label>Bean 名称（容器中的 MessageChannel / MessageSender）</label>
        <select id="mfBean"><option value="">请选择</option>${beanOptions(ch.beanName || "")}</select>
      </div>
      <div class="field"><label>备注</label><input id="mfRemark" value="${esc(ch.remark || "")}"></div>
      <div class="field"><label>状态</label>
        <select id="mfStatus">
          <option value="1" ${ch.status !== "0" ? "selected" : ""}>启用</option>
          <option value="0" ${ch.status === "0" ? "selected" : ""}>停用</option>
        </select>
      </div>
      <p class="hint" style="font-size:12px;opacity:.75">
        对齐 PushPlus：渠道是「怎么发」。Webhook / Bean 类似 option 配置实例；站内信才按群组成员展开。
      </p>`;
  }

  function wireChannelTypeToggle() {
    const typeEl = document.getElementById("mfType");
    if (!typeEl) return;
    const sync = () => {
      const t = typeEl.value;
      const url = document.getElementById("mfUrlWrap");
      const bean = document.getElementById("mfBeanWrap");
      if (url) url.style.display = t === "WEBHOOK" ? "" : "none";
      if (bean) bean.style.display = t === "BEAN" ? "" : "none";
    };
    typeEl.addEventListener("change", sync);
    sync();
  }

  function readChannelForm(existing) {
    const inbox = existing?.type === "INBOX";
    const type = inbox ? "INBOX" : document.getElementById("mfType")?.value || "WEBHOOK";
    return {
      id: existing?.id,
      name: document.getElementById("mfName")?.value || "",
      type,
      webhookUrl: document.getElementById("mfUrl")?.value || "",
      beanName: document.getElementById("mfBean")?.value || "",
      remark: document.getElementById("mfRemark")?.value || "",
      status: document.getElementById("mfStatus")?.value || "1",
    };
  }

  function sendRelationHintHtml() {
    return `<p class="hint" style="font-size:12px;line-height:1.5;margin:8px 0 12px">
      <b>关联方式（对齐 PushPlus）</b>：群组只表示接收人（系统用户 / 部门 / 内嵌外部人），无独立联系人模块；渠道在发送时勾选。
      站内信展开系统用户；外部人不入站内信；<b>Webhook、Bean 忽略群组成员</b>。
    </p>`;
  }

  /** 群组内嵌外部人列表编辑区 */
  function externalsEditorHtml(externals) {
    const list = externals || [];
    return `
      <div id="mfExtList" style="display:grid;gap:8px;margin-bottom:8px">
        ${list
          .map(
            (ex, i) => `
          <div data-ex-row="${i}" style="border:1px solid #ddd;border-radius:8px;padding:8px;display:grid;gap:6px">
            <input data-f="name" placeholder="名称" value="${esc(ex.name || "")}">
            <input data-f="phone" placeholder="手机" value="${esc(ex.phone || "")}">
            <input data-f="email" placeholder="邮箱" value="${esc(ex.email || "")}">
            <input type="hidden" data-f="id" value="${esc(ex.id || "")}">
            <button type="button" data-del-ex="${i}" style="justify-self:start;font-size:12px">移除</button>
          </div>`
          )
          .join("") || `<div class="hint" style="font-size:12px;opacity:.7">暂无外部人，点击下方添加</div>`}
      </div>
      <button type="button" id="mfAddExt" style="font-size:12px;margin-bottom:8px">+ 添加外部人</button>
      <p class="hint" style="font-size:12px;opacity:.75">外部人挂在本群组内，不进独立通讯录；跨群复用可再建小群或复制。</p>`;
  }

  function wireExternalsEditor() {
    const root = document.getElementById("mfExtList");
    const addBtn = document.getElementById("mfAddExt");
    if (!root || !addBtn) return;
    const renumber = () => {
      [...root.querySelectorAll("[data-ex-row]")].forEach((row, i) => {
        row.setAttribute("data-ex-row", String(i));
        const del = row.querySelector("[data-del-ex]");
        if (del) del.setAttribute("data-del-ex", String(i));
      });
    };
    addBtn.onclick = () => {
      const empty = root.querySelector(".hint");
      if (empty) empty.remove();
      const i = root.querySelectorAll("[data-ex-row]").length;
      const div = document.createElement("div");
      div.setAttribute("data-ex-row", String(i));
      div.style.cssText = "border:1px solid #ddd;border-radius:8px;padding:8px;display:grid;gap:6px";
      div.innerHTML = `
        <input data-f="name" placeholder="名称" value="">
        <input data-f="phone" placeholder="手机" value="">
        <input data-f="email" placeholder="邮箱" value="">
        <input type="hidden" data-f="id" value="">
        <button type="button" data-del-ex="${i}" style="justify-self:start;font-size:12px">移除</button>`;
      root.appendChild(div);
      div.querySelector("[data-del-ex]").onclick = () => {
        div.remove();
        renumber();
        if (!root.querySelector("[data-ex-row]")) {
          root.innerHTML = `<div class="hint" style="font-size:12px;opacity:.7">暂无外部人，点击下方添加</div>`;
        }
      };
    };
    root.querySelectorAll("[data-del-ex]").forEach((btn) => {
      btn.onclick = () => {
        btn.closest("[data-ex-row]")?.remove();
        renumber();
        if (!root.querySelector("[data-ex-row]")) {
          root.innerHTML = `<div class="hint" style="font-size:12px;opacity:.7">暂无外部人，点击下方添加</div>`;
        }
      };
    });
  }

  function readExternalsFromEditor() {
    const root = document.getElementById("mfExtList");
    if (!root) return [];
    return [...root.querySelectorAll("[data-ex-row]")].map((row) => ({
      id: row.querySelector('[data-f="id"]')?.value || undefined,
      name: row.querySelector('[data-f="name"]')?.value || "",
      phone: row.querySelector('[data-f="phone"]')?.value || "",
      email: row.querySelector('[data-f="email"]')?.value || "",
    }));
  }

  function tokenScopeFields(t) {
    const allowed = new Set(t.allowedChannelIds || []);
    return `
      <div class="field"><label>名称</label><input id="mfTokName" value="${esc(t.name || "")}"></div>
      <div class="field"><label>过期（可空=永久）</label><input id="mfTokExp" value="${esc(t.expireTime || "")}" placeholder="2027-01-01"></div>
      <div class="field"><label>状态</label>
        <select id="mfTokStatus">
          <option value="1" ${t.status !== "0" ? "selected" : ""}>启用</option>
          <option value="0" ${t.status === "0" ? "selected" : ""}>停用</option>
        </select>
      </div>
      <div class="field"><label>允许渠道（空=全部；不是一渠一 Token）</label>
        <div class="check-grid" id="mfTokCh" style="max-height:160px;overflow:auto;border:1px solid #ddd;border-radius:8px;padding:8px">
          ${M()
            .state.channels.map(
              (c) =>
                `<label style="display:flex;gap:8px;align-items:center;font-size:13px;padding:3px 0">
                  <input type="checkbox" value="${c.id}" ${allowed.has(c.id) ? "checked" : ""}>
                  ${esc(c.name)}（${esc(M().channelTypeLabel(c.type))}）
                </label>`
            )
            .join("")}
        </div>
      </div>
      <p class="hint" style="font-size:12px;opacity:.75">Token 标识调用方；请求里再选 channel。白名单用于收窄权限。</p>`;
  }

  function readTokenForm(existing) {
    const box = document.getElementById("mfTokCh");
    return {
      id: existing?.id,
      name: document.getElementById("mfTokName")?.value || "",
      expireTime: document.getElementById("mfTokExp")?.value || "",
      status: document.getElementById("mfTokStatus")?.value || "1",
      allowedChannelIds: box
        ? [...box.querySelectorAll("input:checked")].map((i) => i.value)
        : [],
    };
  }

  global.MsgForms = {
    esc,
    channelEditorFields,
    wireChannelTypeToggle,
    readChannelForm,
    sendRelationHintHtml,
    externalsEditorHtml,
    wireExternalsEditor,
    readExternalsFromEditor,
    tokenScopeFields,
    readTokenForm,
    channelTypeOptions,
    beanOptions,
  };
})(window);
