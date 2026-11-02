(function () {
  const PATHS = {
    home: '<path d="M4 11 12 4l8 7"/><path d="M6 10.5V20h12V10.5"/>',
    grid: '<rect x="4" y="4" width="6.5" height="6.5" rx="1"/><rect x="13.5" y="4" width="6.5" height="6.5" rx="1"/><rect x="4" y="13.5" width="6.5" height="6.5" rx="1"/><rect x="13.5" y="13.5" width="6.5" height="6.5" rx="1"/>',
    pulse: '<path d="M3 12h4l2.2-5 3.6 10L15 12h6"/>',
    folder: '<path d="M3 7h6l2 2h10v10H3z"/>',
    bell: '<path d="M6 16V11a6 6 0 0 1 12 0v5l1.5 2h-15z"/><path d="M10 19a2 2 0 0 0 4 0"/>',
    user: '<circle cx="12" cy="8" r="3"/><path d="M5.5 19.5c1.2-3 3.2-4.5 6.5-4.5s5.3 1.5 6.5 4.5"/>',
    shield: '<path d="M12 3.5 5.5 6.2v5.6c0 3.7 2.6 6.2 6.5 7.7 3.9-1.5 6.5-4 6.5-7.7V6.2z"/>',
    building: '<path d="M4 20V9l8-4 8 4v11"/><path d="M9 20v-5h6v5"/>',
    list: '<path d="M8 7h12M8 12h12M8 17h8"/><path d="M4 7h.01M4 12h.01M4 17h.01"/>',
    book: '<path d="M5 5.5h8a3.5 3.5 0 0 1 3.5 3.5V20H8a3 3 0 0 0-3 3z"/><path d="M5 5.5V20"/>',
    sliders: '<path d="M4 8h16M4 16h16"/><circle cx="9" cy="8" r="2"/><circle cx="15" cy="16" r="2"/>',
    clock: '<circle cx="12" cy="12" r="8"/><path d="M12 8v4l3 2"/>',
    file: '<path d="M7 3.5h7l4 4V20.5H7z"/><path d="M14 3.5V8h4.5"/>',
    tag: '<path d="M4 12.2V5h7.2L20 13.8 12.2 21.6z"/><circle cx="8.2" cy="8.2" r="1"/>',
    inbox: '<path d="M4 13h4.2L10 16h4l1.8-3H20v7H4z"/><path d="M4 13 6.2 4.5h11.6L20 13"/>',
    send: '<path d="M4 12 20 4.5 14 20l-2.2-6.2z"/>',
    users: '<circle cx="9" cy="8" r="2.6"/><circle cx="16" cy="9" r="2"/><path d="M4 19c.8-2.6 2.6-4 5-4s4.2 1.4 5 4"/>'
  }
  const ICONS = {
    home: 'home', profile: 'user', user: 'user', role: 'shield', dept: 'building',
    menu: 'list', dict: 'book', config: 'sliders', job: 'clock', jobLog: 'file',
    oper: 'file', loginlog: 'file', file: 'file', classify: 'tag', inbox: 'inbox',
    send: 'send', sendItem: 'list', group: 'users', channel: 'pulse'
  }
  const AREAS = [
    { id: 'home', label: '首页', icon: 'home', title: '开始' },
    { id: 'system', label: '系统', icon: 'grid', title: '系统管理' },
    { id: 'monitor', label: '监控', icon: 'pulse', title: '系统监控' },
    { id: 'files', label: '文件', icon: 'folder', title: '文件' },
    { id: 'msg', label: '消息', icon: 'bell', title: '消息中心' }
  ]
  const GROUPS = [
    { area: 'home', name: '开始', items: [{ id: 'home', label: '首页' }, { id: 'profile', label: '个人中心' }] },
    { area: 'system', name: '系统管理', items: [
      { id: 'user', label: '用户管理' }, { id: 'role', label: '角色管理' }, { id: 'dept', label: '部门管理' },
      { id: 'menu', label: '菜单管理' }, { id: 'dict', label: '字典管理' }, { id: 'config', label: '参数设置' }
    ]},
    { area: 'monitor', name: '系统监控', items: [
      { id: 'job', label: '定时任务' }, { id: 'jobLog', label: '调度日志' },
      { id: 'oper', label: '操作日志' }, { id: 'loginlog', label: '登录日志' }
    ]},
    { area: 'files', name: '文件', items: [
      { id: 'file', label: '文件管理' }, { id: 'classify', label: '文件分类' }
    ]},
    { area: 'msg', name: '消息中心', items: [
      { id: 'inbox', label: '收件箱' }, { id: 'send', label: '发送记录' }, { id: 'sendItem', label: '发送明细' },
      { id: 'group', label: '消息群组' }, { id: 'channel', label: '发送渠道' }
    ]}
  ]
  const PAGES = {
    home: { title: '工作台', kind: 'home' },
    profile: { title: '个人中心', kind: 'profile' },
    user: { title: '用户管理', add: '用户名', columns: ['用户名', '昵称', '部门', '角色', '状态'], rows: [
      ['admin', '系统管理员', '研发中心', '超级管理员', '正常'],
      ['auditor', '审计账号', '审计部', '审计员', '正常'],
      ['ops', '运维账号', '运维组', '运维', '停用'],
      ['editor', '内容账号', '市场部', '普通角色', '正常']
    ]},
    role: { title: '角色管理', add: '角色名', columns: ['角色名', '权限字符', '状态'], rows: [
      ['超级管理员', 'admin', '正常'], ['审计员', 'auditor', '正常'], ['运维', 'ops', '停用']
    ]},
    dept: { title: '部门管理', add: '部门名', columns: ['部门', '上级', '状态'], rows: [
      ['研发中心', '总公司', '正常'], ['审计部', '总公司', '正常'], ['运维组', '研发中心', '正常'], ['市场部', '总公司', '正常']
    ]},
    menu: { title: '菜单管理', add: '菜单名', columns: ['菜单', '上级', '路径'], rows: [
      ['用户管理', '系统管理', 'system/user'], ['收件箱', '消息中心', 'msg/inbox'], ['定时任务', '系统监控', 'job/job']
    ]},
    dict: { title: '字典管理', add: '字典名', columns: ['字典', '类型', '状态'], rows: [
      ['用户状态', 'sys_normal_disable', '正常'], ['通知类型', 'msg_type', '正常']
    ]},
    config: { title: '参数设置', add: '参数名', columns: ['参数', '键', '值'], rows: [
      ['主框架页', 'sys.index.skinName', 'skin-blue'], ['初始密码', 'sys.user.initPassword', '123456']
    ]},
    job: { title: '定时任务', add: '任务名', columns: ['任务', '表达式', '状态'], rows: [
      ['清理日志', '0 0 1 * * ?', '正常'], ['消息重试', '0 0/5 * * * ?', '正常'], ['统计汇总', '0 0 2 * * ?', '停用']
    ]},
    jobLog: { title: '调度日志', columns: ['任务', '结果', '时间'], rows: [
      ['清理日志', '成功', '今天 01:00'], ['消息重试', '失败', '今天 10:05']
    ]},
    oper: { title: '操作日志', columns: ['操作', '模块', '结果'], rows: [
      ['新增用户', '用户管理', '成功'], ['修改角色', '角色管理', '成功']
    ]},
    loginlog: { title: '登录日志', columns: ['账号', '结果', '时间'], rows: [
      ['admin', '成功', '刚刚'], ['ops', '失败', '昨天']
    ]},
    file: { title: '文件管理', add: '文件名', columns: ['文件', '分类', '大小'], rows: [
      ['说明.pdf', '文档', '1.2 MB'], ['封面.png', '图片', '340 KB']
    ]},
    classify: { title: '文件分类', add: '分类名', columns: ['分类', '数量'], rows: [
      ['图片', '18'], ['文档', '6']
    ]},
    inbox: { title: '收件箱', columns: ['标题', '类型', '时间'], rows: [
      ['登录环境有变化', '系统', '刚刚'], ['还有 2 件事没完成', '待办', '1 小时前']
    ]},
    send: { title: '发送记录', columns: ['主题', '渠道', '状态'], rows: [
      ['验证码', '短信', '成功'], ['待办提醒', '站内信', '成功']
    ]},
    sendItem: { title: '发送明细', columns: ['接收人', '渠道', '状态'], rows: [
      ['admin', '站内信', '成功'], ['ops', '短信', '失败']
    ]},
    group: { title: '消息群组', add: '群组名', columns: ['群组', '人数'], rows: [
      ['全体成员', '128'], ['运维组', '6']
    ]},
    channel: { title: '发送渠道', add: '渠道名', columns: ['渠道', '状态'], rows: [
      ['站内信', '正常'], ['短信', '正常'], ['邮件', '停用']
    ]}
  }

  const AREA_OF = {}
  GROUPS.forEach((group) => group.items.forEach((item) => { AREA_OF[item.id] = group.area }))

  const state = { current: 'home', status: '', editing: null }
  const listeners = []

  function emit() {
    const status = document.getElementById('status')
    if (status) status.textContent = state.status
    listeners.forEach((fn) => fn())
  }
  function icon(name) {
    const key = PATHS[name] ? name : (ICONS[name] || 'file')
    return '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">' + PATHS[key] + '</svg>'
  }
  function esc(value) {
    return String(value).replace(/[&<>"]/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[char]))
  }
  function pill(text) {
    if (text === '正常' || text === '成功') return '<span class="pill ok">' + text + '</span>'
    if (text === '停用') return '<span class="pill off">' + text + '</span>'
    if (text === '失败') return '<span class="pill bad">' + text + '</span>'
    return null
  }
  function page() { return PAGES[state.current] }
  function query() {
    const input = document.getElementById('menu-q')
    return input ? input.value.trim() : ''
  }
  function groupsFor(areaId) {
    const q = query()
    const source = q
      ? GROUPS.map((group) => ({ ...group, items: group.items.filter((item) => item.label.includes(q) || group.name.includes(q)) })).filter((group) => group.items.length)
      : GROUPS.filter((group) => !areaId || group.area === areaId)
    return source
  }
  function open(id) {
    state.current = id
    state.status = ''
    state.editing = null
    emit()
  }
  function openArea(areaId) {
    const group = GROUPS.find((item) => item.area === areaId)
    open(group.items[0].id)
  }
  function listHTML() {
    const current = page()
    const head = current.columns.map((name) => '<th scope="col">' + esc(name) + '</th>').join('')
    const body = current.rows.map((row, index) => {
      const cells = row.map((cell) => '<td>' + (pill(cell) || esc(cell)) + '</td>').join('')
      const actions = current.add ? '<td><button type="button" class="btn btn-text" data-edit="' + index + '">修改</button><button type="button" class="btn btn-danger" data-remove="' + index + '">删除</button></td>' : ''
      return '<tr>' + cells + actions + '</tr>'
    }).join('')
    const actionHead = current.add ? '<th scope="col">操作</th>' : ''
    const addBtn = current.add ? '<button type="button" class="btn btn-primary" id="add-btn">新增</button>' : ''
    return '<div class="page-head"><h1>' + esc(current.title) + '</h1><p>' + current.rows.length + ' 条</p><div class="spacer"></div>' + addBtn + '</div><div class="table-wrap"><table><thead><tr>' + head + actionHead + '</tr></thead><tbody>' + body + '</tbody></table></div>'
  }
  function showModal(editing) {
    const current = page()
    state.editing = editing
    document.getElementById('add-title').textContent = (editing == null ? '新增' : '修改') + current.title.replace('管理', '')
    document.getElementById('add-label').textContent = current.add
    document.getElementById('add-name').value = editing == null ? '' : current.rows[editing][0]
    document.getElementById('add-error').hidden = true
    document.getElementById('modal').hidden = false
    document.getElementById('add-name').focus()
  }

  function bind() {
    document.addEventListener('click', (event) => {
      const openEl = event.target.closest('[data-open]')
      if (openEl) { open(openEl.dataset.open); return }
      const areaEl = event.target.closest('[data-area]')
      if (areaEl) { openArea(areaEl.dataset.area); return }
      const remove = event.target.closest('[data-remove]')
      if (remove && page().rows) {
        page().rows.splice(Number(remove.dataset.remove), 1)
        state.status = '已删除'
        emit()
        return
      }
      const edit = event.target.closest('[data-edit]')
      if (edit) { showModal(Number(edit.dataset.edit)); return }
      if (event.target.closest('#add-btn')) { showModal(null); return }
      if (event.target.closest('#go-home')) { open('home'); return }
      if (event.target.closest('#go-profile')) {
        document.getElementById('user-menu').hidden = true
        open('profile')
        return
      }
      if (event.target.closest('#logout')) {
        document.getElementById('user-menu').hidden = true
        document.getElementById('app').hidden = true
        document.getElementById('gate').hidden = false
        return
      }
      const who = document.getElementById('who')
      const menu = document.getElementById('user-menu')
      if (event.target.closest('#who')) {
        menu.hidden = !menu.hidden
        who.setAttribute('aria-expanded', menu.hidden ? 'false' : 'true')
        return
      }
      if (!event.target.closest('.user')) {
        menu.hidden = true
        who.setAttribute('aria-expanded', 'false')
      }
    })
    const search = document.getElementById('menu-q')
    if (search) search.addEventListener('input', emit)
    document.getElementById('add-form').addEventListener('submit', (event) => {
      event.preventDefault()
      const input = document.getElementById('add-name')
      const name = input.value.trim()
      if (!name) {
        document.getElementById('add-error').hidden = false
        input.focus()
        return
      }
      const current = page()
      if (state.editing == null) {
        current.rows.unshift(current.columns.map((_, index) => index === 0 ? name : (current.columns[index] === '状态' ? '正常' : '—')))
        state.status = '已新增 ' + name
      } else {
        current.rows[state.editing][0] = name
        state.status = '已修改 ' + name
        state.editing = null
      }
      document.getElementById('modal').hidden = true
      emit()
    })
    document.getElementById('modal').addEventListener('click', (event) => {
      if (event.target.closest('[data-close]')) document.getElementById('modal').hidden = true
    })
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape') document.getElementById('modal').hidden = true
    })
    document.getElementById('login-form').addEventListener('submit', (event) => {
      event.preventDefault()
      const user = document.getElementById('login-user')
      const pass = document.getElementById('login-pass')
      const summary = document.getElementById('login-errors')
      const list = document.getElementById('login-error-list')
      const misses = []
      ;[['login-user', user, '请填写账号'], ['login-pass', pass, '请填写密码']].forEach(([id, field, text]) => {
        field.removeAttribute('aria-invalid')
        document.getElementById(id + '-error').hidden = true
        if (!field.value.trim()) misses.push([id, text])
      })
      if (misses.length) {
        list.replaceChildren()
        misses.forEach(([id, text]) => {
          document.getElementById(id).setAttribute('aria-invalid', 'true')
          document.getElementById(id + '-error').hidden = false
          const li = document.createElement('li')
          const a = document.createElement('a')
          a.href = '#' + id
          a.textContent = text
          li.append(a)
          list.append(li)
        })
        summary.hidden = false
        summary.focus()
        return
      }
      summary.hidden = true
      document.getElementById('gate').hidden = true
      document.getElementById('app').hidden = false
      open('home')
      state.status = '已进入'
      emit()
    })
  }

  window.QB = {
    AREAS, GROUPS, state, icon, esc, pill, page, query, groupsFor, open, openArea, listHTML, bind,
    on(fn) { listeners.push(fn) },
    area() { return AREA_OF[state.current] }
  }
})()
