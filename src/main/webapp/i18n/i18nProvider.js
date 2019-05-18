import englishMessages from './messages/en';
import russianMessages from './messages/ru';

const messages = {
    ru: russianMessages,
    en: englishMessages,
};

export default locale => messages[locale];
