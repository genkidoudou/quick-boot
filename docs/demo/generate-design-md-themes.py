# -*- coding: utf-8 -*-
"""从 awesome-design-md 的 DESIGN.md 生成 84 个主题预览页 + index.html。"""
from __future__ import annotations

import html
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "awesome-design-md" / "design-md"
OUT = ROOT / "demo" / "design-md-themes"

# README 品类 → 目录名（与 collection 对齐；未列出的进 Other）
CATEGORIES: list[tuple[str, list[str]]] = [
    (
        "AI & LLM Platforms",
        [
            "claude",
            "cohere",
            "elevenlabs",
            "jobright",
            "minimax",
            "mistral.ai",
            "ollama",
            "opencode.ai",
            "replicate",
            "runwayml",
            "together.ai",
            "voltagent",
            "x.ai",
        ],
    ),
    (
        "Developer Tools & IDEs",
        ["cursor", "expo", "lovable", "raycast", "superhuman", "vercel", "warp"],
    ),
    (
        "Backend, Database & DevOps",
        [
            "clickhouse",
            "composio",
            "hashicorp",
            "mongodb",
            "oracle",
            "posthog",
            "sanity",
            "sentry",
            "supabase",
        ],
    ),
    (
        "Productivity & SaaS",
        ["cal", "intercom", "linear.app", "mintlify", "notion", "resend", "sap", "zapier"],
    ),
    (
        "Design & Creative Tools",
        ["airtable", "clay", "figma", "framer", "miro", "webflow"],
    ),
    (
        "Fintech & Crypto",
        [
            "binance",
            "coinbase",
            "kraken",
            "mastercard",
            "mercury",
            "nerdwallet",
            "revolut",
            "stripe",
            "wise",
        ],
    ),
    (
        "E-commerce & Retail",
        ["airbnb", "meta", "mydealz", "nike", "shopify", "starbucks"],
    ),
    (
        "Media & Consumer Tech",
        [
            "1win",
            "aljazeera",
            "apple",
            "dji",
            "hp",
            "ibm",
            "nvidia",
            "oura",
            "pinterest",
            "playstation",
            "spacex",
            "spotify",
            "theverge",
            "uber",
            "vodafone",
            "wired",
        ],
    ),
    (
        "Automotive",
        ["bmw", "bmw-m", "bugatti", "ferrari", "lamborghini", "renault", "tesla"],
    ),
    ("Retro Web", ["dell-1996", "nintendo-2001"]),
]


HEX = r"#(?:[0-9A-Fa-f]{3}|[0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})\b"


def parse_colors_from_yaml_block(block: str) -> dict[str, str]:
    colors: dict[str, str] = {}
    in_colors = False
    for line in block.splitlines():
        if re.match(r"^colors:\s*$", line):
            in_colors = True
            continue
        if in_colors:
            if re.match(r"^[a-zA-Z]", line):
                in_colors = False
                continue
            cm = re.match(
                rf'^\s{{2}}([A-Za-z0-9_-]+):\s*"?({HEX})"?',
                line,
            )
            if cm:
                colors[cm.group(1)] = cm.group(2)
    return colors


def parse_colors_from_markdown(text: str) -> dict[str, str]:
    """无 YAML 时：从「**Name** (`#hex`)」或「Name (`#hex`)」抽取。"""
    colors: dict[str, str] = {}
    # **Spotify Green** (`#1ed760`)
    for m in re.finditer(
        rf"\*\*([^*]+)\*\*\s*[：（(]\s*`?({HEX})`?",
        text,
    ):
        key = re.sub(r"[^A-Za-z0-9]+", "-", m.group(1).strip()).strip("-").lower() or "color"
        if key not in colors:
            colors[key] = m.group(2)
    # - Primary: `#3E6AE1` / Electric Blue (`#3E6AE1`)
    for m in re.finditer(
        rf"(?i)(?:primary|brand|accent|cta|action)[^\n#]{{0,40}}`?({HEX})`?",
        text,
    ):
        colors.setdefault("primary", m.group(1))
    for m in re.finditer(
        rf"(?i)(?:canvas|background|near-black|near black|page)[^\n#]{{0,40}}`?({HEX})`?",
        text,
    ):
        colors.setdefault("canvas", m.group(1))
    for m in re.finditer(
        rf"(?i)(?:ink|body text|heading|foreground)[^\n#]{{0,40}}`?({HEX})`?",
        text,
    ):
        colors.setdefault("ink", m.group(1))
    return colors


