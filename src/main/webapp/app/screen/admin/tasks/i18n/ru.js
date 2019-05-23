export const tasks = {
    name: 'Задача |||| Задачи',
    validation: {
        urlFormat: "Проверьте формат url. Пример: http://google.com",
        yearFormat: "Проверите формат года (от 0 до 3000). Пример: 2019",
        ISBN13Format: "Проверьте формат ISBN 13. Пример: 9783836221191"
    },
    list: {},
    edit: {
        title: 'Задача',
        taskFieldGroup: 'Задача',
        bookFieldGroup: 'Книга',
    },
    action: {
        bookLink: "Перейти к связанной книге",
        ESCALATED: "Отправить на исправление",
        SUBMITTED: "Отправить",
        APPROVED: "Одобрить"
    },
    status: {

        ESCALATED: "Ожидает исправления",
        SUBMITTED: "Отправлено",
        APPROVED: "Одобрено",
        INIT: "Создание"
    },
    fields: {
        "id": "идентификатор",
        "version": "версия",
        "book": {
            "id": "book id",
            "version": "book version",
            "title": "книга"
        },
        "createdBy": {
            "id": "createdBy id",
            "version": "createdBy version",
            "name": "создано",
            "email": "email",
            "imageUrl": "createdBy imageUrl",
            "emailVerified": "createdBy emailVerified",
            "provider": "createdBy provider",
            "providerId": "createdBy providerId"
        },
        "lastUpdBy": {
            "id": "lastUpdBy id",
            "version": "lastUpdBy version",
            "name": "lastUpdBy name",
            "email": "lastUpdBy email",
            "imageUrl": "lastUpdBy imageUrl",
            "emailVerified": "lastUpdBy emailVerified",
            "provider": "lastUpdBy provider",
            "providerId": "lastUpdBy providerId"
        },
        "q": "Поиск",
        "command": "command",
        "availableCommands": [],
        "assignee": {
            "id": "assignee id",
            "version": "assignee version",
            "name": "исполнитель",
            "email": "assignee email",
            "imageUrl": "assignee imageUrl",
            "emailVerified": "assignee emailVerified",
            "provider": "assignee provider",
            "providerId": "assignee providerId"
        },
        "workflowStep": "статус",
        "bookName": "описание",
        "bookWork": "работа",
        "bookLang": {
            "id": "bookLang id",
            "version": "bookLang version",
            "createdBy": "bookLang createdBy",
            "lastUpdBy": "bookLang lastUpdBy",
            "q": "bookLang q",
            "code": "язык"
        },
        "bookIsbn": "ISBN",
        "bookIsbn13": "ISBN13",
        "bookAuthors": "авторы",
        "bookOriginalPublicationYear": "год",
        "bookOriginalTitle": "оригинальное название",
        "bookTitle": "название",
        "bookImageUrl": "bookImageUrl",
        "bookSmallImageUrl": "изображение"
    },
};

