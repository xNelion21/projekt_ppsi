import { createI18n } from 'vue-i18n';

import en from './locales/en.json';
import pl from './locales/pl.json';

const messages = {
    'en-US': en,
    'pl-PL': pl,
};

const savedLang = localStorage.getItem('userLanguage') || 'en-US';

const i18n = createI18n({
    legacy: false,
    locale: savedLang,
    fallbackLocale: 'en-US',
    messages,
});

export default i18n;