def parse_frontmatter(text: str) -> dict:
    data: dict = {"colors": {}, "fonts": [], "weights": [], "sizes": []}
    m = re.match(r"^---\r?\n(.*?)\r?\n---", text, re.S)
    if m:
        block = m.group(1)
        data["raw"] = block
        name_m = re.search(r"^name:\s*(.+)$", block, re.M)
        if name_m:
            data["name"] = name_m.group(1).strip().strip("\"'")
        desc_m = re.search(r"^description:\s*(.+)$", block, re.M)
        if desc_m:
            data["description"] = desc_m.group(1).strip().strip("\"'")
        data["colors"] = parse_colors_from_yaml_block(block)
        fonts = re.findall(r'fontFamily:\s*"?([^"\n#]+)"?', block)
        data["fonts"] = [f.strip().rstrip(",") for f in fonts if f.strip()]
        data["weights"] = [int(w) for w in re.findall(r"fontWeight:\s*(\d+)", block)[:8]]
        data["sizes"] = [float(s) for s in re.findall(r"fontSize:\s*([0-9.]+)px", block)[:12]]

    if len(data["colors"]) < 2:
        md_colors = parse_colors_from_markdown(text)
        for k, v in md_colors.items():
            data["colors"].setdefault(k, v)

    # 仍不够：扫全文前几个有语义的 hex
    if "primary" not in data["colors"]:
        for m in re.finditer(rf"(?i)(green|blue|red|orange|purple|accent|brand|cta).{{0,24}}`?({HEX})`?", text):
            data["colors"]["primary"] = m.group(2)
            break

    if not data["fonts"]:
        fm = re.search(r"(?i)font family[^\n]*?:\s*`?([A-Za-z][A-Za-z0-9 ,\-]+)", text)
        if fm:
            data["fonts"] = [fm.group(1).strip()]
        else:
            for fam in ("Circular", "Geist", "Inter", "SF Pro", "Universal Sans", "Sohne", "Open Sans", "Roboto"):
                if fam.lower() in text.lower():
                    data["fonts"] = [f'{fam}, system-ui, "PingFang SC", "Microsoft YaHei", sans-serif']
                    break

    # Overview 第一段作 description 兜底
    if not data.get("description"):
        om = re.search(r"## (?:1\.\s*)?(?:Visual Theme|Overview)[^\n]*\n+(.+?)(?:\n\n|\n## )", text, re.S)
        if om:
            para = re.sub(r"\s+", " ", om.group(1)).strip()
            data["description"] = para[:220] + ("..." if len(para) > 220 else "")

    return data


def pick_color(colors: dict, *keys: str, default: str = "#111111") -> str:
    lower = {k.lower(): v for k, v in colors.items()}
    for k in keys:
        if k in colors:
            return colors[k]
        if k.lower() in lower:
            return lower[k.lower()]
    for k in keys:
        kl = k.lower()
        for ck, v in lower.items():
            if kl in ck or ck in kl:
                return v
    # brand-ish names from markdown labels
    for hint in ("primary", "brand", "accent", "cta", "action", "green", "blue", "orange", "purple", "red"):
        for ck, v in lower.items():
            if hint in ck and luminance(v) < 0.92:
                return v
    for v in colors.values():
        if luminance(v) < 0.85:
            return v
    if colors:
        return next(iter(colors.values()))
    return default


def luminance(hex_color: str) -> float:
    h = hex_color.lstrip("#")
    if len(h) == 3:
        h = "".join(c * 2 for c in h)
    if len(h) != 6:
        return 0.5
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255


def soft_mix(hex_color: str, toward: str = "#ffffff", amount: float = 0.85) -> str:
    def rgb(hx: str):
        h = hx.lstrip("#")
        if len(h) == 3:
            h = "".join(c * 2 for c in h)
        return int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)

    try:
        r1, g1, b1 = rgb(hex_color)
        r2, g2, b2 = rgb(toward)
    except Exception:
        return toward
    r = round(r1 + (r2 - r1) * amount)
    g = round(g1 + (g2 - g1) * amount)
    b = round(b1 + (b2 - b1) * amount)
    return f"#{r:02x}{g:02x}{b:02x}"


