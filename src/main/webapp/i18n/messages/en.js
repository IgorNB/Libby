import englishMessages from 'ra-language-english';
import treeEnglishMessages from 'ra-tree-language-english';
import {mergeTranslations} from 'react-admin';

import {tasks} from "../../app/screen/admin/tasks/i18n/en";
import {books} from "../../app/screen/admin/books/i18n/en";
import {comments} from "../../app/screen/admin/comments/i18n/en";
import {langs} from "../../app/screen/admin/langs/i18n/en";
import {users} from "../../app/screen/admin/users/i18n/en";

import {tasksPublic} from "../../app/screen/public/tasksPublic/i18n/en";
import {booksPublic} from "../../app/screen/public/booksPublic/i18n/en";
import {langsPublic} from "../../app/screen/public/langsPublic/i18n/en";
import {commentsPublic} from "../../app/screen/public/commentsPublic/i18n/en";


export const messages = {
    ...mergeTranslations(englishMessages, treeEnglishMessages),
    resources: {
        tasks: tasks,
        books: books,
        comments: comments,
        langs: langs,
        users: users,

        tasksPublic: tasksPublic,
        booksPublic: booksPublic,
        langsPublic: langsPublic,
        commentsPublic: commentsPublic,
    }
};

export default messages;
