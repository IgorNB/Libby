import russianMessages from 'ra-language-russian';
import treeEnglishMessages from 'ra-tree-language-english';
import {mergeTranslations} from 'react-admin';

import {tasks} from "../../app/screen/admin/tasks/i18n/ru";
import {books} from "../../app/screen/admin/books/i18n/ru";
import {comments} from "../../app/screen/admin/comments/i18n/ru";
import {langs} from "../../app/screen/admin/langs/i18n/ru";
import {users} from "../../app/screen/admin/users/i18n/ru";

import {tasksPublic} from "../../app/screen/public/tasksPublic/i18n/ru";
import {booksPublic} from "../../app/screen/public/booksPublic/i18n/ru";
import {langsPublic} from "../../app/screen/public/langsPublic/i18n/ru";
import {commentsPublic} from "../../app/screen/public/commentsPublic/i18n/ru";


export const messages = {
    ...mergeTranslations(russianMessages, treeEnglishMessages),
    resources: {
        tasks: tasks,
        books: books,
        langs: langs,
        comments: comments,
        users: users,

        tasksPublic: tasksPublic,
        booksPublic: booksPublic,
        langsPublic: langsPublic,
        commentsPublic: commentsPublic,
    },
};

export default messages;