def resolve_tokens(colors: dict) -> dict:
    primary = pick_color(
        colors,
        "primary",
        "brand",
        "accent",
        "link",
        "action",
        "cta",
        default="#409EFF",
    )
    canvas = pick_color(
        colors,
        "canvas",
        "canvas-soft",
        "canvas-soft-2",
        "bg",
        "background",
        "surface",
        "paper",
        default="#ffffff",
    )
    # prefer darkest ink-like for text
    ink_candidates = []
    for k, v in colors.items():
        lk = k.lower()
        if any(x in lk for x in ("ink", "body", "text", "foreground", "fg", "black", "mute")):
            ink_candidates.append((lk, v))
    ink = pick_color(colors, "ink", "body", "text", "foreground", "fg", default="#111111")
    if ink_candidates:
        # darkest by luminance
        ink = min((v for _, v in ink_candidates), key=luminance)

    mute = pick_color(colors, "mute", "ink-mute", "ink-mute-2", "body", "secondary", default="#666666")
    hairline = pick_color(colors, "hairline", "border", "line", "divider", default="#e5e5e5")
    surface = pick_color(
        colors,
        "canvas-soft",
        "canvas-soft-2",
        "surface",
        "card",
        "panel",
        default=soft_mix(canvas, "#f5f5f5", 0.5) if luminance(canvas) > 0.5 else soft_mix(canvas, "#ffffff", 0.08),
    )
    on_primary = pick_color(colors, "on-primary", "onPrimary", default="#ffffff")
    if abs(luminance(primary) - luminance(on_primary)) < 0.2:
        on_primary = "#ffffff" if luminance(primary) < 0.55 else "#111111"

    dark_ui = luminance(canvas) < 0.35
    return {
        "primary": primary,
        "canvas": canvas,
        "surface": surface,
        "ink": ink,
        "mute": mute,
        "hairline": hairline,
        "on_primary": on_primary,
        "soft": soft_mix(primary, "#ffffff" if not dark_ui else canvas, 0.82 if not dark_ui else 0.75),
        "dark_ui": dark_ui,
    }


def display_name(slug: str, meta: dict) -> str:
    raw = meta.get("name") or slug
    raw = re.sub(r"-Inspired-design-analysis$", "", raw, flags=re.I)
    raw = raw.replace("-", " ").replace("_", " ")
    # prefer slug title for short label
    pretty = {
        "linear.app": "Linear",
        "mistral.ai": "Mistral AI",
        "together.ai": "Together AI",
        "opencode.ai": "OpenCode AI",
        "x.ai": "xAI",
        "bmw-m": "BMW M",
        "dell-1996": "Dell (1996)",
        "nintendo-2001": "Nintendo (2001)",
        "theverge": "The Verge",
        "runwayml": "Runway",
        "cal": "Cal.com",
        "1win": "1win",
        "aljazeera": "Al Jazeera",
    }
    return pretty.get(slug, slug.replace(".", " ").replace("-", " ").title())


def font_stack(meta: dict) -> str:
    fonts = meta.get("fonts") or []
    if not fonts:
        return 'system-ui, -apple-system, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif'
    # take first family list, keep as CSS
    first = fonts[0]
    # ensure fallbacks
    if "sans-serif" not in first and "serif" not in first and "mono" not in first:
        first = first + ', system-ui, "PingFang SC", "Microsoft YaHei", sans-serif'
    return first


def display_weight(meta: dict) -> int:
    ws = meta.get("weights") or [600]
    return ws[0]


def display_size(meta: dict) -> int:
    sizes = meta.get("sizes") or [40]
    # clamp for preview
    s = max(sizes) if sizes else 40
    return int(min(max(s, 28), 56))


