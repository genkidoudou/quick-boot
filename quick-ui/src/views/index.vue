<template>
  <div class="app-container home">
    <section class="qb-media-surface home-hero no-blur" aria-label="欢迎">
      <p class="qb-on-media-muted home-hero__eyebrow">QuickBoot</p>
      <h1 class="qb-on-media home-hero__title">欢迎进入 {{ appConfig.loginTitle }}</h1>
      <p class="qb-on-media-muted home-hero__sub">基于 Spring Boot + Vue 3 的快速开发平台</p>
    </section>

    <div class="home-grid">
      <article class="qb-banner-card">
        <div class="qb-banner-card__head">
          <h2 class="home-card-title">认证与客户端</h2>
          <p class="home-card-desc">OAuth 客户端、签名校验与授权类型统一管理</p>
        </div>
        <div class="qb-banner-card__dock">
          <span class="qb-badge-soft is-info">系统</span>
          <span class="num">HMAC</span>
        </div>
      </article>

      <article class="qb-card-colorful" style="--qb-card-accent-rgb: 0, 122, 255">
        <h2 class="home-card-title">消息通道</h2>
        <p class="home-card-desc">邮件、Webhook、机器人渠道的彩色配置摘要入口</p>
        <div class="home-card-meta">
          <span class="qb-badge-soft is-success">可用</span>
        </div>
      </article>

      <article class="qb-ops-card has-media home-ops" :style="{ backgroundImage: opsBg }">
        <h2 class="home-card-title qb-on-media">运维概览</h2>
        <p class="qb-on-media-muted home-card-desc">任务、日志与监控面板沿用影像底图运维卡原型</p>
      </article>

      <article class="qb-card-shell home-settings">
        <h2 class="home-card-title">分组设置</h2>
        <div class="app-grouped-list">
          <div class="app-grouped-list__item">
            <span>明暗与玻璃</span>
            <span class="text-muted-foreground text-sm">布局设置</span>
          </div>
          <div class="app-grouped-list__item">
            <span>壁纸与画质</span>
            <span class="text-muted-foreground text-sm">仅玻璃态</span>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup name="Index">
import { appConfig } from '@/config/env'

const opsBg =
  'linear-gradient(135deg, rgba(11,19,34,0.2), rgba(11,19,34,0.55)), radial-gradient(circle at 20% 20%, #8d51f9, #0b1322 70%)'
</script>

<style scoped lang="scss">
.home {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-bottom: var(--qb-bottom-clearance);
}

.home-hero {
  min-height: 220px;
  padding: 48px 28px;
  background-image:
    radial-gradient(ellipse at 30% 20%, rgba(141, 81, 249, 0.55), transparent 50%),
    linear-gradient(145deg, #182235, #0b1322 60%, #1a2740);
}

.home-hero__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.04em;
}

.home-hero__title {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 600;
}

.home-hero__sub {
  margin: 0;
  font-size: 14px;
}

.home-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 900px) {
  .home-grid {
    grid-template-columns: 1fr;
  }
}

.home-card-title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: inherit;
}

.home-card-desc {
  margin: 0;
  font-size: 13px;
  line-height: 1.5;
  color: var(--qb-muted-fg);
}

.home-ops .home-card-desc {
  color: var(--qb-on-media-muted);
}

.home-card-meta {
  margin-top: 14px;
}

.home-settings {
  padding: 0;
  overflow: hidden;
}

.home-settings .home-card-title {
  padding: 16px 18px 0;
  color: var(--qb-fg);
}

.home-settings .app-grouped-list {
  border: none;
  border-radius: 0;
  box-shadow: none;
  margin-top: 8px;
}
</style>
