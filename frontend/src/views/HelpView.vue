<template>
  <div class="help-view-container">
    <h1>{{ t('help.title') }}</h1>
    <p class="intro">{{ t('help.intro') }}</p>

    <section class="help-section">
      <h3>{{ t('help.sections.gettingStarted.title') }}</h3>
      <p>{{ t('help.sections.gettingStarted.content') }}</p>
    </section>

    <section class="help-section">
      <h3>{{ t('help.sections.addTask.title') }}</h3>
      <p>{{ t('help.sections.addTask.content') }}</p>
    </section>

    <section class="help-section">
      <h3>{{ t('help.sections.navigation.title') }}</h3>
      <ul>
        <li><strong>{{ t('help.sections.navigation.inbox') }}</strong>: {{ t('help.sections.navigation.inboxDesc') }}</li>
        <li><strong>{{ t('help.sections.navigation.today') }}</strong>: {{ t('help.sections.navigation.todayDesc') }}</li>
        <li><strong>{{ t('help.sections.navigation.upcoming') }}</strong>: {{ t('help.sections.navigation.upcomingDesc') }}</li>
        <li><strong>{{ t('help.sections.navigation.completed') }}</strong>: {{ t('help.sections.navigation.completedDesc') }}</li>
        <li><strong>{{ t('help.sections.navigation.calendar') }}</strong>: {{ t('help.sections.navigation.calendarDesc') }}</li>
      </ul>
    </section>

    <section class="help-section">
      <h3>{{ t('help.sections.settings.title') }}</h3>
      <p>{{ t('help.sections.settings.content') }}</p>
    </section>

    <p class="text-muted mt-5">{{ t('help.contactUs') }} <a href="mailto:support@taskify.com">support@taskify.com</a>.</p>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
</script>

<style scoped>
.help-view-container {
  max-width: 800px;
  margin: 3rem auto;
  padding: 2rem 2.5rem;
  background-color: var(--color-background-soft);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  color: var(--color-text);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h1 {
  text-align: center;
  color: var(--color-heading);
  margin-bottom: 1.8rem;
  font-weight: 700;
  font-size: 2.6rem;
  letter-spacing: 0.04em;
}

.intro {
  max-width: 600px;
  margin: 0 auto 3rem;
  font-size: 1.15rem;
  line-height: 1.8;
  color: var(--color-text-mute);
  text-align: center;
}

.help-section {
  margin-bottom: 2.5rem;
  padding-bottom: 1.8rem;
  border-bottom: 1px solid var(--color-border);
}

.help-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

h3 {
  color: var(--color-accent-dark);
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.7rem;
  font-weight: 600;
  border-left: 4px solid var(--color-accent);
  padding-left: 12px;
}

p {
  line-height: 1.75;
  margin-bottom: 1rem;
  font-size: 1.05rem;
}

ul {
  padding-left: 25px;
  margin-bottom: 1rem;
}

li {
  margin-bottom: 0.6rem;
  line-height: 1.6;
  font-size: 1.05rem;
}

strong {
  color: var(--color-accent);
}

.text-muted {
  color: var(--color-text-mute) !important;
  text-align: center;
  font-size: 0.95rem;
  margin-top: 4rem;
}

a {
  color: var(--color-accent);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.25s ease;
}

a:hover {
  text-decoration: underline;
  color: var(--color-accent-dark);
}
</style>