def page_html(slug: str, meta: dict, tokens: dict, desc: str) -> str:
    name = display_name(slug, meta)
    t = tokens
    font = font_stack(meta)
    dw = display_weight(meta)
    ds = display_size(meta)
    # show up to 10 color swatches from design
    swatches = list(meta.get("colors", {}).items())[:12]
    swatch_html = "".join(
        f'<div class="sw"><i style="background:{html.escape(v)}"></i><span>{html.escape(k)}<br>{html.escape(v)}</span></div>'
        for k, v in swatches
    )
    esc = html.escape
    return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>{esc(name)} · DESIGN.md → Quick UI</title>
  <style>
    :root {{
      --primary: {t['primary']};
      --on-primary: {t['on_primary']};
      --canvas: {t['canvas']};
      --surface: {t['surface']};
      --ink: {t['ink']};
      --mute: {t['mute']};
      --hairline: {t['hairline']};
      --soft: {t['soft']};
      --font: {font};
    }}
    * {{ box-sizing: border-box; }}
    html, body {{ margin: 0; min-height: 100%; }}
    body {{
      background: var(--canvas);
      color: var(--ink);
      font-family: var(--font);
      font-size: 15px;
      line-height: 1.5;
    }}
    button, input {{ font: inherit; color: inherit; }}
    button {{ cursor: pointer; }}
    a {{ color: var(--primary); }}
    .topbar {{
      display: flex; align-items: center; gap: 16px;
      height: 56px; padding: 0 20px;
      border-bottom: 1px solid var(--hairline);
      background: var(--surface);
    }}
    .brand {{ font-weight: {dw}; letter-spacing: -0.02em; }}
    .brand i {{
      display: inline-grid; place-items: center;
      width: 22px; height: 22px; margin-right: 8px;
      border-radius: 6px; background: var(--primary); color: var(--on-primary);
      font-style: normal; font-size: 12px; vertical-align: -3px;
    }}
    .nav {{ display: flex; gap: 4px; margin-left: 12px; }}
    .nav a {{
      padding: 6px 10px; border-radius: 8px; text-decoration: none; color: var(--mute);
    }}
    .nav a.on {{ background: var(--soft); color: var(--primary); }}
    .back {{ margin-left: auto; color: var(--mute); text-decoration: none; font-size: 13px; }}
    .shell {{
      display: grid; grid-template-columns: 220px 1fr; min-height: calc(100vh - 56px);
    }}
    .side {{
      padding: 16px 10px; border-right: 1px solid var(--hairline); background: var(--surface);
    }}
    .side button {{
      display: block; width: 100%; text-align: left;
      height: 36px; padding: 0 12px; margin: 2px 0;
      border: 0; border-radius: 8px; background: transparent; color: var(--ink);
    }}
    .side button.on {{ background: var(--soft); color: var(--primary); font-weight: 600; }}
    .main {{ padding: 28px 32px 48px; }}
    .kicker {{ color: var(--mute); font-size: 13px; margin: 0 0 8px; }}
    h1 {{
      margin: 0; font-size: {ds}px; font-weight: {dw};
      letter-spacing: -0.03em; line-height: 1.1; max-width: 18em;
    }}
    .lead {{ margin: 12px 0 0; color: var(--mute); max-width: 48em; }}
    .actions {{ display: flex; flex-wrap: wrap; gap: 10px; margin-top: 22px; }}
    .btn {{
      height: 40px; padding: 0 18px; border: 0; border-radius: 999px;
      background: var(--primary); color: var(--on-primary); font-weight: 600;
    }}
    .btn-line {{
      height: 40px; padding: 0 18px; border: 1px solid var(--hairline); border-radius: 999px;
      background: transparent; color: var(--ink);
    }}
    .grid {{
      display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-top: 28px;
    }}
    .card {{
      background: var(--surface); border: 1px solid var(--hairline);
      border-radius: 14px; padding: 16px;
    }}
    .card b {{ display: block; font-weight: 600; }}
    .card span {{ display: block; margin-top: 6px; color: var(--mute); font-size: 13px; }}
    .panel {{
      margin-top: 24px; background: var(--surface); border: 1px solid var(--hairline);
      border-radius: 14px; overflow: hidden;
    }}
    .panel-h {{
      display: flex; justify-content: space-between; align-items: center;
      padding: 12px 16px; border-bottom: 1px solid var(--hairline);
    }}
    table {{ width: 100%; border-collapse: collapse; }}
    th, td {{ padding: 12px 16px; text-align: left; border-bottom: 1px solid var(--hairline); font-weight: 400; }}
    th {{ color: var(--mute); font-size: 13px; }}
    .link {{ border: 0; background: none; padding: 0; color: var(--primary); }}
    .swatches {{
      display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
      gap: 10px; margin-top: 28px;
    }}
    .sw {{
      border: 1px solid var(--hairline); border-radius: 10px; overflow: hidden; background: var(--surface);
    }}
    .sw i {{ display: block; height: 44px; }}
    .sw span {{ display: block; padding: 8px 10px; font-size: 11px; color: var(--mute); line-height: 1.35; }}
    .src {{ margin-top: 28px; color: var(--mute); font-size: 12px; }}
    @media (max-width: 900px) {{
      .shell {{ grid-template-columns: 1fr; }}
      .side {{ display: flex; flex-wrap: wrap; gap: 4px; border-right: 0; border-bottom: 1px solid var(--hairline); }}
      .side button {{ width: auto; }}
      .grid {{ grid-template-columns: 1fr; }}
      h1 {{ font-size: 32px; }}
    }}
  </style>
</head>
<body>
  <header class="topbar">
    <div class="brand"><i>Q</i>{esc(name)}</div>
    <nav class="nav" aria-label="预览导航">
      <a class="on" href="#">首页</a>
      <a href="#users">用户</a>
      <a href="#tokens">色板</a>
    </nav>
    <a class="back" href="../index.html">全部主题</a>
  </header>
  <div class="shell">
    <aside class="side" aria-label="侧栏">
      <button class="on" type="button">首页</button>
      <button type="button">消息</button>
      <button type="button">文件</button>
      <button type="button">用户</button>
      <button type="button">定时任务</button>
    </aside>
    <main class="main">
      <p class="kicker">DESIGN.md · {esc(slug)}</p>
      <h1>林晓，2 条消息还没看</h1>
      <p class="lead">{esc(desc)}</p>
      <div class="actions">
        <button class="btn" type="button">看消息</button>
        <button class="btn-line" type="button">新建用户</button>
      </div>
      <div class="grid">
        <div class="card"><b>本周五前提交考勤</b><span>人事通知 · 今天 09:12</span></div>
        <div class="card"><b>密码将在 7 天后过期</b><span>账号安全 · 今天 08:02</span></div>
        <div class="card"><b>合同扫描件</b><span>文件 · 1.2 MB</span></div>
      </div>
      <section class="panel" id="users">
        <div class="panel-h"><b>用户</b><button class="btn" type="button" style="height:32px;padding:0 12px;font-size:13px">新建用户</button></div>
        <table>
          <thead><tr><th>用户名</th><th>昵称</th><th>部门</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr><td>linxiao</td><td>林晓</td><td>产品部</td><td>正常</td><td><button class="link" type="button">修改</button></td></tr>
            <tr><td>zhouqi</td><td>周启</td><td>研发部</td><td>停用</td><td><button class="link" type="button">修改</button></td></tr>
            <tr><td>chenke</td><td>陈可</td><td>设计部</td><td>正常</td><td><button class="link" type="button">修改</button></td></tr>
          </tbody>
        </table>
      </section>
      <h2 id="tokens" style="margin:32px 0 0;font-size:16px;font-weight:600">DESIGN 色板</h2>
      <div class="swatches">{swatch_html or "<p class='lead'>未解析到 colors</p>"}</div>
      <p class="src">Token 来自 docs/awesome-design-md/design-md/{esc(slug)}/DESIGN.md。主色 {esc(t['primary'])} · 画布 {esc(t['canvas'])} · 墨色 {esc(t['ink'])}</p>
    </main>
  </div>
</body>
</html>
"""


def index_html(items: list[dict], by_cat: list[tuple[str, list[dict]]]) -> str:
    sections = []
    for cat, rows in by_cat:
        if not rows:
            continue
        cards = []
        for it in rows:
            cards.append(
                f"""<a class="card" href="themes/{html.escape(it['slug'])}.html">
  <span class="dot" style="background:{html.escape(it['primary'])}"></span>
  <b>{html.escape(it['name'])}</b>
  <span>{html.escape(it['slug'])}</span>
</a>"""
            )
        sections.append(
            f"<section><h2>{html.escape(cat)}</h2><div class='grid'>{''.join(cards)}</div></section>"
        )
    return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Quick UI · DESIGN.md 主题预览（{len(items)}）</title>
  <style>
    * {{ box-sizing: border-box; }}
    body {{
      margin: 0; background: #f3f3f3; color: #111;
      font-family: "PingFang SC", "Microsoft YaHei", system-ui, sans-serif;
      font-size: 15px; line-height: 1.5;
    }}
    .wrap {{ width: min(1100px, calc(100% - 32px)); margin: 0 auto; padding: 28px 0 64px; }}
    h1 {{ margin: 0; font-size: 28px; letter-spacing: -0.03em; }}
    .lead {{ margin: 8px 0 0; color: #555; max-width: 60ch; }}
    .find {{
      width: min(420px, 100%); height: 40px; margin-top: 18px; padding: 0 12px;
      border: 1px solid #ddd; border-radius: 8px; background: #fff;
    }}
    section {{ margin-top: 28px; }}
    h2 {{ margin: 0 0 12px; font-size: 16px; }}
    .grid {{
      display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 10px;
    }}
    .card {{
      display: grid; grid-template-columns: 16px 1fr; gap: 2px 10px; align-items: center;
      padding: 12px; border-radius: 10px; background: #fff; border: 1px solid #e6e6e6;
      text-decoration: none; color: inherit;
    }}
    .card:hover {{ border-color: #111; }}
    .dot {{ width: 16px; height: 16px; border-radius: 4px; grid-row: 1 / span 2; }}
    .card b {{ font-size: 14px; }}
    .card span {{ color: #777; font-size: 12px; }}
    .hidden {{ display: none !important; }}
  </style>
</head>
<body>
  <div class="wrap">
    <h1>DESIGN.md → Quick UI 主题</h1>
    <p class="lead">共 {len(items)} 套。每套一页，色板与字体尽量取自对应 DESIGN.md。点卡片打开预览。</p>
    <input class="find" id="q" type="search" placeholder="搜索品牌，如 stripe、linear、vercel">
    {''.join(sections)}
  </div>
  <script>
    const q = document.getElementById('q')
    q.addEventListener('input', () => {{
      const v = q.value.trim().toLowerCase()
      document.querySelectorAll('.card').forEach((el) => {{
        const t = el.textContent.toLowerCase()
        el.classList.toggle('hidden', v && !t.includes(v))
      }})
      document.querySelectorAll('section').forEach((sec) => {{
        const any = [...sec.querySelectorAll('.card')].some((c) => !c.classList.contains('hidden'))
        sec.classList.toggle('hidden', !any)
      }})
    }})
  </script>
</body>
</html>
"""


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    themes_dir = OUT / "themes"
    themes_dir.mkdir(parents=True, exist_ok=True)

    items: list[dict] = []
    for folder in sorted(SRC.iterdir()):
        if not folder.is_dir():
            continue
        design = folder / "DESIGN.md"
        if not design.exists():
            print("skip no DESIGN", folder.name)
            continue
        text = design.read_text(encoding="utf-8")
        meta = parse_frontmatter(text)
        tokens = resolve_tokens(meta.get("colors") or {})
        slug = folder.name
        name = display_name(slug, meta)
        desc = meta.get("description") or f"{name} inspired design language for Quick UI preview."
        # shorten description
        if len(desc) > 220:
            desc = desc[:217] + "..."
        page = page_html(slug, meta, tokens, desc)
        (themes_dir / f"{slug}.html").write_text(page, encoding="utf-8")
        items.append(
            {
                "slug": slug,
                "name": name,
                "primary": tokens["primary"],
                "canvas": tokens["canvas"],
            }
        )
        print("ok", slug, tokens["primary"])

    slug_set = {i["slug"] for i in items}
    by_cat: list[tuple[str, list[dict]]] = []
    used = set()
    for cat, slugs in CATEGORIES:
        rows = [next(i for i in items if i["slug"] == s) for s in slugs if s in slug_set]
        used.update(s for s in slugs if s in slug_set)
        by_cat.append((cat, rows))
    other = [i for i in items if i["slug"] not in used]
    if other:
        by_cat.append(("Other", other))

    (OUT / "index.html").write_text(index_html(items, by_cat), encoding="utf-8")
    print("wrote", len(items), "themes + index ->", OUT)


if __name__ == "__main__":
    main()
